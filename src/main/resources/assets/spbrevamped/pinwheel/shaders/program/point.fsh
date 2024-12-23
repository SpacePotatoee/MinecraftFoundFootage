#include veil:deferred_utils
#include veil:color_utilities
#include veil:light
#include spb-revamped:shadows
#include spb-revamped:common
#include veil:camera
#include veil:material

in vec3 lightPos;
in vec3 lightColor;
in float radius;
in float shouldRenderShadows;

uniform sampler2D AlbedoSampler;
uniform sampler2D NormalSampler;
uniform sampler2D DiffuseDepthSampler;
uniform sampler2D ShadowSampler;
uniform sampler2D RNoiseDir;
uniform sampler2D LightSampler;
uniform usampler2D MatSampler;


uniform mat4 viewMatrix;
uniform mat4 orthographMatrix;
uniform vec3 lightAngled;
uniform vec2 ScreenSize;
uniform float GameTime;
uniform int CutsceneActive;

out vec4 fragColor;

const int MaxShadowSoftness = 30;

float getSign(float num){
    if(num >= 0.0){
        return 1.0;
    } else {
        return -1.0;
    }
}

float specialClamp(float a, float b, float c){
    if(c > b){
        return b;
    }
    else if(c < a){
        return a;
    }

    return c;
}

vec4 setColor(vec4 albedoColor, vec3 normalVS, vec3 offset, float light) {
    vec3 lightDirection = (VeilCamera.ViewMat * vec4(normalize(offset), 0.0)).xyz;
    float diffuse = specialClamp(0.0, 1.0, dot(normalVS, lightDirection));
    diffuse = (diffuse + MINECRAFT_AMBIENT_LIGHT) / (1.0 + MINECRAFT_AMBIENT_LIGHT);
    diffuse *= attenuate_no_cusp(length(offset), radius);

    float reflectivity = 0.1;
    vec3 diffuseColor = diffuse * lightColor;
    return vec4((albedoColor.rgb * diffuseColor * (1.0 - reflectivity) + diffuseColor * reflectivity) * light, albedoColor.a);
}

void main() {
    vec2 screenUv = gl_FragCoord.xy / ScreenSize;

    vec4 albedoColor = texture(AlbedoSampler, screenUv);

    if(albedoColor.a == 0.0) {
        discard;
    }

    float depth = texture(DiffuseDepthSampler, screenUv).r;
    vec3 pos = viewToWorldSpace(viewPosFromDepth(depth, screenUv));
    vec3 normalVS = texture(NormalSampler, screenUv).xyz;
    vec3 worldNormal = viewToWorldSpaceDirection(normalVS);
    vec3 offset = lightPos - pos;

    vec3 tangent = normalize(cross(worldNormal, normalize(vec3(1.0))));
    vec3 bitangent = normalize(cross(worldNormal, tangent));

    mat3 TBN = mat3(tangent, bitangent, worldNormal);
    TBN = transpose(TBN);

    float maxHeight = 40.6;
    if(CutsceneActive == 1) {
        maxHeight = 25.5;
    }

    uint material = texture(MatSampler, screenUv).g;
    if(pos.y > maxHeight || pos.y < -50.5 || lightPos.y < 20.5){
        fragColor = setColor(albedoColor, normalVS, offset, 1.0);
        return;
    }

    //If the pixel isn't in range, there's no point in doing any calculations
    if(abs(length(offset)) > radius){
        fragColor = setColor(albedoColor, normalVS, offset, 1.0);
        return;
    }

    float light = 0.0;
    float steps = 0.0;
    vec3 offsetPos = vec3(pos.x + (0.009 * worldNormal.r), pos.y + (0.009 * worldNormal.g), pos.z + (0.009 * worldNormal.b));

    for (int i = 0; i < 2; i++){
        vec3 normalRayOffset = vec3((hash22(screenUv * (i+1) * 453.346) * 2.0 - 1.0) * 0.005 * length(offset), 0.0);
        normalRayOffset = (normalRayOffset * TBN) + offsetPos;

        bool hit = ddaRayMarch(offset, normalRayOffset, viewMatrix, orthographMatrix, ShadowSampler);

        if (hit == false){
            light += 1.0;
        }
        steps++;
    }
    light = light / steps;

    fragColor = setColor(albedoColor, normalVS, offset, light);
}