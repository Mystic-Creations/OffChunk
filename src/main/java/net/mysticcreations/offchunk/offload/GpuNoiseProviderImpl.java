package net.mysticcreations.offchunk.offload;

public class GpuNoiseProviderImpl implements GpuChunkGen.GpuNoiseProvider {
    private final NoiseGen gpu;

    public GpuNoiseProviderImpl() {
        this.gpu = new NoiseGen();
    }

    @Override
    public float[] generateHeightMap(int baseX, int baseZ) {
        return gpu.generateHeightmap(baseX, baseZ, 16, 16, 0.01f);
    }
}