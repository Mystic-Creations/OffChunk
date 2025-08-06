package net.mysticcreations.offchunk.offload;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL45;

public class NoiseGen {
    private final int program;

    public NoiseGen() {
        this.program = ComputeShaderLoader.loadComputeShader("noise.comp");
    }

    public float[] generateHeightmap(int chunkX, int chunkZ, int width, int height, float scale) {
        int image = GL45.glGenTextures();
        GL45.glBindTexture(GL45.GL_TEXTURE_2D, image);
        GL45.glTexStorage2D(GL45.GL_TEXTURE_2D, 1, GL45.GL_RGBA32F, width, height);
        GL45.glBindImageTexture(0, image, 0, false, 0, GL45.GL_WRITE_ONLY, GL45.GL_RGBA32F);

        GL45.glUseProgram(program);

        int locStart = GL45.glGetUniformLocation(program, "chunkStartPos");
        GL45.glUniform2i(locStart, chunkX, chunkZ);

        int locScale = GL45.glGetUniformLocation(program, "scale");
        GL45.glUniform1f(locScale, scale);

        GL45.glDispatchCompute(width / 16, height / 16, 1);
        GL45.glMemoryBarrier(GL45.GL_SHADER_IMAGE_ACCESS_BARRIER_BIT);

        FloatBuffer out = BufferUtils.createFloatBuffer(width * height * 4);
        GL45.glGetTextureImage(image, 0, GL45.GL_RGBA, GL45.GL_FLOAT, out);

        float[] heightmap = new float[width * height];
        for (int i = 0; i < heightmap.length; i++) {
            heightmap[i] = out.get(i * 4); 
        }

        GL45.glDeleteTextures(image);
        GL45.glUseProgram(0);

        return heightmap;
    }
}