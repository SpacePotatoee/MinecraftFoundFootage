#include veil:material
#include veil:deferred_buffers
#include veil:camera
#include veil:deferred_utils

#define SHADOW_SAMPLES 2

uniform sampler2D Sampler0;
uniform sampler2D ShadowSampler;
uniform sampler2D NoiseTex;
uniform mat4 viewRix;
uniform mat4 orthoMatrix;
uniform vec3 lightAngle;

uniform vec4 ColorModulator;

in vec4 vertexColor;
in vec2 texCoord0;
in vec2 texCoord2;
in vec4 lightmapColor;
in vec3 normal;
in vec3 viewPos;

vec2 distort(vec2 pos){
	float dist = length(pos) + 0.1;
	return pos / dist;
}

vec3 distort(in vec3 shadowPosition) {
	const float bias0 = 0.95;
	const float bias1 = 1.0 - bias0;

	float factorDistance = length(shadowPosition.xy);

	float distortFactor = factorDistance * bias0 + bias1;

	return shadowPosition * vec3(vec2(1.0 / distortFactor), 0.2);
}

mat2 randRotMat(vec2 coord){
	float randomAngle = texture(NoiseTex, coord * 20.0).r * 100.0;
	float cosTheta = cos(randomAngle);
	float sinTheta = sin(randomAngle);
	return mat2(cosTheta, -sinTheta, sinTheta, cosTheta) / 2048;
}



void main() {
	vec4 color = texture(Sampler0, texCoord0) * vertexColor * ColorModulator;
	if (color.a < 0.5) {
		discard;
	}
	color.rgb = pow(color.rgb, vec3(2.2));

	float lightDir = dot(normalize(lightAngle), viewToWorldSpaceDirection(normal));

	if(lightDir >= -0.001){
		//color.rgb *= lightDir * 1;
		vec3 playerSpace = viewToPlayerSpace(viewPos);
		vec3 adjustedPlayerSpace = playerSpace + 0.01 * viewToWorldSpaceDirection(normal) * length(viewPos);
		vec3 shadowViewPos = (viewRix * vec4(adjustedPlayerSpace, 1.0)).xyz;
		vec4 homogenousPos = orthoMatrix * vec4(shadowViewPos, 1.0);
		vec3 shadowNdcPos = homogenousPos.xyz / homogenousPos.w;
		vec3 distortedNdcSpace = distort(shadowNdcPos);
		vec3 shadowScreenSpace = distortedNdcSpace * 0.5 + 0.5;

		float shadowDepth = shadowScreenSpace.z - 0.0001;

		float shadowSum = 0.0;
		mat2 randRotation = randRotMat(texCoord0);
		for(int x = -SHADOW_SAMPLES; x <= SHADOW_SAMPLES; x++){
			for(int y = -SHADOW_SAMPLES; y <= SHADOW_SAMPLES; y++){
				vec2 offset = randRotation * vec2(x,y);
				float shadowSampler = texture(ShadowSampler, shadowScreenSpace.xy + offset).r;

				if(shadowDepth < shadowSampler){
					shadowSum += 1.0;
				}
			}
		}

		shadowSum /= pow(2 * SHADOW_SAMPLES + 1, 2);
		color.rgb *= clamp(shadowSum, 0.5,1);
	}
	else{
		color.rgb *= 0.5;
	}

	color.rgb = pow(color.rgb, vec3(1/2.2));
	fragAlbedo = vec4(color.rgb, 1.0);
	fragNormal = vec4(normal, 1.0);
	fragMaterial = ivec4(BLOCK_SOLID, 0, 0, 1);
	fragLightSampler = vec4(texCoord2, 0.0, 1.0);
	fragLightMap = lightmapColor;
}