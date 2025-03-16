#include veil:camera
#include veil:blend
#include veil:material
#include veil:deferred_utils
#include spb-revamped:puddles
#include spb-revamped:sky
#include spb-revamped:common
#include spb-revamped:shadows

#define OFFSET vec2(0.1965249, 0.6546237)

uniform sampler2D DiffuseSampler0;
uniform sampler2D DiffuseSampler2;
uniform sampler2D DepthSampler;
uniform sampler2D TransparentDepthSampler;
uniform sampler2D NormalSampler;
uniform sampler2D HandSampler;
uniform sampler2D HandDepth;
uniform sampler2D WaterSampler;
uniform sampler2D NoiseTexture;
uniform sampler2D NoiseTexture2;
uniform sampler2D FogTexture;

uniform sampler2D ShadowSampler;
uniform sampler2D TransparentCompatSampler;
uniform sampler2D OpaqueCompatSampler;
uniform usampler2D TransparentMatSampler;
uniform usampler2D OpaqueMatSampler;
uniform sampler2D OpaqueAlbedoSampler;
uniform sampler2D CloudNoise1;
uniform sampler2D CloudNoise2;

uniform mat4 viewMatrix;
uniform int ShadowToggle;
uniform mat4 orthographMatrix;
uniform vec2 ScreenSize;
uniform vec2 Velocity;
uniform int FogToggle;
uniform int blackScreen;
uniform int TogglePuddles;
uniform float GameTime;
uniform float sunsetTimer;
uniform vec3 shadowColor;

#define FOG_COLOR vec4(0.8, 0.8, 0.8, 1.0)


in vec2 texCoord;
out vec4 fragColor;

float map(vec3 p){
	return p.y - 21.75;
}

float noise3D(vec3 p){
	float z = p.z;
	vec2 z1 = (floor(z) * OFFSET + p.xz)/5.0;
	vec2 z2 = ((floor(z) + 1.0) * OFFSET + p.xz)/5.0;
	float n1 = texture(FogTexture, z1 + GameTime * 100.0).r;
	float n2 = texture(FogTexture, z2 + GameTime * 100.0).r;
	float ratio = fract(z);
	return mix(n1, n2, ratio);
}

void main() {
	if(blackScreen == 0){
		float handDepth = texture(HandDepth, texCoord).r;
		vec4 handColor = texture(HandSampler, texCoord);

		vec4 color = texture(DiffuseSampler0, texCoord);
		vec4 water = texture(WaterSampler, texCoord);
		float depth = texture(DepthSampler, texCoord).r;
		float transparentDepth = texture(TransparentDepthSampler, texCoord).r;

		vec3 viewPos = viewPosFromDepth(depth, texCoord);
		vec3 transparentViewPos = viewPosFromDepth(transparentDepth, texCoord);

		float worldDepth = length(viewPos);
		vec4 normal = texture(NormalSampler, texCoord);
		vec4 compat = texture(TransparentCompatSampler, texCoord);
		vec4 compat2 = texture(OpaqueCompatSampler, texCoord);
		uint OpaqueMat = texture(OpaqueMatSampler, texCoord).r;
		uint TransparentMat = texture(TransparentMatSampler, texCoord).r;

		if (!(compat.a > 0.0) && !(compat2.a > 0.0) && !isEntity(TransparentMat)){
			if (TogglePuddles == 1){
				color = getPuddles(color, texCoord, vec4(worldToViewSpaceDirection(normalize(vec3(0.0,1.0,0.0))), 1.0), DiffuseSampler0, TransparentDepthSampler, NoiseTexture, NoiseTexture2);
			}
		}

		vec3 ro = VeilCamera.CameraPosition;
		vec3 rd = normalize(viewToPlayerSpace(viewPos));
		float travDist = 0.0;
		float hitDist = 0.0;
		vec4 col = vec4(0.0);
		bool inside = false;
		float fog = 0.0;
		vec3 p;
		if (FogToggle == 1) {
			//Raymarching
			for (int i = 0; i < 100; i++) {
				if (inside == false) {
					if (worldDepth > 500.0) {
						break;
					}

					p = ro + rd * travDist;
					float d = map(p);
					travDist += d;


					if (d < 0.001) {
						hitDist = travDist;
						inside = true;
					}

				}
				else {
					float noise = noise3D(p);
					fog += 0.001 * noise;
					p = ro + rd * travDist;
					travDist += 0.1;
					if (travDist > worldDepth || fog >= 1.0 || travDist > 50.0 || p.y < 20.5) {
						break;
					}
				}
			}
			color.rgb = mix(color.rgb, FOG_COLOR.rgb, fog);
		}

		color.rgb = blend(color, water);

		//Do this after the water
		if(OpaqueMat != 15){
			if(ShadowToggle == 1) {
				color.rgb = getVolumetricLight(color, texCoord, transparentViewPos, ScreenSize, viewMatrix, orthographMatrix, ShadowSampler, sunsetTimer, shadowColor);
			}
		}


		if(compat.a > 0.0 || compat2.a > 0.0){
			color.rgb = blend(color, compat);
			color.rgb = blend(color, compat2);
			color.a = min(compat.a + compat2.a, 1.0);
		}

		//Emergency Light. Need this to get it to shine easier
		if(OpaqueMat == 19){
			vec4 testColor = texture(OpaqueAlbedoSampler, texCoord);

			//If the color is pure white
			if(testColor.r + testColor.g + testColor.b >= 3){
				color = testColor;
			} else if(testColor.r >= 1 && testColor.g + testColor.b <= 0) {
				color = vec4(1.0, 0.0, 0.0, 1.0);
			}
		}


		if (handDepth < 1.0){
			color = handColor;
		}

		fragColor = color;
	}else{
		fragColor = vec4(0.0,0.0,0.0,1.0);
	}
}