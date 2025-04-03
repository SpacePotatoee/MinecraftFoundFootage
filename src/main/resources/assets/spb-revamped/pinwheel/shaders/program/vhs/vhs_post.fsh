#include veil:camera
#include veil:deferred_utils
#include spb-revamped:common

uniform sampler2D preSampler;
uniform sampler2D DiffuseSampler0;
uniform sampler2D TotalDepth;
uniform sampler2D MidSampler;
uniform sampler2D VhsNoise;
uniform sampler2D NoEscape;
uniform sampler2D CreepyFace1Image;
uniform sampler2D CreepyFace2Image;

uniform float GameTime;

uniform int youCantEscape;
uniform int Jumpscare;
uniform int CreepyFace1;
uniform int CreepyFace2;
uniform vec2 Rand;

uniform mat4 prevViewMat;
uniform mat4 prevProjMat;
uniform vec3 prevCameraPos;
uniform float MotionBlurStrength;
uniform float DistortionStrength;

in vec2 texCoord;
out vec4 fragColor;

//Parts of this code from: https://agatedragon.blog/2023/12/24/barrel-distortion-shader/
//and https://www.shadertoy.com/view/XtlSD7
vec2 BarrelDistortionCoordinates(vec2 uv){
    vec2 pos = 2.0f * uv - 1.0f;

    float len = distance(pos, vec2(0.0f));
    len = pow(len/1.5f, 1.0f) * DistortionStrength;

    pos = pos + pos * len * len;

    pos = 0.5f * (pos + 1.0f);

    return pos;
}

vec4 Viginette(vec2 uv){
    uv = 2.0f * uv - 1.0f;
    float disty = abs(distance(1*uv, vec2(0,0))-2);
    uv = 0.5f * (uv + 1.0f);
    return vec4(disty);
}

void main() {
    vec2 uv = BarrelDistortionCoordinates(texCoord);
//    vec2 uv = texCoord;
    vec4 Distortion = texture(DiffuseSampler0, uv);
    vec4 viginette = Viginette(uv);

    float depth = texture(TotalDepth, uv).r;
    vec3 positionVS = viewPosFromDepthSample(depth, uv);
    vec3 NDCPos = projectAndDivide(VeilCamera.ProjMat, positionVS);

    vec3 cameraOffset = prevCameraPos - VeilCamera.CameraPosition;
    vec3 playerSpace = viewToPlayerSpace(positionVS);

    vec3 prevViewPos = (prevViewMat * vec4(playerSpace - cameraOffset, 1.0)).xyz;
    vec3 prevNDCPos = projectAndDivide(prevProjMat, prevViewPos);

    vec2 velocity = (NDCPos - prevNDCPos).xy;

    //Motion Blur
    vec4 blur3 = vec4(0.0);
    #ifdef MOTION_BLUR
        const float kernalSize3 = 5.0;
        const float coeff3 = 1.0 / (kernalSize3 * kernalSize3);
        for(float x = -1.0; x <= 1.0; x += coeff3){
            blur3 += coeff3 * texture(DiffuseSampler0, uv - vec2(velocity.x * x, velocity.y * x) * MotionBlurStrength * 0.25) * 0.5;
        }
    #else
        blur3 = texture(DiffuseSampler0, uv);
    #endif


    if(youCantEscape == 0) {
        fragColor = blur3;
    } else {
        vec2 uv2 = vec2(uv.x + octave(uv.y + GameTime * 2000.0) * 0.01, uv.y);

        vec2 offset = uv2 + ((hash12(uv2 * 260.23535 + GameTime * 70.0)) * 0.005) + ((hash12(vec2(GameTime * 4562.0))) * 0.01);

        float red = texture(NoEscape, offset + 0.001).r;
        float green = texture(NoEscape, offset - 0.001).g;
        float blue = texture(NoEscape, offset).b;


        fragColor = vec4(red, green, blue, 1.0);
    }

    if(Jumpscare == 1) {
        fragColor = vec4(0.0, 0.0, 0.0, 1.0);

        if(CreepyFace1 == 1) {
            fragColor = texture(CreepyFace1Image, texCoord);
        }

        if(CreepyFace2 == 1) {
            fragColor = texture(CreepyFace2Image, texCoord + Rand * 0.01) + Rand.x;
        }
    }

    //VHS POSST EFFECTS
    fragColor.rgb = rgb2yuv(fragColor.rgb);
    fragColor.rgb += (fragColor.rgb * vec3((hash12(uv * 260.23535 + GameTime * 70.0) + hash12(uv * 737.36346 + GameTime * 100.0)) * 2.0 - 1.0)) * 0.05;
    fragColor.r += step(0.99994, (hash12(uv * 260.23535 + GameTime * 70.0))) * 10.0;
    vec2 vhsNoise = texture(VhsNoise, vec2(uv.x - GameTime * 3000.0, uv.y + GameTime * 5000.0)).gb * 0.1;
    fragColor.gb += vec2(vhsNoise.x * 0.9, vhsNoise.y * 0.9) * 0.2;
    fragColor.rgb = yuv2rgb(fragColor.rgb);
}

