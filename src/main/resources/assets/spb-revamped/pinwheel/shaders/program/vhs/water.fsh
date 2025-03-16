#include veil:deferred_utils
#include veil:material
#include veil:camera
#include veil:fog
#include spb-revamped:shadows

#define REFRACTION_MULTIPLIER 0.02

#define REFLECTIVITY 0.6

uniform sampler2D DiffuseSampler0;
uniform sampler2D WaterFrameBuffer;
uniform sampler2D HandDepth;
uniform sampler2D WaterDepth;
uniform sampler2D OpaqueDepth;
uniform sampler2D UnderWaterBuffer;
uniform sampler2D WaterTexture;
uniform sampler2D CausticsTexture;
uniform usampler2D TransparentMatSampler;
uniform usampler2D OpaqueMatSampler;
uniform sampler2D NormalTexture;
uniform sampler2D NormalSampler;
uniform sampler2D OpaqueNormalSampler;
uniform sampler2D ShadowSampler;
uniform sampler2D VanillaWater;

uniform vec2 ScreenSize;
uniform float GameTime;
uniform int OverWorld;
uniform mat4 viewMatrix;
uniform mat4 orthographMatrix;
uniform mat4 IShadowViewMatrix;
uniform float sunsetTimer;
uniform int blockReflections;

in vec2 texCoord;
out vec4 fragColor;

const float rayStep = 0.04;
const int maxSteps = 175;


float brightness(vec4 color){
    return (color.r + color.g + color.b) / 3.0;
}

float blendOverlay(float base, float blend) {
    return base<0.5?(2.0*base*blend):(1.0-2.0*(1.0-base)*(1.0-blend));
}

vec3 blendOverlay(vec3 base, vec3 blend) {
    return vec3(blendOverlay(base.r,blend.r),blendOverlay(base.g,blend.g),blendOverlay(base.b,blend.b));
}

vec3 hash(vec3 p){
    p = vec3( dot(p,vec3(127.1,311.7, -74.7)),
    dot(p,vec3(269.5,183.3,-246.1)),
    dot(p,vec3(-113.5,271.9,124.6)));

    return fract(sin(p)*438.5453123);
}

vec2 rayMarch(vec3 dir, vec3 origin, float ditherMult){
    float posDepth = 0.0;
    float dDepth = 0.0;
    vec3 Pos = origin;
    float dist = 0.0;
    vec4 projectedCoords = vec4(0.0);
    vec3 Direction = dir;

    for(int i = 0; i <= maxSteps; i++) {
        dist += rayStep + smoothstep(0.0, 1.0, float(i) / maxSteps);
        Pos = origin + Direction * dist;

        projectedCoords = VeilCamera.ProjMat * vec4(Pos, 1.0);
        projectedCoords.xyz /= projectedCoords.w;
        projectedCoords = projectedCoords * 0.5 + 0.5;
        posDepth = texture(WaterDepth, projectedCoords.xy).r;

        if (projectedCoords.x < 0.0 || projectedCoords.x > 1.0 || projectedCoords.y < 0.0 || projectedCoords.y > 1.0) {
            return vec2(-1.0);
        }

        dDepth = Pos.z - posDepth;
        //Hit
        if (projectedCoords.z > posDepth){
            if((projectedCoords.z - posDepth) > 0.001){
                return vec2(-1.0);
            }
            return projectedCoords.xy;
        }


    }
    return projectedCoords.xy;
}

