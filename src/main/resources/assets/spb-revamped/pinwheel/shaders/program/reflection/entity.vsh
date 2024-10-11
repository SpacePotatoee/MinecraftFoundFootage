#include veil:light
#include veil:deferred_utils

layout(location = 0) in vec3 Position;
layout(location = 1) in vec4 Color;
layout(location = 2) in vec2 UV0;
layout(location = 3) in ivec2 UV1;

uniform sampler2D Sampler1;
uniform sampler2D Sampler2;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform mat3 NormalMat;

out vec4 vertexColor;
out vec2 texCoord0;
out vec4 overlayColor;

const vec4 plane = vec4(0, 1, 0, -21.01);

void main() {
    gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);

    gl_ClipDistance[0] = dot(vec4(playerSpaceToWorldSpace(Position), 1.0), plane);

    vertexColor = Color;
    texCoord0 = UV0;
    overlayColor = texelFetch(Sampler1, UV1, 0);
}