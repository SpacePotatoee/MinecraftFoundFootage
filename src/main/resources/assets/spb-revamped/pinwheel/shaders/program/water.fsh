#include veil:deferred_utils
#include veil:camera

uniform sampler2D DiffuseSampler0;
#line 0 2
#define BLOCK_SOLID 0
#define BLOCK_CUTOUT 1
#define BLOCK_CUTOUT_MIPPED 2
#define BLOCK_TRANSLUCENT 3

#define ENTITY_SOLID 4
#define ENTITY_CUTOUT 5
#define ENTITY_TRANSLUCENT 6
#define ENTITY_TRANSLUCENT_EMISSIVE 7

#define PARTICLE 8
#define ARMOR_CUTOUT 9
#define LEAD 10
#define BREAKING 11
#define CLOUD 12
#define WORLD_BORDER 13

bool isBlock(uint material) {
    return material >= BLOCK_SOLID  && material <= BLOCK_TRANSLUCENT;
}

bool isEntity(uint material) {
    return material >= ENTITY_SOLID && material <= ENTITY_TRANSLUCENT_EMISSIVE;
}

bool isEmissive(uint material) {
    return material == ENTITY_TRANSLUCENT_EMISSIVE;
}

#line 2 0
#line 0 3
layout(location = 0) out vec4 fragColor;
layout(location = 1) out vec4 fragAlbedo;
layout(location = 2) out vec4 fragNormal;
layout(location = 3) out ivec4 fragMaterial;
layout(location = 4) out vec4 fragLightSampler;
layout(location = 5) out vec4 fragLightMap;

#line 31 0
#line 0 4
#define TRANSLUCENT_TRANSPARENCY 0
#define ADDITIVE_TRANSPARENCY 1
#define LIGHTNING_TRANSPARENCY 2
#define GLINT_TRANSPARENCY 3
#define CRUMBLING_TRANSPARENCY 4
#define NO_TRANSPARENCY 5

vec3 blend(vec4 dst, vec4 src) {
    return src.rgb + (dst.rgb * (1 - src.a));
}

vec3 blendAdditive(vec4 dst, vec4 src) {
    return src.rgb + dst.rgb * dst.a;
}

vec3 blendLightning(vec4 dst, vec4 src) {
    return src.rgb * src.a + dst.rgb;
}

vec3 blendGlint(vec4 dst, vec4 src) {
    return src.rgb * src.rgb + dst.rgb;
}

vec3 blendCrumbling(vec4 dst, vec4 src) {
    return src.rgb * dst.rgb + dst.rgb * src.rgb;
}

vec3 blend(uint material, vec4 dst, vec4 src) {
    if (material != NO_TRANSPARENCY) {
        if (material == TRANSLUCENT_TRANSPARENCY) {
            return blend(dst, src);
        }
        if (material == ADDITIVE_TRANSPARENCY) {
            return blendAdditive(dst, src);
        }
        if (material == LIGHTNING_TRANSPARENCY) {
            return blendLightning(dst, src);
        }
        if (material == GLINT_TRANSPARENCY) {
            return blendGlint(dst, src);
        }
        if (material == CRUMBLING_TRANSPARENCY) {
            return blendCrumbling(dst, src);
        }
    }
    return src.a == 0.0 ? dst.rgb : src.rgb;
}

#line 38 0

uniform sampler2D Sampler0;
uniform sampler2D WaterTexture;
uniform sampler2D UnderWaterBuffer;
uniform sampler2D WaterFrameBuffer;
uniform vec4 ColorModulator;
uniform float GameTime;

in vec4 vertexColor;
in vec2 texCoord0;
in vec2 texCoord2;
in vec4 overlayColor;
in vec4 lightmapColor;
in vec3 normal;

void main() {
    vec3 cameraPos = VeilCamera.CameraPosition;
    vec4 color = texture(Sampler0, texCoord0) * vertexColor * ColorModulator;
    if (color.a < 0.1) {
        discard;
    }
    //vec3 viewSpace = vec4(vec4(BlockPos - cameraPos,1) * VeilCamera.ViewMat).xyz;

    //fragAlbedo = texture(UnderWaterBuffer, texCoord.xz);
    fragAlbedo = vec4(vec3(0), 1) * 50;
    fragNormal = vec4(normal, 1.0);
    fragMaterial = ivec4(BLOCK_TRANSLUCENT, TRANSLUCENT_TRANSPARENCY, 0, 1);
    fragLightSampler = vec4(texCoord2, 0.0, 1.0);
    fragLightMap = lightmapColor;
}


