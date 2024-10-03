#include veil:deferred_utils
#include veil:camera

#define SHADOW_SAMPLES 2

#define SHADOW_STRENGTH 0.8

uniform sampler2D Mid2Sampler;
uniform sampler2D DiffuseSampler0;
uniform sampler2D NormalSampler;
uniform sampler2D HandDepth;
uniform sampler2D DepthSampler;
uniform sampler2D TranslucentDepthSampler;
uniform sampler2D ShadowSampler;
uniform sampler2D NoiseTex;
uniform mat4 viewMatrix;
uniform mat4 orthographMatrix;
uniform vec3 lightAngle;

out vec4 fragColor;
in vec2 texCoord;

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
    vec4 color = texture(DiffuseSampler0, texCoord);
    float depth = texture(DepthSampler, texCoord).r;
    float handDepth = texture(HandDepth, texCoord).r;
    vec4 normal = texture(NormalSampler, texCoord);

    vec3 viewPos = viewPosFromDepth(depth, texCoord);
    vec3 playerSpace = viewToPlayerSpace(viewPos);
    float worldDepth = length(viewPos);

    color.rgb = pow(color.rgb, vec3(2.2));

    float lightDir = dot(normalize(lightAngle), viewToWorldSpaceDirection(normal.rgb));

    if(handDepth >= 1.0 && worldDepth <= 60.0){
        if (lightDir >= - 0.001){
            //color.rgb *= lightDir * 1;
            vec3 adjustedPlayerSpace = playerSpace + 0.001 * viewToWorldSpaceDirection(normal.rgb) * length(viewPos);
            vec3 shadowViewPos = (viewMatrix * vec4(adjustedPlayerSpace, 1.0)).xyz;
            vec4 homogenousPos = orthographMatrix * vec4(shadowViewPos, 1.0);
            vec3 shadowNdcPos = homogenousPos.xyz / homogenousPos.w;
            vec3 distortedNdcSpace = distort(shadowNdcPos);
            vec3 shadowScreenSpace = distortedNdcSpace * 0.5 + 0.5;

            float shadowDepth = shadowScreenSpace.z - 0.0001;

            float shadowSum = 0.0;
            mat2 randRotation = randRotMat(texCoord);
            for (int x = - SHADOW_SAMPLES; x <= SHADOW_SAMPLES; x++){
                for (int y = - SHADOW_SAMPLES; y <= SHADOW_SAMPLES; y++){
                    vec2 offset = randRotation * vec2(x, y);
                    float shadowSampler = texture(ShadowSampler, shadowScreenSpace.xy + offset).r;

                    if (shadowDepth < shadowSampler){
                        shadowSum += 1.0;
                    }
                }
            }

            shadowSum /= pow(2.0 * SHADOW_SAMPLES + 1.0, 2.0);
            color.rgb *= clamp(shadowSum, 1.0 - SHADOW_STRENGTH, 1.0);
        }
        else{
            color.rgb *= 1.0 - SHADOW_STRENGTH;
        }
    }

    color.rgb = pow(color.rgb, vec3(1/2.2));

    fragColor = color;
}
