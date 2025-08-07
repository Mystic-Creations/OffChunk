#version 430

layout (local_size_x = 16, local_size_y = 16) in;

layout (rgba32f, binding = 0) uniform image2D outImage;

void main() {
    ivec2 coord = ivec2(gl_GlobalInvocationID.xy);
    imageStore(outImage, coord, vec4(4.0, 4.0, 4.0, 4.0));
}