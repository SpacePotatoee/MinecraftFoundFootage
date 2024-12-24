#include veil:deferred_utils
#include veil:camera
#include veil:color_utilities

#define REFLECTIVITY 0.6

const float rayStep = 0.1;
const int maxSteps = 100;
const int BinSearchSteps = 10;
const float SCALE = 0.75;

vec3 hash(vec3 p){
    p = vec3( dot(p,vec3(127.1,311.7, -74.7)),
    dot(p,vec3(269.5,183.3,246.1)),
    dot(p,vec3(113.5,271.9,124.6)));

    return fract(sin(p)*43758.5453123);
}

void spaceConversion(inout vec4 projectedCoords, inout float posDepth, inout vec3 Pos, sampler2D DepthSampler){
    projectedCoords = VeilCamera.ProjMat * vec4(Pos, 1.0);
    projectedCoords.xyz /= projectedCoords.w;
    projectedCoords = projectedCoords * 0.5 + 0.5;
    posDepth = texture(DepthSampler, projectedCoords.xy).r;
}

vec2 rayMarch(vec3 dir, vec3 origin, sampler2D DepthSampler){
    float posDepth = 0.0;
    float dDepth = 0.0;
    vec3 Pos = origin;
    vec4 projectedCoords = vec4(0.0);

    dir = dir * rayStep;

    for(int i = 0; i < maxSteps; i++){
        Pos += dir;

        spaceConversion(projectedCoords, posDepth, Pos, DepthSampler);
        if (projectedCoords.x < 0.0 || projectedCoords.x > 1.0 || projectedCoords.y < 0.0 || projectedCoords.y > 1.0) break;

        dDepth = Pos.z - posDepth;
        //Hit
        if (projectedCoords.z > posDepth){
            if((projectedCoords.z - posDepth) > 0.0008){
                continue;
            }
            //Binary Search
            for (int j = 0; j < BinSearchSteps; j++){
                spaceConversion(projectedCoords, posDepth, Pos, DepthSampler);

                dDepth = projectedCoords.z - posDepth;
                dir *= 0.5;
                if (dDepth > 0.0){
                    Pos -= dir;
                }
                else{
                    Pos += dir;
                }
            }

            spaceConversion(projectedCoords, posDepth, Pos, DepthSampler);
            return projectedCoords.xy;
        }

    }
    return projectedCoords.xy;
}

vec4 getReflection(vec4 fragColor, vec4 normal, vec3 viewPos, float jitterMult, sampler2D DiffuseSampler0, sampler2D DepthSampler){
    vec3 reflected = normalize(reflect(normalize(viewPos), normalize(normal.rgb)));
    vec3 worldSpace = viewToWorldSpace(viewPos);
    vec3 jitter = hash(worldSpace) * jitterMult;
    vec2 projectedCoord = rayMarch(jitter + reflected * max(rayStep, -viewPos.z), viewPos, DepthSampler);
    vec3 reflectedTexture = texture(DiffuseSampler0, projectedCoord).rgb;
    float Luminance = luminance(reflectedTexture);

    vec2 dCoords = smoothstep(0.4, 0.5, abs(vec2(0.5) - projectedCoord));

    float screenEdgefactor = clamp(1.0f - (dCoords.x + dCoords.y), 0.0f, 1.0f);

    float ReflectionMultiplier = screenEdgefactor * (reflected.z);

    if(Luminance >= 1){
        return mix(fragColor, mix(fragColor, vec4(reflectedTexture * 20, 1.0) * clamp(-ReflectionMultiplier, 0.0, 1.0), -ReflectionMultiplier), clamp(REFLECTIVITY, 0.0, 1.0));
    }

    return mix(fragColor, mix(fragColor, vec4(reflectedTexture, 1.0) * clamp(-ReflectionMultiplier, 0.0, 1.0), -ReflectionMultiplier), clamp(REFLECTIVITY, 0.0, 1.0));
}

vec4 getPuddles(vec4 fragColor, vec2 texCoord, vec4 normal, sampler2D DiffuseSampler0, sampler2D DepthSampler, sampler2D NoiseTexture, sampler2D NoiseTexture2){
    vec4 color = fragColor;
    vec4 mainTexture = texture(DiffuseSampler0, texCoord);
    float depth = texture(DepthSampler, texCoord).r;
    vec3 viewSpace = viewPosFromDepth(depth, texCoord);
    vec3 worldSpace = viewToWorldSpace(viewSpace);

    vec4 noise = texture(NoiseTexture, (worldSpace.xz * 0.02) * SCALE);
    vec4 noise_2 = texture(NoiseTexture2, (worldSpace.xz * 0.5) * SCALE);
    noise = (clamp(smoothstep(0.1, 0.9, noise_2) * 0.2, 0.0, 1.0) + smoothstep(0.1, 0.9, noise));

    if (worldSpace.y <= 21.001 && worldSpace.y >= 20.99 && length(viewSpace) <= 150){
        noise = smoothstep(0.3, 0.7, noise);
        noise = clamp(noise, 0.0, 1.0);
//        color = mainTexture;

        if (noise.r < 0.5){
            color = getReflection(color, normal, viewSpace, 0.02, DiffuseSampler0, DepthSampler);
            color = mix(color, mainTexture, noise) - (noise * 0.02);
            color -= (1.0 - noise) * 0.1;
        }
        else if (noise.r < 0.8){
            color = getReflection(color, normal, viewSpace, 1, DiffuseSampler0, DepthSampler);
            color = mix(color, mainTexture, noise) - (noise * 0.02);
            color -= (1.0 - noise) * 0.1;
        }
        else if (noise.r < 0.85){
            color -= 0.015;
        }
    }

    return color;
}