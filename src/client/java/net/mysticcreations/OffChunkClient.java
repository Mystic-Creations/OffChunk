package net.mysticcreations;

import net.fabricmc.api.ClientModInitializer;
import net.mysticcreations.gpu.ComputeShaderHelper;

import java.nio.FloatBuffer;

public class OffChunkClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		System.out.println("[OffChunk] Initializing compute shader...");

		ComputeShaderHelper shader = new ComputeShaderHelper(256, 256, "noise.comp");
		shader.dispatch();
		FloatBuffer data = shader.readTextureData();
		for (int i = 0; i < 5 * 4; i += 4) {
			float r = data.get(i);
			float g = data.get(i + 1);
			float b = data.get(i + 2);
			System.out.printf("Noise: %.3f %.3f %.3f%n", r, g, b);
		}
	}
}