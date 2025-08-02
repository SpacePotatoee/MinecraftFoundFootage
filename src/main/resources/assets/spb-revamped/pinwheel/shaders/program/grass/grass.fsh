#include veil:camera
#include veil:deferred_buffers
#include veil:deferred_utils
#include veil:material

uniform sampler2D WindNoise;
uniform float GameTime;
uniform float grassHeight;


//out vec4 fragColor;
in vec3 localPos;
in vec3 normal;


float getGrassHeightGradient(float height){
    return height / grassHeight;
}

void main() {
//    discard;
    vec3 worldPos = localPos + VeilCamera.CameraPosition;
    float grassGradient = getGrassHeightGradient(worldPos.y - 31);
    vec3 grassColor = mix(vec3(0.0, 0.1, 0.0), vec3(0.7, 1.0, 0.5)*0.7, grassGradient);


    #ifdef LEVEL324
        grassGradient = getGrassHeightGradient(worldPos.y - (31+34));
        grassColor = mix(vec3(0.3, 0.2, 0.1), vec3(0.2, 0.2, 0.0)*0.5, grassGradient);
    #endif
    float occlusionFactor = clamp(grassGradient, 0.2, 1.0);


//    fragColor = vec4(grassColor * occlusionFactor, 1.0);
    fragAlbedo = vec4(grassColor * occlusionFactor, 1.0);
    fragNormal = vec4(worldToViewSpaceDirection(normal), 1.0);
    fragMaterial = ivec4(15, 0, 0, 1);
//    fragLightSampler = vec4(texCoord2, 0.0, 1.0);
    fragLightMap = vec4(1);
}