#include veil:camera

uniform sampler2D WindNoise;
uniform float GameTime;
uniform float grassHeight;


out vec4 fragColor;
in vec3 localPos;
in vec3 WorldPos;

float getGrassHeightGradient(float height){
    return height / grassHeight;
}

void main() {
//    discard;
    vec3 worldPos = localPos + VeilCamera.CameraPosition;
    float grassGradient = getGrassHeightGradient(worldPos.y + 60);
    vec3 grassColor = mix(vec3(0.0, 0.4, 0.0), vec3(0.5, 0.7, 0.0), grassGradient);
    float occlusionFactor = clamp(grassGradient, 0.2, 1.0);


    fragColor = vec4(grassColor * occlusionFactor, 1.0);
//    fragColor = vec4(WorldPos, 1.0);
}