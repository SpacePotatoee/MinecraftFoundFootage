#include veil:material
#include veil:deferred_buffers
#include veil:deferred_utils
#include veil:camera
#include veil:blend
#include spb-revamped:accurateuv

uniform sampler2D Sampler0;
uniform sampler2D Sampler1;
uniform sampler2D Sampler3;

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

    vec3 dir = Pos;
    vec3 worldNormal = abs(viewToWorldSpaceDirection(normal));

    vec4 color = vec4(0.0);
    vec4 normalMap = vec4(0.0);
    float dist = 0.0;
    vec3 pos = vec3(0.0);
    vec3 texCoords = vec3(0.0);
    float heightMapDepth = 0.0;
    for(int i = 1; i <= MaxSteps; i++){
        pos = worldPos + dir * dist;

        texCoords = vec3((pos.zx * worldNormal.g) + (vec2(pos.x, -pos.y) * worldNormal.b) + (-pos.zy * worldNormal.r), dist);
        heightMapDepth = ((1.0 - texture(Sampler0, vec2(texCoords.x, -texCoords.y) * ZOOM).r) * 0.05);

        if(texCoords.z >= heightMapDepth){
            color = texture(Sampler1, vec2(texCoords.x, -texCoords.y) * ZOOM) * vertexColor;
            normalMap = texture(Sampler3, vec2(texCoords.x, -texCoords.y) * ZOOM) * 2.0 - 1.0;
            normalMap.rgb *= TBN;
            normalMap.r = normalMap.r;
            break;
        }

        dist += 0.001;
    }
//    color = texture(Sampler1, worldPos.xz * ZOOM);

    fragAlbedo = vec4(color.r, color.g * 0.99, color.b * 0.8, 1.0);
    fragNormal = vec4(normalMap.rgb, 1.0);
    fragMaterial = ivec4(BLOCK_SOLID, 0, 0, 1);
    fragLightSampler = vec4(texCoord2, 0.0, 1.0);
    fragLightMap = lightmapColor;
}


