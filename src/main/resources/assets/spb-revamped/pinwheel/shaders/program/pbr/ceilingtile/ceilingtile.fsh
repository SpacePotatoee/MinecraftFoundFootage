#include veil:material
#include veil:deferred_buffers
#include veil:deferred_utils
#include veil:camera
#include veil:blend
#include spb-revamped:accurateuv

uniform sampler2D Sampler0;
uniform sampler2D Sampler1;
uniform sampler2D ColorMap;
uniform sampler2D NormalMap;

uniform vec4 ColorModulator;

in vec4 vertexColor;
in vec2 texCoord0;
in vec2 texCoord2;
in vec4 lightmapColor;
in vec3 normal;
in vec3 worldPos;
in vec3 Pos;
in mat3 TBN;


void main() {
    vec2 faceUV = getAccurateUV(worldPos, normal);

    vec4 color = texture(Sampler0, texCoord0) * vertexColor;
    vec4 normalMap = (texture(NormalMap, faceUV) * 2.0 - 1.0);
    normalMap.g = -normalMap.g;
    normalMap.rgb *= TBN;
    normalMap.rgb *= abs(viewToWorldSpaceDirection(normal).g);
    if(abs(viewToWorldSpaceDirection(normal).g) <= 0.01){
        normalMap.rgb = normal.rgb;
    }


    fragAlbedo = vec4(color.rgb, 1.0);
    fragNormal = vec4(normalMap.rgb, 1.0);
    fragMaterial = ivec4(BLOCK_SOLID, 0, 0, 1);
    fragLightSampler = vec4(texCoord2, 0.0, 1.0);
    fragLightMap = lightmapColor;
}


