package net.mysticcreations.offchunk.offload;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import org.lwjgl.opengl.GL45;

public class NoiseGen {
    private final int program;

    public NoiseGen() {
        this.program = ComputeShaderLoader.loadComputeShader("noise.comp");
    }

    public float[] generateHeightmap(int chunkX, int chunkZ, int width, int height, float scale) {
        int size = width * height;
        int bufferSize = size * Float.BYTES;

        int ssbo = GL45.glGenBuffers();
        GL45.glBindBuffer(GL45.GL_SHADER_STORAGE_BUFFER, ssbo);
        GL45.glBufferData(GL45.GL_SHADER_STORAGE_BUFFER, bufferSize, GL45.GL_DYNAMIC_COPY);
        GL45.glBindBufferBase(GL45.GL_SHADER_STORAGE_BUFFER, 0, ssbo);

        GL45.glUseProgram(program);

        int locChunkStart = GL45.glGetUniformLocation(program, "chunkStartPos");
        int locScale = GL45.glGetUniformLocation(program, "scale");
        int locWidth = GL45.glGetUniformLocation(program, "width");

        GL45.glUniform2i(locChunkStart, chunkX, chunkZ);
        GL45.glUniform1f(locScale, scale);
        GL45.glUniform1i(locWidth, width);

        GL45.glDispatchCompute((width + 15) / 16, (height + 15) / 16, 1);
        GL45.glMemoryBarrier(GL45.GL_SHADER_STORAGE_BARRIER_BIT);

        GL45.glBindBuffer(GL45.GL_SHADER_STORAGE_BUFFER, ssbo);
        ByteBuffer byteBuffer = GL45.glMapBuffer(GL45.GL_SHADER_STORAGE_BUFFER, GL45.GL_READ_ONLY);
        FloatBuffer floatBuffer = byteBuffer.asFloatBuffer();

        float[] heightmap = new float[size];
        floatBuffer.get(heightmap);

        GL45.glUnmapBuffer(GL45.GL_SHADER_STORAGE_BUFFER);
        GL45.glDeleteBuffers(ssbo);
        GL45.glUseProgram(0);

        return heightmap;
    }
}