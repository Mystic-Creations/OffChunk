package net.mysticcreations.offchunk;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.mysticcreations.offchunk.offload.NoiseGen;

public class OffChunkClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(ClientCommandManager.literal("testgpu")
                .executes(context -> {
                    NoiseGen gpu = new NoiseGen();
                    float[] heightmap = gpu.generateHeightmap(0, 0, 16, 16, 0.01f);
                    float first = heightmap.length > 0 ? heightmap[0] : -1;

                    MinecraftClient.getInstance().inGameHud.getChatHud()
                        .addMessage(Text.literal("GPU height[0] = " + first));
                    return 1;
                }));
        });
    }
}