vec4 getReflection(vec4 fragColor, vec4 normal, float depth, vec2 texCoord, vec3 viewPos, float ditherMult){
    vec3 reflected = normalize(reflect(normalize(viewPos), normalize(normal.rgb)));
    vec3 worldSpace = viewToWorldSpace(viewPos);
    vec3 jitter = (hash(worldSpace) * 2.0 - 1.0) * ditherMult;
    vec2 projectedCoord = rayMarch(jitter + reflected * max(rayStep, -viewPos.z), viewPos, ditherMult);
    //Out of the screen, if this isn't included reflections form weird artifacts
    if(projectedCoord.x == -1.0){
        return fragColor;
    }
    vec3 reflectedTexture = texture(DiffuseSampler0, projectedCoord).rgb;

    vec2 dCoords = smoothstep(0.4, 0.55, abs(vec2(0.5) - projectedCoord));

    float screenEdgefactor = clamp(1.0f - (dCoords.x + dCoords.y), 0.0f, 1.0f);

    float ReflectionMultiplier = screenEdgefactor * (reflected.b);
    return mix(fragColor, mix(fragColor, vec4(reflectedTexture, 1.0) * clamp(0.0, 1.0, -ReflectionMultiplier), -ReflectionMultiplier), clamp(REFLECTIVITY, 0.0, 1.0));
}

vec4 getCaustics(vec2 color, vec3 opaqueWorldPos){
    vec4 underWater = texture(UnderWaterBuffer, texCoord);
    float scale = 0.1;
    float mult = 0.1;
    vec3 opaqueNormal = texture(OpaqueNormalSampler, texCoord).rgb;
    opaqueNormal = abs(viewToWorldSpaceDirection(opaqueNormal));

    vec4 caustics = (texture(CausticsTexture, (opaqueWorldPos.yz * scale + GameTime * 40.0) + color * REFRACTION_MULTIPLIER) * mult) * opaqueNormal.r;
    caustics += (texture(CausticsTexture, (opaqueWorldPos.xz * scale + GameTime * 40.0) + color * REFRACTION_MULTIPLIER) * mult) * opaqueNormal.g;
    caustics += (texture(CausticsTexture, (opaqueWorldPos.xy * scale + GameTime * 40.0) + color * REFRACTION_MULTIPLIER) * mult) * opaqueNormal.b;

    caustics += (texture(CausticsTexture, (opaqueWorldPos.yz * scale - vec2(GameTime * 40.0, -GameTime * 10.0)) + color * REFRACTION_MULTIPLIER) * mult) * opaqueNormal.r;
    caustics += (texture(CausticsTexture, (opaqueWorldPos.xz * scale - vec2(GameTime * 40.0, -GameTime * 10.0)) + color * REFRACTION_MULTIPLIER) * mult) * opaqueNormal.g;
    caustics += (texture(CausticsTexture, (opaqueWorldPos.xy * scale - vec2(GameTime * 40.0, -GameTime * 10.0)) + color * REFRACTION_MULTIPLIER) * mult) * opaqueNormal.b;

    return caustics * brightness(underWater) * 2.0;
}



