#version 460
#include veil:camera

layout(location = 0) in vec3 Position;
layout(location = 1) in vec3 Normal;

layout (std430, binding = 0) buffer MyBuffer {
    vec3 position[];
} myBuffer;

uniform float GameTime;

out vec3 localPos;
out vec3 normal;


void main() {
    vec3 pos = Position;

    vec3 cameraPos = VeilCamera.CameraPosition;

    float cameraX = mod(cameraPos.x, 1);
    float cameraZ = mod(cameraPos.z, 1);
    cameraPos.xz = vec2(cameraX, cameraZ);

    vec3 offset = myBuffer.position[gl_InstanceID];

    vec3 tempNormal = Normal;

    normal = tempNormal;

    localPos = (pos - cameraPos) + offset;

    gl_Position = VeilCamera.ProjMat * VeilCamera.ViewMat * vec4(localPos , 1.0);
}