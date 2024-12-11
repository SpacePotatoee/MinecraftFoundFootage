#include veil:light

layout(location = 0) in vec3 Position;
layout(location = 1) in vec4 Color;
layout(location = 2) in vec2 UV0;

uniform sampler2D Sampler2;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform vec3 ChunkOffset;
uniform mat3 NormalMat;
uniform mat4 orthoMatrix;
uniform mat4 viewRix;

out vec4 vertexColor;
out vec2 texCoord0;
out vec4 viewPos;

const vec4 plane = vec4(0,-1,0,9);

vec3 distort(in vec3 shadowPosition) {
    const float bias0 = 0.95;
    const float bias1 = 1.0 - bias0;

    float factorDistance = length(shadowPosition.xy);

    float distortFactor = factorDistance * bias0 + bias1;

    return shadowPosition * vec3(vec2(1.0 / distortFactor), 0.2);
}

void main() {
    vec3 pos = Position + ChunkOffset;

    viewPos = orthoMatrix * viewRix * vec4(pos, 1.0);
    gl_Position = ProjMat * ModelViewMat * vec4(pos, 1.0);

    gl_ClipDistance[0] = dot(vec4(Position, 1.0), plane);

    vertexColor = Color;
    texCoord0 = UV0;

    gl_Position.xyz = distort(gl_Position.xyz);
}