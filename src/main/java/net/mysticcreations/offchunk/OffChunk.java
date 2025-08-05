package net.mysticcreations.offchunk;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OffChunk implements ModInitializer {
    public static final String MOD_ID = "offchunk";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing OffChunk");
    }
}