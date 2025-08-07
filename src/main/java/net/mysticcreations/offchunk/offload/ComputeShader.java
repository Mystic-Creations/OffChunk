package net.mysticcreations.offchunk.offload;

import java.io.IOException;
import java.io.InputStream;
import java.nio.FloatBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glDeleteTextures;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glGetTexImage;
import static org.lwjgl.opengl.GL15.GL_WRITE_ONLY;
import static org.lwjgl.opengl.GL20.GL_COMPILE_STATUS;
import static org.lwjgl.opengl.GL20.GL_LINK_STATUS;
import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL20.glDeleteProgram;
import static org.lwjgl.opengl.GL20.glDeleteShader;
import static org.lwjgl.opengl.GL20.glGetProgramInfoLog;
import static org.lwjgl.opengl.GL20.glGetProgrami;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;
import static org.lwjgl.opengl.GL20.glGetShaderi;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glShaderSource;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL30.GL_RGBA32F;
import static org.lwjgl.opengl.GL42.GL_SHADER_IMAGE_ACCESS_BARRIER_BIT;
import static org.lwjgl.opengl.GL42.glBindImageTexture;
import static org.lwjgl.opengl.GL42.glMemoryBarrier;
import static org.lwjgl.opengl.GL42.glTexStorage2D;
import static org.lwjgl.opengl.GL43.GL_COMPUTE_SHADER;
import static org.lwjgl.opengl.GL43.glDispatchCompute;

public class ComputeShader {
    private final int width, height;
    private final int textureID;
    private final int programID;

    public ComputeShader(int width, int height, String shaderFileName) {
        this.width = width;
        this.height = height;
        this.programID = loadComputeShader(shaderFileName);
        this.textureID = createTexture(width, height);
    }

    private int loadComputeShader(String shaderFileName) {
        String shaderSource = readShaderFromClasspath(shaderFileName);
        int shader = glCreateShader(GL_COMPUTE_SHADER);
        glShaderSource(shader, shaderSource);
        glCompileShader(shader);

        if (glGetShaderi(shader, GL_COMPILE_STATUS) == 0)
            throw new RuntimeException("Shader compile error: " + glGetShaderInfoLog(shader));

        int program = glCreateProgram();
        glAttachShader(program, shader);
        glLinkProgram(program);

        if (glGetProgrami(program, GL_LINK_STATUS) == 0)
            throw new RuntimeException("Shader link error: " + glGetProgramInfoLog(program));

        glDeleteShader(shader);
        return program;
    }

    private int createTexture(int width, int height) {
        int tex = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, tex);
        glTexStorage2D(GL_TEXTURE_2D, 1, GL_RGBA32F, width, height);
        glBindTexture(GL_TEXTURE_2D, 0);
        return tex;
    }

    public void dispatch() {
        glUseProgram(programID);
        glBindImageTexture(0, textureID, 0, false, 0, GL_WRITE_ONLY, GL_RGBA32F);
        glDispatchCompute((int) Math.ceil(width / 16.0), (int) Math.ceil(height / 16.0), 1);
        glMemoryBarrier(GL_SHADER_IMAGE_ACCESS_BARRIER_BIT);
    }

    public FloatBuffer readTextureData() {
        glBindTexture(GL_TEXTURE_2D, textureID);
        FloatBuffer buffer = BufferUtils.createFloatBuffer(width * height * 4);
        glGetTexImage(GL_TEXTURE_2D, 0, GL_RGBA, GL_FLOAT, buffer);
        return buffer;
    }

    public void cleanup() {
        glDeleteTextures(textureID);
        glDeleteProgram(programID);
    }

    private String readShaderFromClasspath(String path) {
        try (InputStream in = ComputeShader.class.getResourceAsStream("/assets/offchunk/shaders/" + path)) {
            if (in == null) throw new IOException("Shader not found: " + path);
            Scanner scanner = new Scanner(in, StandardCharsets.UTF_8).useDelimiter("\\A");
            return scanner.hasNext() ? scanner.next() : "";
        } catch (IOException e) {
            throw new RuntimeException("Shader read failed: " + path, e);
        }
    }
}