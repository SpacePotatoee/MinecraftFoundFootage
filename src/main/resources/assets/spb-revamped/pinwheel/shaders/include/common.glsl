#include spb-revamped:shadows
#include veil:camera

//Conversions from https://www.shadertoy.com/view/3lycWz
vec3 rgb2yuv(vec3 rgb){
    float y = 0.299*rgb.r + 0.587*rgb.g + 0.114*rgb.b;
    return vec3(y, 0.493*(rgb.b-y), 0.877*(rgb.r-y));
}

vec3 yuv2rgb(vec3 yuv){
    float y = yuv.x;
    float u = yuv.y;
    float v = yuv.z;

    return vec3(
    y + 1.0/0.877*v,
    y - 0.39393*u - 0.58081*v,
    y + 1.0/0.493*u
    );
}


vec4 blur(float kernalSize, float offset, sampler2D textureSampler, vec2 texCoord){
    vec4 blur = vec4(0.0);
    float halfSize = (kernalSize - 1.0) / 2.0;
    float coeff = 1.0 / (kernalSize * kernalSize);
    vec2 dx = vec2(offset, 0.0);
    vec2 dy = vec2(0.0, offset);

    for (float x = -halfSize; x <= halfSize; x++) {
        for (float y = -halfSize; y <= halfSize; y++) {
            blur += (coeff * 0.5) * texture(textureSampler, texCoord + x * dx + y * dy);
        }
    }
    return blur;
}

bool ddaRayMarch(vec3 offset, vec3 pos, mat4 viewMatrix, mat4 orthographMatrix, sampler2D ShadowSampler){
    float maxLength = length(offset);
    vec3 origin = pos;
    ivec3 stepDir = ivec3(0);
    vec3 rayDir = normalize(offset);
    vec3 stepSizes = 1.0 / abs(rayDir);
    vec3 rayLengths = vec3(0);
    bool hit = false;

    if (rayDir.x < 0.0) {
        stepDir.x = -1;
        rayLengths.x = -fract(origin.x) * stepSizes.x;
    } else {
        stepDir.x = 1;
        rayLengths.x = (1.0 - fract(origin.x)) * stepSizes.x;
    }

    if (rayDir.y < 0.0) {
        stepDir.y = -1;
        rayLengths.y = -fract(origin.y) * stepSizes.y;
    } else {
        stepDir.y = 1;
        rayLengths.y = (1.0 - fract(origin.y)) * stepSizes.y;
    }

    if (rayDir.z < 0.0) {
        stepDir.z = -1;
        rayLengths.z = -fract(origin.z) * stepSizes.z;
    } else {
        stepDir.z = 1;
        rayLengths.z = (1.0 - fract(origin.z)) * stepSizes.z;
    }

    vec3 rayPos = origin;
    vec3 blockPos = floor(origin);
    for(int i = 0; i < 20; i++){
        if(length(origin - rayPos) > maxLength){
            break;
        }

        vec3 centerPos = blockPos + 0.5;

        vec3 playerSpace = centerPos - VeilCamera.CameraPosition;
        vec3 shadowScreenSpace = getShadowCoords(playerSpace, viewMatrix, orthographMatrix);
        float shadowDepth = shadowScreenSpace.z;
        float shadowSampler = texture(ShadowSampler, shadowScreenSpace.xy).r;

        if (shadowDepth > shadowSampler){
            hit = true;
            break;
        }

        vec3 totalRayLengths = abs(rayLengths);
        if(totalRayLengths.x < totalRayLengths.y && totalRayLengths.x < totalRayLengths.z){
            blockPos.x += stepDir.x;
            rayPos = origin + rayDir * rayLengths.x;
            rayLengths.x += (stepSizes.x * stepDir.x);
        } else if(totalRayLengths.y < totalRayLengths.x && totalRayLengths.y < totalRayLengths.z){
            blockPos.y += stepDir.y;
            rayPos = origin + rayDir * rayLengths.y;
            rayLengths.y += (stepSizes.y * stepDir.y);
        } else if(totalRayLengths.z < totalRayLengths.x && totalRayLengths.z < totalRayLengths.y){
            blockPos.z += stepDir.z;
            rayPos = origin + rayDir * rayLengths.z;
            rayLengths.z += (stepSizes.z * stepDir.z);
        }
    }

    return hit;
}

