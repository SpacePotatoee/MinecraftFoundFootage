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
uniform usampler2D MaterialSampler;
uniform sampler2D NormalTexture;
uniform sampler2D NormalSampler;
uniform sampler2D OpaqueNormalSampler;
uniform sampler2D ShadowSampler;
uniform sampler2D MinecraftMain;
uniform float GameTime;
uniform int OverWorld;
uniform mat4 viewMatrix;
uniform mat4 orthographMatrix;
uniform vec3 lightAngled;

in vec2 texCoord;
out vec4 fragColor;

const float rayStep = 0.05;
const int maxSteps = 125;
const int BinSearchSteps = 10;


float brightness(vec4 color){
return (color.r + color.g + color.b) / 3.0;
}

vec2 rayMarch(vec3 dir, vec3 origin){
float posDepth = 0.0;
float dDepth = 0.0;
vec3 Pos = origin;
vec4 projectedCoords = vec4(0.0);

dir = dir * rayStep;

for(int i = 0; i <= maxSteps; i++){
Pos += dir;

projectedCoords = VeilCamera.ProjMat * vec4(Pos, 1.0);
projectedCoords.xyz /= projectedCoords.w;
projectedCoords = projectedCoords * 0.5 + 0.5;
posDepth = texture(WaterDepth, projectedCoords.xy).r;

if (projectedCoords.x < 0.0 || projectedCoords.x > 1.0 || projectedCoords.y < 0.0 || projectedCoords.y > 1.0) break;

dDepth = Pos.z - posDepth;
//Hit
if (projectedCoords.z > posDepth){
//        float check = smoothstep(0, 1.0, );
if((projectedCoords.z - posDepth) > 0.0008){
continue;
}

//Binary Search
for (int j = 0; j <= BinSearchSteps; j++){
projectedCoords = VeilCamera.ProjMat * vec4(Pos, 1.0);
projectedCoords.xyz /= projectedCoords.w;
projectedCoords = projectedCoords * 0.5 + 0.5;
posDepth = texture(WaterDepth, projectedCoords.xy).r;

dDepth = projectedCoords.z - posDepth;
dir *= 0.5;
if (dDepth > 0.0){
Pos += dir;
}
else{
Pos -= dir;
}
}

projectedCoords = VeilCamera.ProjMat * vec4(Pos, 1.0);
projectedCoords.xyz /= projectedCoords.w;
projectedCoords = projectedCoords * 0.5 + 0.5;
posDepth = texture(WaterDepth, projectedCoords.xy).r;
return projectedCoords.xy;
}

}
return projectedCoords.xy;
}

vec4 getReflection(vec4 fragColor, vec4 normal, float depth, vec2 texCoord, vec3 viewPos, vec2 refraction){
    vec3 reflected = normalize(reflect(normalize(viewPos), normalize(normal.rgb)));
    vec2 projectedCoord = rayMarch(reflected * max(rayStep, -viewPos.z), viewPos);
    vec3 reflectedTexture = texture(DiffuseSampler0, projectedCoord).rgb;

    vec2 dCoords = smoothstep(0.4, 0.55, abs(vec2(0.5) - projectedCoord));

    float screenEdgefactor = clamp(1.0f - (dCoords.x + dCoords.y), 0.0f, 1.0f);

    float ReflectionMultiplier = screenEdgefactor * (reflected.z);

    return mix(fragColor, mix(fragColor, vec4(reflectedTexture, 1.0) * clamp(-ReflectionMultiplier, 0.0, 1.0), -ReflectionMultiplier), clamp(REFLECTIVITY, 0.0, 1.0));
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
    uint material = texture(MaterialSampler, texCoord).r;
    vec4 normalSampler = texture(NormalSampler, texCoord);
    vec4 minecraftMain = texture(MinecraftMain, texCoord);
    float opaqueDepth = texture(OpaqueDepth, texCoord).r;

    vec2 color = vec2(0.0);
    vec2 color2 = vec2(0.0);

    vec4 normal = vec4(0.0);
    vec4 normal2 = vec4(0.0);
    if (isReflective > 0.0 && handDepth == 1.0 && isBlock(material)) {
        vec3 viewPos = viewPosFromDepth(waterDepth, texCoord);
        vec3 playerSpace = viewToPlayerSpace(viewPos);
        vec3 worldPos = playerSpace + cameraPos;

        color = texture(WaterTexture, worldPos.xz * 0.05 + vec2(GameTime * 50.0)).rg - 0.5;
        color2 = texture(WaterTexture, worldPos.xz * 0.05 - vec2(0.0, GameTime * 50.0)).rg - 0.5;
        color = color + color2;

        normal = texture(NormalTexture, worldPos.xz * 0.1 + vec2(GameTime * 50.0));
        normal2 = texture(NormalTexture, worldPos.xz * 0.1 - vec2(0, GameTime * 50.0));
        normal2 += texture(NormalTexture, worldPos.xz * 0.1 - vec2(- GameTime * 103.235456, GameTime * 50.0));
        normal = (normal + normal2) / 3.0;
        normal = vec4(normal.r, normal.b, normal.g, normal.a) * 2.0 - 1.0;

        vec3 shadowScreenSpace = getShadowCoords(playerSpace, viewMatrix, orthographMatrix);
        float shadowDepth = shadowScreenSpace.z;
        float shadowSampler = texture(ShadowSampler, shadowScreenSpace.xy).r;
        float shadow = step(shadowDepth, shadowSampler);

        vec3 opaqueViewPos = viewPosFromDepth(opaqueDepth, texCoord + color * REFRACTION_MULTIPLIER);
        vec3 opaquePlayerSpace = viewToPlayerSpace(opaqueViewPos);
        vec3 opaqueWorldPos = opaquePlayerSpace + VeilCamera.CameraPosition;
        shadowScreenSpace = getShadowCoords(opaquePlayerSpace, viewMatrix, orthographMatrix);
        shadowDepth = shadowScreenSpace.z;
        shadowSampler = texture(ShadowSampler, shadowScreenSpace.xy).r;
        shadow = step(shadowDepth, shadowSampler);


        fragColor = texture(DiffuseSampler0, texCoord + color * REFRACTION_MULTIPLIER);

        float vertexDistance = fog_distance((worldPos - opaqueWorldPos) * 0.4, 0);
        fragColor = linear_fog(fragColor, vertexDistance, 0, 10, vec4(0, 0.15, 0.2, 1.0));

        fragColor = getReflection(fragColor, mix(normalSampler, normal, 0.05), waterDepth, texCoord, viewPos, color) * vec4(0.0, 1.2, 1.15, 1.0);


        if (shadow >= 1.0){
            vec3 lightangle = (viewMatrix * vec4(0.0, 0.0, 1.0, 0.0)).xyz;
            lightangle.y = - lightangle.y;

            vec3 view = -VeilCamera.IViewMat[2].xyz;
            //            view.z = -view.z;

            vec3 reflectedView = reflect(viewDirFromUv(texCoord), normalize(normal.rgb));
            float specular = dot(reflectedView, normalize(lightAngled));
            specular = pow(specular, 20.0);
            specular *= 1.0;

            if (specular > 0.0){
                fragColor += specular;
            }
        }

        if (shadow >= 1.0){
            vec4 caustics = getCaustics(color, opaqueWorldPos);
            fragColor += clamp(caustics, 0.0, 1.0) * 2.0;
        }




    }

}
