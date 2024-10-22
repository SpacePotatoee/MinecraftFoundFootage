#include veil:deferred_utils
#include veil:camera
#include spb-revamped:shadows
#include spb-revamped:sky

#define SHADOW_SAMPLES 3

#define SHADOW_STRENGTH 0.5

uniform sampler2D Mid2Sampler;
uniform sampler2D DiffuseSampler0;
uniform sampler2D NormalSampler;
uniform sampler2D HandDepth;
uniform sampler2D DepthSampler;
uniform sampler2D TranslucentDepthSampler;
uniform sampler2D ShadowSampler;
uniform sampler2D NoiseTex;
uniform sampler2D TransparentCompatSampler;
uniform sampler2D OpaqueCompatSampler;
uniform usampler2D TransparentMatSampler;
uniform usampler2D OpaqueMatSampler;
uniform sampler2D CloudNoise1;
uniform sampler2D CloudNoise2;

uniform mat4 viewMatrix;
uniform mat4 orthographMatrix;
uniform vec3 lightAngle;
uniform int ShadowToggle;
uniform float GameTime;

const vec3 LIGHT_COLOR = vec3(0.9411, 0.8156, 0.5803);
//const vec3 LIGHT_COLOR = vec3(1);

out vec4 fragColor;
in vec2 texCoord;


mat2 randRotMat(vec2 coord){
    float randomAngle = texture(NoiseTex, coord * 20.0).r * 100.0;
    float cosTheta = cos(randomAngle);
    float sinTheta = sin(randomAngle);
    return mat2(cosTheta, -sinTheta, sinTheta, cosTheta) / 2048;
}

void main() {
    vec4 compat = texture(TransparentCompatSampler, texCoord);
    vec4 compat2 = texture(OpaqueCompatSampler, texCoord);
    uint Mat = texture(TransparentMatSampler, texCoord).r;
    uint Mat2 = texture(OpaqueMatSampler, texCoord).r;

    vec4 color = texture(DiffuseSampler0, texCoord);
    float depth = texture(DepthSampler, texCoord).r;
    float handDepth = texture(HandDepth, texCoord).r;
    vec4 normal = texture(NormalSampler, texCoord);

    vec3 viewPos = viewPosFromDepth(depth, texCoord);
    vec3 playerSpace = viewToPlayerSpace(viewPos);
    float worldDepth = length(viewPos);


    if(ShadowToggle == 1){
        color.rgb = pow(color.rgb, vec3(2.2));

        float lightDir = dot(normalize(lightAngle), viewToWorldSpaceDirection(normal.rgb));

        if (handDepth >= 1.0){
            if (lightDir >= - 0.001){
                vec3 shadowScreenSpace = getShadow(playerSpace, normal, viewPos, viewMatrix, orthographMatrix);

                float shadowDepth = shadowScreenSpace.z;

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
                color.rgb *= (clamp(shadowSum, 1.0 - SHADOW_STRENGTH, 1.0)) * LIGHT_COLOR;
            }
            else{
                color.rgb *= 1.0 - SHADOW_STRENGTH;
            }
        }


        //////////////////////////////////////////////////////////////////////////

        vec3 ro = VeilCamera.CameraPosition;
        vec3 rd = viewDirFromUv(texCoord);
        float dist = 0.0;
        float brightness = 0.0;

        //raymarch
        for (int i = 0; i <= 250; i++){
            vec3 rp = ro + rd * dist;
            dist += 0.1;

            if (dist > worldDepth){
                break;
            }

            vec3 playerSpace = rp - ro;
            vec3 shadowScreenSpace = getShadow(playerSpace, viewMatrix, orthographMatrix);
            float shadowDepth = shadowScreenSpace.z;
            float shadowSampler = texture(ShadowSampler, shadowScreenSpace.xy).r;


            if (shadowDepth < shadowSampler){
                brightness += 0.001;
            }

            if (brightness >= 1.5){
                brightness = 1.5;
                break;
            }

        }

        if (handDepth >= 1){
            color.rgb += (brightness * LIGHT_COLOR);
        }

        color.rgb = pow(color.rgb, vec3(1 / 2.2));
    }

    if(compat.a > 0 || compat2.a > 0){
        if(Mat == 15 || Mat2 == 15){
            color.rgb = getSky(texCoord, GameTime, CloudNoise1, CloudNoise2);
        }
        else{
            color = compat + compat2;
        }
    }
    fragColor = color;
}
