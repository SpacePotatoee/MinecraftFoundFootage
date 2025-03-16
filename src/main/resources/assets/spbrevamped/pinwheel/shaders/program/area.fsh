//#include veil:common
#include veil:deferred_utils
#include veil:color_utilities
#include veil:light
#include spb-revamped:shadows
#include spb-revamped:common
#include spb-revamped:puddles
#include veil:camera

#define OFFSET vec2(0.1965249, 0.6546237)

in mat4 lightMat;
in vec3 lightColor;
in vec2 size;
in float maxAngle;
in float maxDistance;
in vec3 lightWorldPosition;

uniform sampler2D AlbedoSampler;
uniform sampler2D NormalSampler;
uniform sampler2D DiffuseDepthSampler;
uniform sampler2D ShadowSampler;
uniform sampler2D FogTexture;

uniform mat4 viewMatrix;
uniform mat4 orthographMatrix;
uniform vec2 ScreenSize;
uniform float gameTime;

out vec4 fragColor;



// acos approximation
// faster and also doesn't flicker weirdly
float sacos( float x ){
    float y = abs( specialClamp2(x,-1.0,1.0) );
    float z = (-0.168577*y + 1.56723) * sqrt(1.0 - y);
    return mix( 0.5*3.1415927, z, sign(x) );
}

struct AreaLightResult {
    vec3 position;
    float angle;
};

AreaLightResult closestPointOnPlaneAndAngle(vec3 point, mat4 planeMatrix, vec2 planeSize) {
    // no idea why i need to do this
    planeMatrix[3].xyz *= -1.0;
    // transform the point to the plane's local space
    vec3 localSpacePoint = (planeMatrix * vec4(point, 1.0)).xyz;
    // clamp position
    vec3 localSpacePointOnPlane = vec3(clamp(localSpacePoint.xy, -planeSize, planeSize), 0);

    // calculate the angles
    vec3 direction = normalize(localSpacePoint - localSpacePointOnPlane);
    float angle = sacos(dot(direction, vec3(0.0, 0.0, 1.0)));

    // transform back to global space
    return AreaLightResult((inverse(planeMatrix) * vec4(localSpacePointOnPlane, 1.0)).xyz, angle);
}

vec4 setColor(vec4 albedoColor, vec3 normalVS, vec3 offset, float angle){
    vec3 lightDirection = (VeilCamera.ViewMat * vec4(normalize(offset), 0.0)).xyz;
    float diffuse = (dot(normalVS, lightDirection) + 1.0) * 0.5;
    diffuse = (diffuse + MINECRAFT_AMBIENT_LIGHT) / (1.0 + MINECRAFT_AMBIENT_LIGHT);
    diffuse *= attenuate_no_cusp(length(offset), maxDistance);
    // angle falloff
    float angleFalloff = specialClamp2(angle, 0.0, maxAngle) / maxAngle;
    angleFalloff = smoothstep(1.0, 0.0, angleFalloff);
    diffuse *= angleFalloff;

    float reflectivity = 0.1;
    vec3 diffuseColor = diffuse * lightColor;

    return vec4(albedoColor.rgb * diffuseColor * (1.0 - reflectivity) + diffuseColor * reflectivity, albedoColor.a);
}

float noise3D(vec3 p){
    float z = p.z;
    vec2 z1 = (floor(z) * OFFSET + p.xz)/5.0;
    vec2 z2 = ((floor(z) + 1.0) * OFFSET + p.xz)/5.0;
    float n1 = texture(FogTexture, z1 + gameTime * 100.0).r;
    float n2 = texture(FogTexture, z2 + gameTime * 100.0).r;
    float ratio = fract(z);
    return mix(n1, n2, ratio);
}

void main() {
    vec2 screenUv = gl_FragCoord.xy / ScreenSize;

    vec4 albedoColor = texture(AlbedoSampler, screenUv);
    if(albedoColor.a == 0) {
        discard;
    }


    vec3 normalVS = texture(NormalSampler, screenUv).xyz;
    vec3 worldNormal = viewToWorldSpaceDirection(normalVS);
    float depth = texture(DiffuseDepthSampler, screenUv).r;
    vec3 viewPos = viewPosFromDepth(depth, screenUv);
    vec3 pos = viewToWorldSpace(viewPos);

    // lighting calculation
    AreaLightResult areaLightInfo = closestPointOnPlaneAndAngle(pos, lightMat, size);
    vec3 lightPos = areaLightInfo.position;
    float angle = areaLightInfo.angle;
    vec3 offset = lightPos - pos;

    if(pos.y > 40.6 || pos.y < 20.5) {
        fragColor = setColor(albedoColor, normalVS, offset, angle);
        return;
    }

    //If the pixel isn't in range, there's no point in doing any calculations
    if(abs(length(offset)) > maxDistance) {
        fragColor = vec4(0.0);
        return;
    }

    vec3 offsetPos = vec3(pos.x + (0.01 * worldNormal.r), pos.y + (0.01 * worldNormal.g), pos.z + (0.01 * worldNormal.b));

    vec3 rayDir = viewDirFromUv(screenUv);
    float dist = 0.0;
    float brightness = 0.0;
    bool hit = ddaRayMarch(offset, offsetPos, viewMatrix, orthographMatrix, ShadowSampler);

    if(hit == false){
        fragColor = setColor(albedoColor, normalVS, offset, angle);
    } else {
        fragColor = vec4(0.0);
    }

}