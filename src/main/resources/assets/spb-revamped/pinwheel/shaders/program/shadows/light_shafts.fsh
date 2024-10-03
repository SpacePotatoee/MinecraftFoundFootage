#include veil:deferred_utils
#include veil:camera

uniform sampler2D Mid2Sampler;
uniform sampler2D DiffuseSampler0;
uniform sampler2D NormalSampler;
uniform sampler2D HandDepth;
uniform sampler2D DepthSampler;
uniform sampler2D TranslucentDepthSampler;
uniform sampler2D ShadowSampler;
uniform mat4 viewMatrix;
uniform mat4 orthographMatrix;

out vec4 fragColor;
in vec2 texCoord;

vec2 distort(vec2 pos){
    float dist = length(pos) + 0.1;
    return pos / dist;
}

vec3 distort(in vec3 shadowPosition) {
    const float bias0 = 0.95;
    const float bias1 = 1.0 - bias0;

    float factorDistance = length(shadowPosition.xy);

    float distortFactor = factorDistance * bias0 + bias1;

    return shadowPosition * vec3(vec2(1.0 / distortFactor), 0.2);
}


void main() {
    vec4 mainTexture = texture(DiffuseSampler0, texCoord);
    float depth = texture(DepthSampler, texCoord).r;
    float translucentDepth = texture(TranslucentDepthSampler, texCoord).r;
    vec3 positionVS = viewPosFromDepthSample(translucentDepth, texCoord);
    float worldDepth = length(positionVS);
    float handDepth = texture(HandDepth, texCoord).r;
    vec4 normal = texture(NormalSampler, texCoord);

    vec3 ro = VeilCamera.CameraPosition;
    vec3 rd = viewDirFromUv(texCoord);
    float dist = 0.0;
    float brightness = 1.0;

//    for(int i = 0; i <= 200; i++){
//
//    }

    //raymarch
    for(int i = 0; i <= 500; i++){
        vec3 rp = ro + rd * dist;
        dist += 0.05;

        if(dist > worldDepth){
            break;
        }

        //vec3 viewPos = viewPosFromDepth(depth, texCoord);
        vec3 playerSpace = rp - ro;
        //vec3 adjustedPlayerSpace = playerSpace + 0.01 * viewToWorldSpaceDirection(normal.rgb) * length(viewPos);
        vec3 shadowViewPos = (viewMatrix * vec4(playerSpace, 1.0)).xyz;
        vec4 homogenousPos = orthographMatrix * vec4(shadowViewPos, 1.0);
        vec3 shadowNdcPos = homogenousPos.xyz / homogenousPos.w;
        vec3 distortedNdcSpace = distort(shadowNdcPos);
        vec3 shadowScreenSpace = distortedNdcSpace * 0.5 + 0.5;
        float shadowDepth = shadowScreenSpace.z - 0.0001;
        float shadowSampler = texture(ShadowSampler, shadowScreenSpace.xy).r;
        //float shadow = clamp(step(shadowDepth, shadowSampler), 0.2, 1.0);


        if(shadowDepth < shadowSampler){
            brightness += 0.002;
        }

        if(brightness >= 1.9){
            brightness = 1.5;
            break;
        }

    }

    if(handDepth >= 1){
        mainTexture.rgb *= brightness;
    }

    fragColor = mainTexture;
    //fragColor = vec4(vec3(shadow), 1.0);
}
