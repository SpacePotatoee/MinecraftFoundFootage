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
uniform float atlasAspectRatio;


in vec4 vertexColor;
in vec2 texCoord0;
in vec2 texCoordOffset;
in vec2 texCoord2;
in vec4 lightmapColor;
in vec3 normal;
in vec3 worldPos;
in vec3 Pos;
in float Zoom;
flat in int Resolution;
flat in int Enableheight;
in float Depth;
in mat3 TBN;

const int MaxSteps = 75;

void main() {
    vec4 color = vec4(0.0);
    vec4 normalMap = vec4(0.0);
    float dist = 0.0;

    //If height is disabled
    if(Enableheight == 0){
        vec3 repWorldPos = mod(worldPos, Zoom) *1 / Zoom;
        vec2 faceUV = getAccurateUV(repWorldPos, normal);

        vec2 zoomedUV = faceUV;
        zoomedUV *= (0.00006101539 * Resolution) * vec2(2.0 * atlasAspectRatio, 2.0);
        zoomedUV += texCoordOffset;

        color = texture(Sampler0, zoomedUV) * vertexColor;

        normalMap = (texture(Sampler1, zoomedUV) * 2.0 - 1.0);
        normalMap.rgb *= TBN;
    } else {
        vec3 dir = normalize(Pos);
        vec3 worldNormal = abs(viewToWorldSpaceDirection(normal));


//        float dist = 0;
        vec3 pos = vec3(0.0);
        for(int i = 0; i < MaxSteps; i++) {
            pos = worldPos + dir * dist;

            vec3 texCoords = vec3((pos.zx * worldNormal.g) + (vec2(pos.x, -pos.y) * worldNormal.b) + (-pos.zy * worldNormal.r), dist);

            vec3 repeatedTexCoord = vec3(mod(texCoords.xy, Zoom)*1/Zoom, texCoords.z);

            vec2 zoomedUV2 = repeatedTexCoord.xy;
            zoomedUV2 *= (0.00006101539 * Resolution) * vec2(2.0 * atlasAspectRatio, 2.0);
            zoomedUV2 += texCoordOffset;

            float heightMapDepth = ((1 - texture(Sampler3, zoomedUV2).r) * Depth) - 0.35;

            if(texCoords.z >= heightMapDepth){
                color = texture(Sampler0, zoomedUV2) * vertexColor;
                normalMap = texture(Sampler1, zoomedUV2) * 2.0 - 1.0;
                normalMap.rgb *= TBN;
                normalMap.r = normalMap.r;
                break;
            }

            dist += 0.002;
        }
    }

    if (color.a < 0.1) {
        discard;
    }

    fragAlbedo = vec4(color.rgb * (1.0 - dist*5), 1.0);
    fragNormal = vec4(normalMap.rgb, 1.0);
    fragMaterial = ivec4(BLOCK_SOLID, 0, 0, 1);
    fragLightSampler = vec4(texCoord2, 0.0, 1.0);
    fragLightMap = lightmapColor;
}


