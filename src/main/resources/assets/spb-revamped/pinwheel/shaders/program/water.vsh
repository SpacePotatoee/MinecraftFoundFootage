#line 0 1
#define MINECRAFT_LIGHT_POWER   (0.6)
#define MINECRAFT_AMBIENT_LIGHT (0.4)
#include veil:deferred_utils

uniform float GameTime;

vec4 minecraft_mix_light(vec3 lightDir0, vec3 lightDir1, vec3 normal, vec4 color) {
    lightDir0 = normalize(lightDir0);
    lightDir1 = normalize(lightDir1);
    float light0 = max(0.0, dot(lightDir0, normal));
    float light1 = max(0.0, dot(lightDir1, normal));
    float lightAccum = min(1.0, (light0 + light1) * MINECRAFT_LIGHT_POWER + MINECRAFT_AMBIENT_LIGHT);
    return vec4(color.rgb * lightAccum, color.a);
}

vec4 minecraft_mix_light(vec3 lightDir0, vec3 lightDir1, vec3 normal) {
    lightDir0 = normalize(lightDir0);
    lightDir1 = normalize(lightDir1);
    float light0 = max(0.0, dot(lightDir0, normal));
    float light1 = max(0.0, dot(lightDir1, normal));
    float lightAccum = min(1.0, (light0 + light1) * MINECRAFT_LIGHT_POWER + MINECRAFT_AMBIENT_LIGHT);
    return vec4(lightAccum, lightAccum, lightAccum, 1.0);
}

vec2 minecraft_sample_lightmap_coords(ivec2 uv) {
    return clamp(uv / 256.0, vec2(0.5 / 16.0), vec2(15.5 / 16.0));
}

vec4 minecraft_sample_lightmap(sampler2D lightMap, ivec2 uv) {
    return texture(lightMap, minecraft_sample_lightmap_coords(uv));
}

float attenuate_no_cusp(float distance, float radius) {
    float s = distance / radius;

    if (s >= 1.0) {
        return 0.0;
    }

    float oneMinusS = 1.0 - s;
    return oneMinusS * oneMinusS * oneMinusS;
}

#line 2 0

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


out vec4 vertexColor;
out vec2 texCoord0;
out vec2 texCoord2;
out vec4 lightmapColor;
out vec3 normal;

void main() {

    vec3 Pos = Position;


    vec3 pos = Pos + ChunkOffset;
    vec3 worldPos = playerSpaceToWorldSpace(pos);
    float octaves = 0.015;
    pos.y += octaves * sin(worldPos.x + GameTime*3000) - octaves;
    pos.y += octaves * cos(worldPos.z + GameTime*3000) - octaves;
    pos.y += octaves * sin(worldPos.x - GameTime*300) - octaves;
    pos.y += octaves * cos(worldPos.z + GameTime*5600) - octaves;
    pos.y += octaves * sin(worldPos.x + GameTime*2340) - octaves;
    pos.y += octaves * cos(worldPos.z - GameTime*4213) - octaves;

    gl_Position = ProjMat * ModelViewMat * vec4(pos, 1.0);

    vertexColor = Color;
    texCoord0 = UV0;
    texCoord2 = minecraft_sample_lightmap_coords(UV2);
    lightmapColor = texture(Sampler2, texCoord2);
    normal = NormalMat * Normal;
}


