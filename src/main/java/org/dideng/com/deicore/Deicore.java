package org.dideng.com.deicore;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Deicore implements ModInitializer {
    public static final String MOD_ID = "deicore";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final String MOD_VERSION = "1.0.0-SNAPSHOT";
    @Override
    public void onInitialize() {
        LOGGER.info("deicore initialized successfully, version: {}",Deicore.MOD_VERSION);
    }

}
