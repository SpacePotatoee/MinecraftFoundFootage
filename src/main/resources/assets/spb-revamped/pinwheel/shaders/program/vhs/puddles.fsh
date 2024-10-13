#include veil:deferred_utils
#include veil:color_utilities

#define REFLECTIVITY 0.6

uniform sampler2D DiffuseSampler0;
uniform sampler2D DepthSampler;
uniform sampler2D HandSampler;
uniform sampler2D NoiseTexture;
uniform sampler2D RandomTexture;
uniform sampler2D NormalSampler;
uniform mat4 projMat;

uniform int TogglePuddles;

out vec4 fragColor;
in vec2 texCoord;

const float rayStep = 0.01;
const int maxSteps = 1000;
const int BinSearchSteps = 50;

vec3 hash(vec3 p){
    p = vec3( dot(p,vec3(127.1,311.7, -74.7)),
    dot(p,vec3(269.5,183.3,246.1)),
    dot(p,vec3(113.5,271.9,124.6)));

    return fract(sin(p)*43758.5453123);
}

float brightness(vec4 color){
    return (color.r + color.g + color.b) / 3;
}

vec2 rayMarch(vec3 dir, vec3 origin){
    float posDepth = 0.0;
    float dDepth = 0.0;
    vec3 Pos = origin;
    vec4 projectedCoords = vec4(0.0);

    dir = dir * rayStep;

    for(int i = 0; i < maxSteps; i++){
        Pos += dir;

        projectedCoords = VeilCamera.ProjMat * vec4(Pos, 1.0);
        projectedCoords.xyz /= projectedCoords.w;
        projectedCoords = projectedCoords * 0.5 + 0.5;
        posDepth = texture(DepthSampler, projectedCoords.xy).r;

        if (projectedCoords.x < 0.0 || projectedCoords.x > 1.0 || projectedCoords.y < 0.0 || projectedCoords.y > 1.0) break;

        dDepth = Pos.z - posDepth;
        //Hit
        if (projectedCoords.z > posDepth){


            //Binary Search
            for (int j = 0; j < BinSearchSteps; j++){
                projectedCoords = VeilCamera.ProjMat * vec4(Pos, 1.0);
                projectedCoords.xyz /= projectedCoords.w;
                projectedCoords = projectedCoords * 0.5 + 0.5;
                posDepth = texture(DepthSampler, projectedCoords.xy).r;

                dDepth = projectedCoords.z - posDepth;
                dir *= 0.5;
                if (dDepth > 0.0){
                    Pos -= dir;
                }
                else{
                    Pos += dir;
                }
            }

            projectedCoords = VeilCamera.ProjMat * vec4(Pos, 1.0);
            projectedCoords.xyz /= projectedCoords.w;
            projectedCoords = projectedCoords * 0.5 + 0.5;
            posDepth = texture(DepthSampler, projectedCoords.xy).r;
            return projectedCoords.xy;
        }

    }
    return projectedCoords.xy;
}

vec4 getReflection(vec4 fragColor, vec4 normal, vec3 viewPos, float jitterMult){
    vec3 reflected = normalize(reflect(normalize(viewPos), normalize(normal.rgb)));
    vec3 worldSpace = viewToWorldSpace(viewPos);
    vec3 jitter = hash(worldSpace) * jitterMult;
    vec2 projectedCoord = rayMarch(jitter + reflected * max(rayStep, -viewPos.z), viewPos);
    vec3 reflectedTexture = texture(DiffuseSampler0, projectedCoord).rgb;
    float Luminance = luminance(reflectedTexture);

    vec2 dCoords = smoothstep(0.3, 0.5, abs(vec2(0.5) - projectedCoord));

    float screenEdgefactor = clamp(1.0 - (dCoords.x + dCoords.y), 0.0, 1.0);

    float ReflectionMultiplier = screenEdgefactor * (reflected.z);

    if(Luminance >= 1.0){
        return mix(fragColor, mix(fragColor, vec4(reflectedTexture * 20, 1.0) * clamp(-ReflectionMultiplier, 0.0, 1.0), -ReflectionMultiplier), clamp(REFLECTIVITY, 0, 1));
    }

    return mix(fragColor, mix(fragColor, vec4(reflectedTexture, 1.0) * clamp(-ReflectionMultiplier, 0.0, 1.0), -ReflectionMultiplier), clamp(REFLECTIVITY, 0, 1));
}


void main() {
    if(TogglePuddles == 1){
        vec4 mainTexture = texture(DiffuseSampler0, texCoord);
        vec4 normal = texture(NormalSampler, texCoord);
        float handDepth = texture(HandSampler, texCoord).r;
        float depth = texture(DepthSampler, texCoord).r;
        vec3 viewSpace = viewPosFromDepth(depth, texCoord);
        vec3 worldSpace = viewToWorldSpace(viewSpace);

        vec4 noise = texture(NoiseTexture, worldSpace.xz * 0.02);
        vec4 randomNoise = texture(RandomTexture, worldSpace.xz * 0.5);
        noise = (clamp(smoothstep(0.1, 0.9, randomNoise) * 0.2, 0, 1) + smoothstep(0.1, 0.9, noise));

        if (worldSpace.y <= 21.001 && handDepth >= 1.0 && length(viewSpace) <= 150){
            noise = smoothstep(0.3, 0.7, noise);
            noise = clamp(noise, 0, 1);
            fragColor = mainTexture;

            if (noise.r < 0.5){
                fragColor = getReflection(fragColor, normal, viewSpace, 0.02);
                fragColor = mix(fragColor, mainTexture, noise) - (noise * 0.02);
                fragColor -= (1 - noise) * 0.1;
            }
            else if (noise.r < 0.8){
                fragColor = getReflection(fragColor, normal, viewSpace, 1);
                fragColor = mix(fragColor, mainTexture, noise) - (noise * 0.02);
                fragColor -= (1 - noise) * 0.1;
            }
            else if (noise.r < 0.85){
                fragColor -= 0.015;
            }
        }
    }
}

