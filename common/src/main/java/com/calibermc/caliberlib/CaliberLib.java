package com.calibermc.caliberlib;

import com.calibermc.caliberlib.platform.Services;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** The loader-neutral start of the library. Each loader's entry point calls {@link #init()} once. */
public final class CaliberLib {

	public static final String MOD_ID = "caliberlib";
	public static final Logger LOGGER = LoggerFactory.getLogger("CaliberLib");

	private CaliberLib() {
	}

	public static void init() {
		LOGGER.info("Loading Caliber Lib on {}", Services.platform().loaderName());
	}
}
