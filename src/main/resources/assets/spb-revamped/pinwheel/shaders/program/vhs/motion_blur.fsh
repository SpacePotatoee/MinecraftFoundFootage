#include veil:camera
#include veil:deferred_utils

uniform sampler2D preSampler;
uniform sampler2D DiffuseSampler0;
uniform sampler2D DiffuseDepthSampler;
uniform sampler2D MidSampler;
uniform vec2 Velocity;
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

    vec4 blur2 = vec4(0);
    const float kernalSize2 = 11;
    const float halfSize2 = 5;
    const float coeff2 = 1 / (kernalSize2 * kernalSize2);
    const vec2 dx2 = vec2(0.0002, 0.0);
    const vec2 dy2 = vec2(0.0, 0.0002);
    for(float x = -halfSize2; x<= halfSize2; x++){
        for(float y = -halfSize2; y<= halfSize2; y++){
            blur2 += (coeff2 * 0.2) * texture(DiffuseSampler0, uv + x * dx2 + y * dy2);
        }
    }

    vec4 blur3 = vec4(0);
    const float kernalSize3 = 11;
    const float coeff3 = 1 / (kernalSize3 * kernalSize3);

    for(float x = -1; x <= 1; x += coeff3){
        blur3 += coeff3 * texture(DiffuseSampler0, uv - vec2(Velocity.x * x, Velocity.y * x) * 0.04) * 0.5;
    }


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
        fragColor += blur3;
    }
    //fragColor = texture(DiffuseSampler0, texCoord);
}

