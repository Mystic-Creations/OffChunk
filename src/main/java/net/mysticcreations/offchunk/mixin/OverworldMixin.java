package net.mysticcreations.offchunk.mixin;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.level.LevelProperties;
import net.mysticcreations.offchunk.offload.GpuChunkGen;
import net.mysticcreations.offchunk.offload.GpuNoiseProviderImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ServerWorld.class)
public class OverworldMixin {

    @ModifyArg(
        method = "<init>",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/world/ServerWorld;<init>(Lnet/minecraft/server/MinecraftServer;Lnet/minecraft/server/world/WorkerExecutor;Lnet/minecraft/world/level/LevelProperties;Lnet/minecraft/registry/RegistryKey;Lnet/minecraft/world/dimension/DimensionType;Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/server/WorldGenerationProgressListener;ZJLnet/minecraft/world/gen/chunk/ChunkGenerator;Z)V"
        ),
        index = 9
    )
    private ChunkGenerator replaceChunkGenerator(ChunkGenerator original) {
        BiomeSource biomeSource = original.getBiomeSource();
        return new GpuChunkGen(biomeSource, new GpuNoiseProviderImpl());
    }
}