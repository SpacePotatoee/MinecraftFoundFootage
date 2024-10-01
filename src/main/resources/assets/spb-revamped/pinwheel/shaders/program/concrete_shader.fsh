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

float zoom = 0.5;

vec3 random(vec3 p) {
  p = p + 0.02;
  float x = dot(p, vec3(123.4, 234.5, 432.3));
  float y = dot(p, vec3(234.5, 345.6, 824.1));
  float z = dot(p, vec3(134.5, 735.6, 144.1));
  vec3 gradient = vec3(x, y, z);
  gradient = sin(gradient);
  gradient = gradient * 43758.5453;

  gradient = sin(gradient);
  return gradient;

  //gradient = sin(gradient);
  //return gradient;
}

float perlin (vec3 st){
	vec3 ipos = floor(st);
	vec3 fpos = fract(st);
	
	fpos = fpos*fpos*(3.0-2.0*fpos);
	
	//fpos = smoothstep(0,1,fpos);
	
	//Find the corners of each cell
	vec3 BL = ipos + vec3(0.0, 0.0, 0.0);
	vec3 BR = ipos + vec3(1.0, 0.0, 0.0);
	vec3 TL = ipos + vec3(0.0, 1.0, 0.0);
	vec3 TR = ipos + vec3(1.0, 1.0, 0.0);
	
	vec3 BL2 = ipos + vec3(0.0, 0.0, 1.0);
	vec3 BR2 = ipos + vec3(1.0, 0.0, 1.0);
	vec3 TL2 = ipos + vec3(0.0, 1.0, 1.0);
	vec3 TR2 = ipos + vec3(1.0, 1.0, 1.0);
	
	//Create a random vector for each corner
	vec3 gradBL = random(BL);
	vec3 gradBR = random(BR);
	vec3 gradTL = random(TL);
	vec3 gradTR = random(TR);
	
	vec3 gradBL2 = random(BL2);
	vec3 gradBR2 = random(BR2);
	vec3 gradTL2 = random(TL2);
	vec3 gradTR2 = random(TR2);
	
	//Find distance to corners
	vec3 distFromPixelToBL = fpos - vec3(0.0, 0.0, 0.0);
	vec3 distFromPixelToBR = fpos - vec3(1.0, 0.0, 0.0);
	vec3 distFromPixelToTL = fpos - vec3(0.0, 1.0, 0.0);
	vec3 distFromPixelToTR = fpos - vec3(1.0, 1.0, 0.0);
	
	vec3 distFromPixelToBL2 = fpos - vec3(0.0, 0.0, 1.0);
	vec3 distFromPixelToBR2 = fpos - vec3(1.0, 0.0, 1.0);
	vec3 distFromPixelToTL2 = fpos - vec3(0.0, 1.0, 1.0);
	vec3 distFromPixelToTR2 = fpos - vec3(1.0, 1.0, 1.0);
	
	//Dot products
	float dotBL = dot(gradBL, distFromPixelToBL);
	float dotBR = dot(gradBR, distFromPixelToBR);
	float dotTL = dot(gradTL, distFromPixelToTL);
	float dotTR = dot(gradTR, distFromPixelToTR);
	
	float dotBL2 = dot(gradBL2, distFromPixelToBL2);
	float dotBR2 = dot(gradBR2, distFromPixelToBR2);
	float dotTL2 = dot(gradTL2, distFromPixelToTL2);
	float dotTR2 = dot(gradTR2, distFromPixelToTR2);
	
	//Mix it all together!!
	float b = mix(dotBL,dotBR,fpos.x);
	float t = mix(dotTL,dotTR,fpos.x);
	
	float b2 = mix(dotBL2,dotBR2,fpos.x);
	float t2 = mix(dotTL2,dotTR2,fpos.x);
	
	float perlin = mix(b,t,fpos.y);
	float perlin2 = mix(b2,t2,fpos.y);
	float final = mix(perlin, perlin2, fpos.z);
	
	return final;
}

float fbmPerlinNoise (vec3 uv) {
  float fbmNoise = 0.0;
  float amplitude = 1.0;
  float octaves = 5.0;

  for (float i = 0.0; i < octaves; i++) {
    fbmNoise = fbmNoise + perlin(uv) * amplitude;
    amplitude = amplitude * 0.5;
    uv = uv * 2.0;
  }
  return fbmNoise;
}

#line 38 0

uniform sampler2D Sampler0;

uniform vec4 ColorModulator;

in vec4 vertexColor;
in vec2 texCoord0;
in vec2 texCoord2;
in vec4 overlayColor;
in vec4 lightmapColor;
in vec3 normal;
in vec3 BlockPos;

void main() {
    vec4 color = texture(Sampler0, texCoord0) * vertexColor * ColorModulator;
    if (color.a < 0.1) {
        discard;
    }
    
    float fbmNoise = fbmPerlinNoise(BlockPos * 1.5);
    
    fbmNoise /= fbmPerlinNoise(vec3(-fbmNoise)) * 20;
    
    vec4 OUT = vec4(vec3(fbmNoise+ 0.2),1);
    
    fragAlbedo = mix(color, OUT, 0.15);
    //fragAlbedo = color;
    fragNormal = vec4(normal, 1.0);
    fragMaterial = ivec4(BLOCK_TRANSLUCENT, TRANSLUCENT_TRANSPARENCY, 0, 1);
    fragLightSampler = vec4(texCoord2, 0.0, 1.0);
    fragLightMap = lightmapColor;
}


