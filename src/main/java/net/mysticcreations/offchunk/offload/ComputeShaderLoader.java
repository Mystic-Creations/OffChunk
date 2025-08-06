package net.mysticcreations.offchunk.offload;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import org.lwjgl.opengl.GL45;

public class ComputeShaderLoader {
    public static int loadComputeShader(String path) {
        int shader = GL45.glCreateShader(GL45.GL_COMPUTE_SHADER);
        String source = readShaderFile(path);
        GL45.glShaderSource(shader, source);
        GL45.glCompileShader(shader);

        int success = GL45.glGetShaderi(shader, GL45.GL_COMPILE_STATUS);
        if (success == GL45.GL_FALSE) {
            String log = GL45.glGetShaderInfoLog(shader);
            throw new RuntimeException("Failed to compile compute shader:\n" + log);
        }

        int program = GL45.glCreateProgram();
        GL45.glAttachShader(program, shader);
        GL45.glLinkProgram(program);

        int linkStatus = GL45.glGetProgrami(program, GL45.GL_LINK_STATUS);
        if (linkStatus == GL45.GL_FALSE) {
            String log = GL45.glGetProgramInfoLog(program);
            throw new RuntimeException("Failed to link compute shader program:\n" + log);
        }

        GL45.glDeleteShader(shader);
        return program;
    }

    private static String readShaderFile(String path) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                ComputeShaderLoader.class.getClassLoader().getResourceAsStream("assets/offchunk/shaders/" + path),
                StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining("\n"));
        } catch (Exception e) {
            throw new RuntimeException("Failed to read shader file: " + path, e);
        }
    }
}