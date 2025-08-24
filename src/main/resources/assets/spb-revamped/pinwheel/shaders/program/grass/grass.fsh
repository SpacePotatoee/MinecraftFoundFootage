#include veil:camera
#include veil:deferred_buffers
#include veil:deferred_utils
#include veil:material

//tbh i change this caus it looks a bit better now ik it ain't gonna be accepted but ayyyyyyyyyy it's for the funsies
uniform sampler2D WindNoise;
uniform float GameTime;
uniform float grassHeight;

in vec3 localPos;
in vec3 normal;

float getGrassHeightGradient(float height){
    return height / max(grassHeight, 0.0001);
}

void main() {
    vec3 worldPos = localPos + VeilCamera.CameraPosition;
    float baseY = 31.0;

    float tH = clamp(getGrassHeightGradient(worldPos.y - baseY), 0.0, 1.0);
    vec3 baseLow  = vec3(0.10, 0.20, 0.12);
    vec3 baseHigh = vec3(0.28, 0.42, 0.18);
    vec3 grassColor = mix(baseLow, baseHigh, pow(tH, 1.55));

    float hueN = texture(WindNoise, worldPos.xz * 0.11 + GameTime * 0.01).r - 0.5;
    grassColor += 0.02 * vec3(hueN * 0.4, hueN * 0.2, -hueN * 0.2);

    #ifdef LEVEL324
    tH = clamp(getGrassHeightGradient(worldPos.y - (baseY + 34.0)), 0.0, 1.0);
    vec3 l324Low  = vec3(0.30, 0.20, 0.10);
    vec3 l324High = vec3(0.40, 0.22, 0.08) * 0.8;
    grassColor = mix(l324Low, l324High, pow(tH, 1.4));
    #endif

    float aoN = texture(WindNoise, worldPos.xz * 0.09 + GameTime * 0.02).r;
    float baseAO = mix(0.55, 1.0, pow(tH, 1.3));
    float occlusionFactor = clamp(baseAO * (0.7 + 0.3 * aoN), 0.28, 1.0);

    vec3 N = normalize(normal);
    float facing = clamp(dot(N, vec3(0.0, 1.0, 0.0)), 0.0, 1.0);
    grassColor = mix(grassColor, grassColor * vec3(1.12, 1.08, 1.04), 0.14 * facing);

    vec3 L = normalize(vec3(0.2, 1.0, 0.35));
    float NdotL = max(dot(N, L), 0.0);
    grassColor *= (0.62 + 0.28 * NdotL);

    float back = pow(max(dot(N, -L), 0.0), 2.2) * mix(0.06, 0.22, tH);
    grassColor += back * vec3(0.40, 0.65, 0.72);

    vec3  V  = normalize(-localPos);
    vec3  H  = normalize(L + V);
    float nh = max(dot(N, H), 0.0);
    float sparkleMask = step(0.976, texture(WindNoise, worldPos.xz * 2.2).r);
    float spec = pow(nh, 56.0) * 0.10 * sparkleMask * (0.5 + 0.5 * tH);
    grassColor += spec;

    float pulse = 0.985 + 0.015 * sin(GameTime * 1.2 + worldPos.x * 0.11 + worldPos.z * 0.13);
    grassColor *= pulse;

    vec3  fogColor = vec3(0.96, 0.97, 0.985);
    float dist     = length(localPos);
    float height   = worldPos.y;

    float fogDist, fogHeight;
    #ifdef LEVEL324
    float kDist = 0.045;
    float kH    = 0.020;
    float hRef  = baseY + 1.0;
    fogDist   = 1.0 - exp(-kDist * dist);
    fogHeight = 1.0 - exp(-kH * max(0.0, (hRef + 3.0) - height));
    #else
    float kDist = 0.025;
    float kH    = 0.012;
    float hRef  = baseY + 1.0;
    fogDist   = 1.0 - exp(-kDist * dist);
    fogHeight = 1.0 - exp(-kH * max(0.0, (hRef + 2.0) - height));
    #endif

    float fog = clamp(0.65 * fogDist + 0.35 * fogHeight, 0.0, 1.0);
    float fogNoise = texture(WindNoise, gl_FragCoord.xy * 0.35).r - 0.5;
    fog = clamp(fog + fogNoise * 0.02, 0.0, 1.0);

    float fogAtten = mix(1.0, 0.5, fog);
    grassColor *= fogAtten;

    vec3 finalColor = mix(grassColor, fogColor, fog);

    fragAlbedo   = vec4(finalColor * occlusionFactor, 1.0);
    fragNormal   = vec4(worldToViewSpaceDirection(N), 1.0);
    fragMaterial = ivec4(15, 0, 0, 1);
    fragLightMap = vec4(1.0);
}
