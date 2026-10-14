package com.calibermc.caliberlib.assets;

import com.calibermc.caliberlib.assets.client.ClientAssets;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * Generates every family of a family table with the source mods' jars, and
 * writes the client and data files out for comparison with a release. Runs
 * only when given them: {@code gradlew :common:test --tests '*AllFamiliesTest'
 * -Pfamilies=<table.json> -PresourceJars=<jar>:<jar>... -PfamiliesOut=<dir>}.
 */
@EnabledIfSystemProperty(named = "caliberlib.families", matches = ".+")
class AllFamiliesTest {

	@Test
	void generateEveryFamily() throws Exception {
		Fixtures.bootstrap();
		FamilyTable table = new FamilyTable(Path.of(System.getProperty("caliberlib.families")));
		List<Path> jars = Arrays.stream(System.getProperty("caliberlib.resourceJars", "").split(File.pathSeparator))
				.filter(s -> !s.isBlank()).map(Path::of).toList();
		JarResources resources = new JarResources(jars);
		GameView game = table.game(resources);
		long start = System.nanoTime();
		Map<String, String> client = EngineGoldenTest.read(ClientAssets.index(table.families, resources, game));
		Map<String, String> data = EngineGoldenTest.read(AssetEngine.dataIndex(table.families, game));
		long ms = (System.nanoTime() - start) / 1_000_000;
		Path out = Path.of(System.getProperty("caliberlib.familiesOut", "build/all-families"));
		Files.createDirectories(out);
		for (Map.Entry<String, Map<String, String>> e : Map.of("client", client, "data", data).entrySet()) {
			JsonObject json = new JsonObject();
			e.getValue().forEach(json::addProperty);
			Files.writeString(out.resolve(e.getKey() + ".json"),
					new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create().toJson(json) + "\n");
		}
		Files.writeString(out.resolve("summary.txt"), String.format("families %d%nclient files %d%ndata files %d%nms %d%n",
				table.families.size(), client.size(), data.size(), ms));
		assertFalse(client.isEmpty());
	}
}