void main() {
    vec3 cameraPos = VeilCamera.CameraPosition;
    float isReflective = texture(WaterFrameBuffer, texCoord).r;
    float handDepth = texture(HandDepth, texCoord).r;
    float waterDepth = texture(WaterDepth, texCoord).r;
    uint material = texture(TransparentMatSampler, texCoord).r;
    uint opaqueMaterial = texture(OpaqueMatSampler, texCoord).r;
    vec4 normalSampler = texture(NormalSampler, texCoord);
    vec4 opaqueNormalSampler = texture(OpaqueNormalSampler, texCoord);
    vec4 vanillaWater = texture(VanillaWater, texCoord);
    float opaqueDepth = texture(OpaqueDepth, texCoord).r;

    vec3 viewPos = viewPosFromDepth(waterDepth, texCoord);

    vec2 color = vec2(0.0);
    vec2 color2 = vec2(0.0);

    vec4 normal = vec4(0.0);
    vec4 normal2 = vec4(0.0);
    if ((isReflective > 0.0 || opaqueMaterial == 18) && isBlock(material)) {
        if(OverWorld != 1){
            vec3 playerSpace = mat3(VeilCamera.IViewMat) * viewPos;
            vec3 worldPos = playerSpace + cameraPos;

            vec3 opaqueViewPos = viewPosFromDepth(opaqueDepth, texCoord + color * REFRACTION_MULTIPLIER);
            vec3 opaquePlayerSpace = mat3(VeilCamera.IViewMat) * opaqueViewPos;
            vec3 opaqueWorldPos = opaquePlayerSpace + VeilCamera.CameraPosition;
            vec3 shadowScreenSpace = getShadowCoords(opaquePlayerSpace, viewMatrix, orthographMatrix);
            float shadowDepth = shadowScreenSpace.z;
            float shadowSampler = texture(ShadowSampler, shadowScreenSpace.xy).r;
            float shadow = step(shadowDepth, shadowSampler);

            if(opaqueMaterial == 18 && !(isReflective > 0.0)) {
                float reflectionSize = 0.25;
                //Only allow pixels closer to the center of the screen to cast reflections to boost performace
                if(blockReflections == 1) {
                    if(opaqueNormalSampler.b < 0.6 && texCoord.x > reflectionSize && texCoord.x < 1.0 - reflectionSize) {
                        //Uncomment top and comment out bottom to see block reflection debug
                        //fragColor = mix( vec4(1,0,0,1), vec4(0), smoothstep(0.0, 0.52, abs(texCoord.x * 2.0 - 1.0)) );
                        fragColor = mix(getReflection(texture(DiffuseSampler0, texCoord), opaqueNormalSampler, waterDepth, texCoord, viewPos, 0.1) * 0.3, vec4(0), smoothstep(0.0, 0.52, abs(texCoord.x * 2.0 - 1.0)) );
                    }
                }
                return;
            }

            color = texture(WaterTexture, worldPos.xz * 0.05 + vec2(GameTime * 50.0)).rg - 0.5;
            color2 = texture(WaterTexture, worldPos.xz * 0.05 - vec2(0.0, GameTime * 50.0)).rg - 0.5;
            color = color + color2;

            normal = texture(NormalTexture, worldPos.xz * 0.2 + vec2(GameTime * 50.0));
            normal2 = texture(NormalTexture, worldPos.xz * 0.2 - vec2(0, GameTime * 50.0));
            normal2 += texture(NormalTexture, worldPos.xz * 0.2 - vec2(- GameTime * 103.235456, GameTime * 50.0));
            normal = (normal + normal2) / 3.0;
            normal = vec4(normal.r, normal.b, normal.g, normal.a) * 2.0 - 1.0;


            fragColor = texture(DiffuseSampler0, texCoord + color * REFRACTION_MULTIPLIER);

            fragColor = getReflection(fragColor, mix(vec4(worldToViewSpaceDirection(normalize(vec3(0.0, 1.0,0.0))), 1.0), normal, 0.02), waterDepth, texCoord, viewPos, 0.0) * vec4(vec3(0.0, 1.2, 1.2), 1.0);

            if (shadow >= 1.0){
                if(sunsetTimer <= 0.27 || (sunsetTimer >= 0.46 && sunsetTimer <= 0.67) || sunsetTimer >= 0.70){
                    vec3 lightangle = (viewMatrix * vec4(0.0, 0.0, 1.0, 0.0)).xyz;
                    lightangle.y = - lightangle.y;

                    vec3 reflectedView = reflect(viewDirFromUv(texCoord), normalize(normal.rgb));
                    float specular = dot(reflectedView, normalize(getLightAngle(IShadowViewMatrix)));
                    specular = pow(specular, 100.0);
                    specular *= 5.0;

                    if (specular > 0.0){
                        fragColor += specular;
                    }

                    vec4 caustics = getCaustics(color, opaqueWorldPos);
                    fragColor += clamp(caustics, 0.0, 1.0) * 2.0;
                }
            }
        } else {
            fragColor = vanillaWater;
        }
    } else {
        fragColor = vec4(0.0);
    }
}