vec4 ddaRayMarchDir(vec3 dir, vec3 pos, mat4 viewMatrix, mat4 orthographMatrix, sampler2D ShadowSampler, out vec3 normal){
    float maxLength = 5;
    vec3 origin = pos;
    ivec3 stepDir = ivec3(0);
    vec3 rayDir = normalize(dir);
    vec3 stepSizes = 1.0 / abs(rayDir);
    vec3 rayLengths = vec3(0);
    bool hit = false;

    if (rayDir.x < 0.0) {
        stepDir.x = -1;
        rayLengths.x = -fract(origin.x) * stepSizes.x;
    } else {
        stepDir.x = 1;
        rayLengths.x = (1.0 - fract(origin.x)) * stepSizes.x;
    }

    if (rayDir.y < 0.0) {
        stepDir.y = -1;
        rayLengths.y = -fract(origin.y) * stepSizes.y;
    } else {
        stepDir.y = 1;
        rayLengths.y = (1.0 - fract(origin.y)) * stepSizes.y;
    }

    if (rayDir.z < 0.0) {
        stepDir.z = -1;
        rayLengths.z = -fract(origin.z) * stepSizes.z;
    } else {
        stepDir.z = 1;
        rayLengths.z = (1.0 - fract(origin.z)) * stepSizes.z;
    }

    vec3 rayPos = origin;
    vec3 blockPos = floor(origin);
    for(int i = 0; i < 8; i++){
        if(length(origin - rayPos) > maxLength){
            break;
        }

        vec3 centerPos = blockPos + 0.5;

        vec3 playerSpace = centerPos - VeilCamera.CameraPosition;
        vec3 shadowScreenSpace = getShadowCoords(playerSpace, viewMatrix, orthographMatrix);
        float shadowDepth = shadowScreenSpace.z;
        float shadowSampler = texture(ShadowSampler, shadowScreenSpace.xy).r;

        if (shadowDepth > shadowSampler){
            hit = true;
            break;
        }

        vec3 totalRayLengths = abs(rayLengths);
        if(totalRayLengths.x < totalRayLengths.y && totalRayLengths.x < totalRayLengths.z){
            blockPos.x += stepDir.x;
            rayPos = origin + rayDir * rayLengths.x;
            rayLengths.x += (stepSizes.x * stepDir.x);
            normal = vec3(-stepDir.x, 0.0, 0.0);
        } else if(totalRayLengths.y < totalRayLengths.x && totalRayLengths.y < totalRayLengths.z){
            blockPos.y += stepDir.y;
            rayPos = origin + rayDir * rayLengths.y;
            rayLengths.y += (stepSizes.y * stepDir.y);
            normal = vec3(0.0, -stepDir.y, 0.0);
        } else if(totalRayLengths.z < totalRayLengths.x && totalRayLengths.z < totalRayLengths.y){
            blockPos.z += stepDir.z;
            rayPos = origin + rayDir * rayLengths.z;
            rayLengths.z += (stepSizes.z * stepDir.z);
            normal = vec3(0.0, 0.0, -stepDir.z);
        }
    }

    vec4 returnValue = vec4(rayPos, 0.0);
    if(hit == true){
        returnValue.w = 1.0;
    }
    return returnValue;
}

float random (vec2 st) {
    float p = fract(sin(dot(st.xy,vec2(0.0,300.233))));
    return p;
}


//Hash Functions from https://www.shadertoy.com/view/4djSRW
float hash12(vec2 p){
    vec3 p3  = fract(vec3(p.xyx) * 0.1031);
    p3 += dot(p3, p3.yzx + 33.33);
    return fract((p3.x + p3.y) * p3.z);
}

vec2 hash22(vec2 p){
    vec3 p3 = fract(vec3(p.xyx) * vec3(.1031, .1030, .0973));
    p3 += dot(p3, p3.yzx+33.33);
    return fract((p3.xx+p3.yz)*p3.zy);
}

vec2 hash2d(vec2 p){
    vec3 p3 = fract(vec3(p.xyx));
    p3 += dot(p3, p3.yzx+19.19);
    return fract((p3.xx+p3.yz)*p3.zy);
}

float octave(float x){
    return mod(sin(x * 2.0) * sin(x * 4.0) * sin(x * 32.0), 1.0);
}