package com.calibermc.caliberlib.assets.client;

import com.mojang.math.Transformation;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * A Wavefront OBJ model as the roof templates use it: positions in block
 * units, faces of three or four corners with texture coordinates, and
 * materials whose {@code map_Kd} names a texture variable ({@code #side}).
 * Bakes to vanilla quads, the same way Forge's OBJ loader does.
 */
public final class ObjModel {

	private record Corner(int position, int uv, int normal) {
	}

	/**
	 * The template's loader options, with Forge's meanings.
	 *
	 * @param flipV           texture v runs from the bottom
	 * @param emissiveAmbient a material's ambient colour lights its faces, which then skip directional shading
	 * @param automaticCulling faces lying on the block's boundary are culled like a full face there
	 */
	public record Options(boolean flipV, boolean emissiveAmbient, boolean automaticCulling) {
	}

	/** Receives each baked quad with the side it is culled against, or null. */
	public interface QuadSink {
		void accept(BakedQuad quad, @Nullable Direction cull);
	}

	private record Face(String material, List<Corner> corners) {
	}

	private final List<Vector3f> positions = new ArrayList<>();
	private final List<float[]> uvs = new ArrayList<>();
	private final List<Vector3f> normals = new ArrayList<>();
	private final List<Face> faces = new ArrayList<>();
	private final Map<String, String> materials = new HashMap<>();
	private final Map<String, Float> ambient = new HashMap<>();

	private ObjModel() {
	}

	/** Parses the OBJ text and its MTL text. */
	public static ObjModel parse(String obj, String mtl) {
		ObjModel model = new ObjModel();
		String current = null;
		for (String raw : mtl.split("\n")) {
			String[] t = raw.trim().split("\\s+");
			if (t[0].equals("newmtl") && t.length > 1) {
				current = t[1];
			} else if (t[0].equals("map_Kd") && t.length > 1 && current != null) {
				model.materials.put(current, t[1]);
			} else if (t[0].equals("Ka") && t.length > 3 && current != null) {
				model.ambient.put(current, Float.parseFloat(t[1]) + Float.parseFloat(t[2]) + Float.parseFloat(t[3]));
			}
		}
		String material = null;
		for (String raw : obj.split("\n")) {
			String[] t = raw.trim().split("\\s+");
			switch (t[0]) {
				case "v" -> model.positions.add(new Vector3f(Float.parseFloat(t[1]), Float.parseFloat(t[2]), Float.parseFloat(t[3])));
				case "vt" -> model.uvs.add(new float[] {Float.parseFloat(t[1]), Float.parseFloat(t[2])});
				case "vn" -> model.normals.add(new Vector3f(Float.parseFloat(t[1]), Float.parseFloat(t[2]), Float.parseFloat(t[3])));
				case "usemtl" -> material = t[1];
				case "f" -> {
					List<Corner> corners = new ArrayList<>();
					for (int i = 1; i < t.length; i++) {
						String[] parts = t[i].split("/");
						corners.add(new Corner(Integer.parseInt(parts[0]) - 1,
								parts.length > 1 && !parts[1].isEmpty() ? Integer.parseInt(parts[1]) - 1 : -1,
								parts.length > 2 && !parts[2].isEmpty() ? Integer.parseInt(parts[2]) - 1 : -1));
					}
					if (corners.size() < 3 || corners.size() > 4) {
						throw new IllegalArgumentException("Faces must have 3 or 4 corners, not " + corners.size());
					}
					model.faces.add(new Face(material, corners));
				}
				default -> {
				}
			}
		}
		return model;
	}

	/** The texture variables the materials use, e.g. {@code #side}. */
	public List<String> textureVariables() {
		return materials.values().stream().distinct().toList();
	}

	/**
	 * Bakes every face. {@code sprites} gives the sprite of a texture
	 * variable; {@code rotation} turns the model about the block's centre.
	 */
	public void bake(Function<String, TextureAtlasSprite> sprites, Transformation rotation, Options options, QuadSink out) {
		Matrix4f matrix = rotation.getMatrix();
		boolean flipV = options.flipV();
		for (Face face : faces) {
			TextureAtlasSprite sprite = sprites.apply(materials.getOrDefault(face.material(), "#missing"));
			// Forge: the ambient colour, as a light level 0-15, turns shading off when it is above 0.
			int light = (int) (ambient.getOrDefault(face.material(), 0f) * 15 / 3.0f);
			boolean shade = !(options.emissiveAmbient() && light > 0);
			List<Corner> corners = new ArrayList<>(face.corners());
			if (corners.size() == 3) {
				corners.add(corners.get(2));
			}
			int[] vertices = new int[32];
			Vector3f[] moved = new Vector3f[4];
			for (int i = 0; i < 4; i++) {
				Corner c = corners.get(i);
				Vector3f p = positions.get(c.position());
				Vector4f v = matrix.transform(new Vector4f(p.x() - 0.5f, p.y() - 0.5f, p.z() - 0.5f, 1));
				moved[i] = new Vector3f(v.x() + 0.5f, v.y() + 0.5f, v.z() + 0.5f);
				float[] uv = c.uv() >= 0 ? uvs.get(c.uv()) : new float[] {0, 0};
				float u = uv[0];
				float vv = flipV ? 1 - uv[1] : uv[1];
				int o = i * 8;
				vertices[o] = Float.floatToRawIntBits(moved[i].x());
				vertices[o + 1] = Float.floatToRawIntBits(moved[i].y());
				vertices[o + 2] = Float.floatToRawIntBits(moved[i].z());
				vertices[o + 3] = -1;
				vertices[o + 4] = Float.floatToRawIntBits(sprite.getU(u * 16));
				vertices[o + 5] = Float.floatToRawIntBits(sprite.getV(vv * 16));
			}
			// As Forge: the face points where its first corner's normal points.
			Corner first = corners.get(0);
			Direction facing = first.normal() >= 0 ? facing(matrix, normals.get(first.normal())) : facing(moved);
			out.accept(new BakedQuad(vertices, -1, facing, sprite, shade), options.automaticCulling() ? cull(moved, facing) : null);
		}
	}

	/** The side a face is culled against: the block side it lies in and faces out of. */
	@Nullable
	private static Direction cull(Vector3f[] p, Direction facing) {
		float edge = facing.getAxisDirection() == Direction.AxisDirection.POSITIVE ? 1 : 0;
		for (Vector3f v : p) {
			if (Math.abs(facing.getAxis().choose(v.x(), v.y(), v.z()) - edge) >= 1.0E-5f) {
				return null;
			}
		}
		return facing;
	}

	/** The direction nearest a normal, turned with the model. */
	private static Direction facing(Matrix4f matrix, Vector3f normal) {
		Vector3f n = matrix.transformDirection(new Vector3f(normal));
		return Direction.getNearest(n.x(), n.y(), n.z());
	}

	/** The direction nearest the face's normal, from its corners. */
	private static Direction facing(Vector3f[] p) {
		Vector3f a = new Vector3f(p[2]).sub(p[0]);
		Vector3f b = new Vector3f(p[3]).sub(p[1]);
		Vector3f n = a.cross(b);
		return Direction.getNearest(n.x(), n.y(), n.z());
	}
}
