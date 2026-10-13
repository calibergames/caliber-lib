package com.calibermc.caliberlib.assets.client;

import com.mojang.blaze3d.platform.NativeImage;

import java.io.IOException;
import java.io.InputStream;

/**
 * Makes a tall door's textures from a two-block door's: the door is
 * stretched to three blocks, then cut into bottom, middle and top. Each
 * animation frame is stretched on its own.
 */
final class ImageComposer {

	enum Part {
		BOTTOM, MIDDLE, TOP
	}

	private static volatile byte[] missing;

	private ImageComposer() {
	}

	/** Vanilla's missing-texture pattern (magenta and black squares), 16 by 16. */
	static byte[] missing() {
		byte[] bytes = missing;
		if (bytes == null) {
			try (NativeImage image = new NativeImage(16, 16, false)) {
				for (int y = 0; y < 16; y++) {
					for (int x = 0; x < 16; x++) {
						image.setPixelRGBA(x, y, (x < 8) != (y < 8) ? 0xFFF800F8 : 0xFF000000);
					}
				}
				bytes = missing = image.asByteArray();
			} catch (IOException e) {
				throw new java.io.UncheckedIOException(e);
			}
		}
		return bytes;
	}

	/** One part of the tall door, as PNG bytes. */
	static byte[] doorPart(InputStream bottomPng, InputStream topPng, Part part) throws IOException {
		try (NativeImage bottom = NativeImage.read(bottomPng); NativeImage top = NativeImage.read(topPng)) {
			int w = bottom.getWidth();
			if (top.getWidth() != w) {
				throw new IOException("Door halves differ in width: " + w + " and " + top.getWidth());
			}
			int frames = Math.max(1, Math.min(bottom.getHeight(), top.getHeight()) / w);
			try (NativeImage out = new NativeImage(w, w * frames, false)) {
				for (int f = 0; f < frames; f++) {
					for (int y = 0; y < w; y++) {
						// Row of the 3w-high door shown at this row of the part,
						// counted from the top of the door.
						int doorRow = (2 - part.ordinal()) * w + y;
						int sourceRow = doorRow * 2 / 3;
						NativeImage src = sourceRow < w ? top : bottom;
						int srcY = f * w + sourceRow % w;
						for (int x = 0; x < w; x++) {
							out.setPixelRGBA(x, f * w + y, src.getPixelRGBA(x, srcY));
						}
					}
				}
				return out.asByteArray();
			}
		}
	}

	/** The tall door's item icon: the door's icon, half again as tall and narrowed to fit. */
	static byte[] doorIcon(InputStream iconPng) throws IOException {
		try (NativeImage icon = NativeImage.read(iconPng)) {
			int w = icon.getWidth();
			int h = Math.min(icon.getHeight(), w);
			int minX = w, minY = h, maxX = -1, maxY = -1;
			for (int y = 0; y < h; y++) {
				for (int x = 0; x < w; x++) {
					if ((icon.getPixelRGBA(x, y) >>> 24) != 0) {
						minX = Math.min(minX, x);
						maxX = Math.max(maxX, x);
						minY = Math.min(minY, y);
						maxY = Math.max(maxY, y);
					}
				}
			}
			try (NativeImage out = new NativeImage(w, h, true)) {
				if (maxX >= 0) {
					int bw = maxX - minX + 1;
					int bh = maxY - minY + 1;
					double scale = Math.min(1.0, h / (bh * 1.5));
					int nw = Math.max(1, (int) Math.round(bw * scale));
					int nh = Math.max(1, (int) Math.round(bh * 1.5 * scale));
					int ox = (w - nw) / 2;
					int oy = (h - nh) / 2;
					for (int y = 0; y < nh; y++) {
						for (int x = 0; x < nw; x++) {
							int sx = minX + Math.min(bw - 1, x * bw / nw);
							int sy = minY + Math.min(bh - 1, y * bh / nh);
							out.setPixelRGBA(ox + x, oy + y, icon.getPixelRGBA(sx, sy));
						}
					}
				}
				return out.asByteArray();
			}
		}
	}
}
