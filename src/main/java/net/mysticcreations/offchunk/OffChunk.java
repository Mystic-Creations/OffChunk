package net.mysticcreations.offchunk;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;
import net.mysticcreations.offchunk.offload.NoiseGen;

public class OffChunk implements ModInitializer {
    public static final String MOD_ID = "offchunk";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing OffChunk");

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("testgpu").executes(context -> {
                NoiseGen gpu = new NoiseGen();
                float[] heightmap = gpu.generateHeightmap(0, 0, 16, 16, 0.01f);
                float first = heightmap.length > 0 ? heightmap[0] : -1;

                context.getSource().sendFeedback(() -> Text.literal("GPU height[0] = " + first), false);
                return 1;
            }));
        });
    }
}