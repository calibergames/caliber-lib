package com.calibermc.caliberlib.compat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/** The compat log: written to the game log, and kept so tests can read it. */
public class CompatLog {

	static final Logger LOGGER = LoggerFactory.getLogger("CaliberLib/compat");

	private final List<String> lines = new ArrayList<>();
	private final boolean debug;

	public CompatLog(boolean debug) {
		this.debug = debug;
	}

	public void info(String message) {
		lines.add("INFO " + message);
		LOGGER.info(message);
	}

	public void warn(String message) {
		lines.add("WARN " + message);
		LOGGER.warn(message);
	}

	public void error(String message) {
		lines.add("ERROR " + message);
		LOGGER.error(message);
	}

	/** Shown only with {@code [compat] debug = true}. */
	public void debug(String message) {
		lines.add("DEBUG " + message);
		if (debug) {
			LOGGER.info(message);
		} else {
			LOGGER.debug(message);
		}
	}

	public List<String> lines() {
		return List.copyOf(lines);
	}
}
