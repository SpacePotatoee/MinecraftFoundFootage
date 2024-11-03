#include veil:camera
#include veil:deferred_utils
#include spb-revamped:common

uniform sampler2D preSampler;
uniform sampler2D DiffuseSampler0;
uniform sampler2D DiffuseDepthSampler;
uniform sampler2D MidSampler;
uniform sampler2D VhsNoise;

uniform vec2 Velocity;
uniform float GameTime;

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

float random (vec2 st) {
    float p = fract(sin(dot(st.xy,vec2(0,300.233))));
    return p;
}

float hash12(vec2 p){
    vec3 p3  = fract(vec3(p.xyx) * .1031);
    p3 += dot(p3, p3.yzx + 33.33);
    return fract((p3.x + p3.y) * p3.z);
}


void main() {
    vec2 uv = BarrelDistortionCoordinates(texCoord);
    vec4 Distortion = texture(DiffuseSampler0, uv);
    vec4 viginette = Viginette(uv);

    float depth = texture(DiffuseDepthSampler, uv).r;
    vec3 positionVS = viewPosFromDepthSample(depth, uv);
    float worldDepth = length(positionVS);

    vec4 blur2 = vec4(0);
    const float kernalSize2 = 11;
    const float halfSize2 = 5;
    const float coeff2 = 1 / (kernalSize2 * kernalSize2);
    const vec2 dx2 = vec2(0.0002, 0.0);
    const vec2 dy2 = vec2(0.0, 0.0002);
    for(float x = -halfSize2; x<= halfSize2; x++){
        for(float y = -halfSize2; y<= halfSize2; y++){
            blur2 += (coeff2) * texture(DiffuseSampler0, uv + x * dx2 + y * dy2);
        }
    }

    //Motion Blur
    vec4 blur3 = vec4(0);
    const float kernalSize3 = 11;
    const float coeff3 = 1 / (kernalSize3 * kernalSize3);
    for(float x = -1; x <= 1; x += coeff3){
        blur3 += coeff3 * texture(DiffuseSampler0, uv - vec2(Velocity.x * x, Velocity.y * x) * 0.04) * 0.5;
    }

    //TV OVERLAY
    if(uv.x < -0.05 || uv.x > 1.05 || uv.y < -0.05 || uv.y > 1.05){

        fragColor = vec4(vec3(0.1),1.0f);
        fragColor *= viginette+0.4;

    }
    else if(uv.y > 2*uv.x-uv.y && uv.x > 1.0 || uv.x > 2*uv.y-uv.x && uv.x < 0.0 ||  uv.y > -2*uv.x-uv.y+2 && uv.x < 0.0 || uv.y < -2*uv.x-uv.y+2 && uv.x > 1.0 ){
        uv = 2*uv-1;

        float disty = abs(distance(uv.y, 0)-1.5);
        fragColor = vec4(vec3(disty),1.0f)-0.35;

        uv = uv/2 + 0.5;
        fragColor *= viginette+0.3;
    }
    else if(uv.x < 0 || uv.x > 1){
        uv = 2*uv-1;

        float distx = abs(distance(uv.x, 0)-1.5);
        fragColor = vec4(vec3(distx),1.0f)-0.35;

        uv = uv/2 + 0.5;
        fragColor *= viginette+0.3;
    }
    else if(uv.y < 0.0f || uv.y > 1.0f){
        uv = 2*uv-1;

        float disty = abs(distance(uv.y, 0)-1.5);
        fragColor = vec4(vec3(disty),1.0f)-0.35;

        uv = uv/2 + 0.5;
        fragColor *= viginette+0.3;
    }else {
        fragColor = (blur2 + blur3) / 2;

        //VHS POSST EFFECTS
        fragColor.rgb = rgb2yuv(fragColor.rgb);
        fragColor.rgb += (fragColor.rgb * vec3((hash12(uv * 260.23535 + GameTime * 70) + hash12(uv * 737.36346 + GameTime * 100)) * 2.0 - 1.0)) * 0.2;
        fragColor.r += step(0.99994, (hash12(uv * 260.23535 + GameTime * 70))) * 10;
        vec2 vhsNoise = texture(VhsNoise, vec2(uv.x - GameTime * 3000, uv.y + GameTime * 5000)).gb * 0.1;
        fragColor.gb += vec2(vhsNoise.x * 0.9, vhsNoise.y * 0.9) * 0.1;
        fragColor.rgb = yuv2rgb(fragColor.rgb);
    }
}

