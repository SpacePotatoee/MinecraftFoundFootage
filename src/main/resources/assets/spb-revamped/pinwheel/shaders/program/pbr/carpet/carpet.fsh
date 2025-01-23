#include veil:material
#include veil:deferred_buffers
#include veil:deferred_utils
#include veil:camera
#include veil:blend
#include spb-revamped:accurateuv

uniform sampler2D Sampler0;
uniform sampler2D Sampler1;

uniform vec4 ColorModulator;

in vec4 vertexColor;
in vec2 texCoord0;
in vec2 texCoord2;
in vec4 lightmapColor;
in vec3 normal;
in vec3 worldPos;
in vec3 Pos;
in mat3 TBN;

const int MaxSteps = 75;
const float ZOOM = 0.5;

void main() {

    vec4 color = texture(Sampler0, worldPos.xz * ZOOM) * vertexColor;
    vec4 normalMap = (texture(Sampler1, worldPos.xz * ZOOM) * 2.0 - 1.0);
    normalMap.g = normalMap.g;
    normalMap.rgb *= TBN;

    fragAlbedo = vec4(color.rgb * vec3(1), 1.0);
    fragNormal = vec4(normalMap.rgb, 1.0);
    fragMaterial = ivec4(BLOCK_SOLID, 0, 0, 1);
    fragLightSampler = vec4(texCoord2, 0.0, 1.0);
    fragLightMap = lightmapColor;
}


