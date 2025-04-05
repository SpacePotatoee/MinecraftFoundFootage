#include veil:camera

layout(location = 0) in vec3 Position;
layout(location = 1) in vec3 Color;

void main() {
    vec3 pos = Position;

    gl_Position = VeilCamera.ProjMat * VeilCamera.ViewMat * vec4(pos + vec3(-1871, 31, 657), 1.0);
}