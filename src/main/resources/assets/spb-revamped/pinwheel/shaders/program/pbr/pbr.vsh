#include veil:light
#include veil:deferred_utils

layout(location = 0) in vec3 Position;
layout(location = 1) in vec4 Color;
layout(location = 2) in vec2 UV0;
layout(location = 3) in ivec2 UV2;
layout(location = 4) in vec3 Normal;

uniform sampler2D Sampler2;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform vec3 ChunkOffset;
uniform mat3 NormalMat;

out vec4 VertexColor;
out vec2 TexCoord0;
out vec2 TexCoord2;
out vec4 LightmapColor;
out vec3 normall;
out vec2 WorldFacePos;
out vec3 WorldPos;
out vec3 position;

void main() {
    vec3 pos = Position + ChunkOffset;
    position = pos;
    gl_Position = vec4(pos, 1.0);

    VertexColor = Color;
    TexCoord0 = UV0;
    TexCoord2 = minecraft_sample_lightmap_coords(UV2);
    LightmapColor = pow(texture(Sampler2, TexCoord2), vec4(3));
    normall = NormalMat * Normal;

    vec3 worldNormal = abs(viewToWorldSpaceDirection(normall));
    WorldPos = pos;

    WorldFacePos = (Position.zx * worldNormal.y) + (Position.xy * worldNormal.z) + (vec2(-Position.z, -Position.y) * worldNormal.x);
}


