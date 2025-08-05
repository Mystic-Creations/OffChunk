package net.mysticcreations.offchunk.gpu;

import com.mojang.serialization.Codec;

import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.chunk.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import net.minecraft.block.BlockState;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.noise.NoiseConfig;

public class GpuChunkGen extends ChunkGenerator {

    private final GpuNoiseProvider noiseProvider;

    public GpuChunkGen(BiomeSource settings, GpuNoiseProvider noiseProvider) {
        super(settings);
        this.noiseProvider = noiseProvider;
    }

    @Override
    public CompletableFuture<Chunk> populateNoise(Executor executor, Blender blender, NoiseConfig noiseConfig, StructureAccessor structureAccessor, Chunk chunk) {
        int baseX = chunk.getPos().getStartX();
        int baseZ = chunk.getPos().getStartZ();

        return CompletableFuture.supplyAsync(() -> {
            float[] heightmap = noiseProvider.generateHeightMap(baseX, baseZ);

            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    int height = 20 + Math.round(heightmap[z * 16 + x] * 80);
                    for (int y = 0; y <= height; y++) {
                        BlockPos pos = new BlockPos(baseX + x, y, baseZ + z);
                        if (y == height) {
                            chunk.setBlockState(pos, Blocks.GRASS_BLOCK.getDefaultState(), false);
                        } else if (y >= height - 4) {
                            chunk.setBlockState(pos, Blocks.DIRT.getDefaultState(), false);
                        } else {
                            chunk.setBlockState(pos, Blocks.STONE.getDefaultState(), false);
                        }
                    }
                }
            }

            return chunk;
        }, executor);
    }

    @Override
    public void buildSurface(ChunkRegion region, StructureAccessor structureAccessor, NoiseConfig noiseConfig, Chunk chunk) {
        int baseX = chunk.getPos().getStartX();
        int baseZ = chunk.getPos().getStartZ();

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                BlockPos pos = chunk.getHeightmapPos(Heightmap.Type.WORLD_SURFACE, new BlockPos(baseX + x, 0, baseZ + z));
                if (chunk.getBlockState(pos).isOf(Blocks.DIRT)) {
                    chunk.setBlockState(pos, Blocks.GRASS_BLOCK.getDefaultState(), false);
                }
            }
        }
    }

    @Override
    public void carve(ChunkRegion region, long seed, NoiseConfig noiseConfig, BiomeAccess biomeAccess, StructureAccessor accessor, Chunk chunk, GenerationStep.Carver carver) {
    }

    @Override
    public void populateEntities(ChunkRegion region) {
    }

    @Override
    public int getWorldHeight() {
        return 384;
    }

    @Override
    public int getSeaLevel() {
        return 63;
    }

    @Override
    public int getMinimumY() {
        return 0;
    }

    @Override
    public VerticalBlockSample getColumnSample(int x, int z, HeightLimitView world, NoiseConfig noiseConfig) {
        BlockPos.Mutable pos = new BlockPos.Mutable(x, 0, z);
        BlockState[] states = new BlockState[getWorldHeight()];
        for (int y = 0; y < states.length; y++) {
            pos.setY(y);
            states[y] = y < 60 ? Blocks.STONE.getDefaultState() : Blocks.AIR.getDefaultState();
        }
        return new VerticalBlockSample(0, states);
    }

    @Override
    public int getHeight(int x, int z, Heightmap.Type heightmap, HeightLimitView world, NoiseConfig noiseConfig) {
        return 80;
    }

    @Override
    public void getDebugHudText(List<String> info, NoiseConfig noiseConfig, BlockPos pos) {
        info.add("GpuChunkGen: GPU noise terrain");
    }

    @Override
    protected Codec<? extends ChunkGenerator> getCodec() {
        return null;
    }

    public interface GpuNoiseProvider {
        float[] generateHeightMap(int baseX, int baseZ);
    }
}