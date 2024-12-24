#include veil:camera
#include veil:deferred_utils
#include spb-revamped:common

uniform sampler2D preSampler;
uniform sampler2D DiffuseSampler0;
uniform sampler2D DiffuseDepthSampler;
uniform sampler2D MidSampler;
uniform sampler2D VhsNoise;
uniform sampler2D NoEscape;
uniform sampler2D CreepyFace1Image;
uniform sampler2D CreepyFace2Image;

uniform vec2 Velocity;
uniform float GameTime;

uniform int youCantEscape;
uniform int Jumpscare;
uniform int CreepyFace1;
uniform int CreepyFace2;
uniform vec2 Rand;


in vec2 texCoord;
out vec4 fragColor;

//Parts of this code from: https://agatedragon.blog/2023/12/24/barrel-distortion-shader/
//and https://www.shadertoy.com/view/XtlSD7
vec2 BarrelDistortionCoordinates(vec2 uv){
    vec2 pos = 2.0f * uv - 1.0f;

    float len = distance(pos, vec2(0.0f));
    len = pow(len/2.0f, 1.0f);

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
    vec4 Distortion = texture(DiffuseSampler0, uv);
    vec4 viginette = Viginette(uv);

    float depth = texture(DiffuseDepthSampler, uv).r;
    vec3 positionVS = viewPosFromDepthSample(depth, uv);
    float worldDepth = length(positionVS);

    vec4 blur2 = vec4(0.0);
//    const float kernalSize2 = 21.0;
//    const float halfSize2 = 10.0;
//    const float coeff2 = 1.0 / (kernalSize2 * kernalSize2);
//    const vec2 dx2 = vec2(0.0002, 0.0);
//    const vec2 dy2 = vec2(0.0, 0.0002);
//    for(float x = -halfSize2; x<= halfSize2; x++){
//        for(float y = -halfSize2; y<= halfSize2; y++){
//            blur2 += (coeff2) * texture(DiffuseSampler0, uv + x * dx2 + y * dy2);
//        }
//    }

    //Motion Blur
    vec4 blur3 = vec4(0.0);
    const float kernalSize3 = 5.0;
    const float coeff3 = 1.0 / (kernalSize3 * kernalSize3);
    for(float x = -1.0; x <= 1.0; x += coeff3){
        blur3 += coeff3 * texture(DiffuseSampler0, uv - vec2(Velocity.x * x, Velocity.y * x) * 0.1) * 0.5;
    }

    //TV OVERLAY
    if(uv.x < -0.05 || uv.x > 1.05 || uv.y < -0.05 || uv.y > 1.05){

        fragColor = vec4(vec3(0.1),1.0f);
        fragColor *= viginette+0.4;

    }
    else if(uv.y > 2.0*uv.x-uv.y && uv.x > 1.0 || uv.x > 2.0*uv.y-uv.x && uv.x < 0.0 ||  uv.y > -2.0*uv.x-uv.y+2.0 && uv.x < 0.0 || uv.y < -2.0*uv.x-uv.y+2.0 && uv.x > 1.0 ){
        uv = 2*uv-1;

        float disty = abs(distance(uv.y, 0.0)-1.5);
        fragColor = vec4(vec3(disty),1.0f)-0.35;

        uv = uv/2.0 + 0.5;
        fragColor *= viginette+0.3;
    }
    else if(uv.x < 0 || uv.x > 1){
        uv = 2.0*uv-1.0;

        float distx = abs(distance(uv.x, 0.0)-1.5);
        fragColor = vec4(vec3(distx),1.0f)-0.35;

        uv = uv/2.0 + 0.5;
        fragColor *= viginette+0.3;
    }
    else if(uv.y < 0.0f || uv.y > 1.0f){
        uv = 2.0*uv-1;

        float disty = abs(distance(uv.y, 0.0)-1.5);
        fragColor = vec4(vec3(disty),1.0f)-0.35;

        uv = uv/2.0 + 0.5;
        fragColor *= viginette+0.3;
    }else {
        if(youCantEscape == 0) {
            fragColor = (blur2 + blur3);
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
        fragColor.rgb += (fragColor.rgb * vec3((hash12(uv * 260.23535 + GameTime * 70.0) + hash12(uv * 737.36346 + GameTime * 100.0)) * 2.0 - 1.0)) * 0.2;
        fragColor.r += step(0.99994, (hash12(uv * 260.23535 + GameTime * 70.0))) * 10.0;
        vec2 vhsNoise = texture(VhsNoise, vec2(uv.x - GameTime * 3000.0, uv.y + GameTime * 5000.0)).gb * 0.1;
        fragColor.gb += vec2(vhsNoise.x * 0.9, vhsNoise.y * 0.9) * 0.2;
        fragColor.rgb = yuv2rgb(fragColor.rgb);
    }
}

