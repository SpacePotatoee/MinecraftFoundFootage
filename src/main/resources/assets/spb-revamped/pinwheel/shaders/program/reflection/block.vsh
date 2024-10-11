#include veil:light
#include veil:deferred_utils

layout(location = 0) in vec3 Position;
layout(location = 1) in vec4 Color;
layout(location = 2) in vec2 UV0;

uniform sampler2D Sampler2;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform vec3 ChunkOffset;
uniform mat3 NormalMat;

out vec4 vertexColor;
out vec2 texCoord0;

const vec4 plane = vec4(0, 1, 0, -21.01);

void main() {
    vec3 pos = Position + ChunkOffset;
    gl_Position = ProjMat * ModelViewMat * vec4(pos, 1.0);

    gl_ClipDistance[0] = dot(vec4(playerSpaceToWorldSpace(pos), 1.0), plane);

    vertexColor = Color;
    texCoord0 = UV0;

}