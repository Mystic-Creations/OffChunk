package net.mysticcreations.offchunk.offload;

import java.nio.FloatBuffer;

public class NoiseGen implements GpuChunkGen.GpuNoiseProvider {
    private final ComputeShader shader;

    public NoiseGen() {
        this.shader = new ComputeShader(16, 16, "noise.comp");
    }

    @Override
    public float[] generateHeightMap(int baseX, int baseZ) {
        shader.dispatch(baseX, baseZ, 0.01f);
        FloatBuffer buffer = shader.readTextureData();
        float[] heightmap = new float[16 * 16];

        for (int i = 0; i < heightmap.length; i++) {
            heightmap[i] = buffer.get(i * 4);
        }

        return heightmap;
    }
}