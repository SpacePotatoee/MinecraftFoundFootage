#include veil:material
#include veil:camera
#include spb-revamped:shadows
#include spb-revamped:sky
#include spb-revamped:puddles
#include spb-revamped:common

uniform sampler2D DiffuseSampler0;
uniform sampler2D DiffuseDepthSampler;
uniform sampler2D DepthSampler;
uniform sampler2D OpaqueSampler;
uniform sampler2D TransparentSampler;
uniform sampler2D TransparentDepthSampler;
uniform sampler2D NormalSampler;
uniform sampler2D HandDepth;
uniform sampler2D MixedSampler;

uniform sampler2D SSAOSampler;
uniform sampler2D ShadowSampler;
uniform sampler2D ditherSample;
uniform sampler2D NoiseTex;

uniform sampler2D TransparentCompatSampler;
uniform sampler2D OpaqueCompatSampler;
uniform usampler2D TransparentMatSampler;
uniform usampler2D OpaqueMatSampler;

uniform mat4 viewMatrix;
uniform mat4 orthographMatrix;
uniform mat4 PrevViewMat;
uniform mat4 PrevProjMat;
uniform int ShadowToggle;
uniform float sunsetTimer;
uniform vec3 prevCameraPos;
uniform vec2 Rand;
uniform vec2 ScreenSize;


in vec2 texCoord;
layout(location = 0) out vec4 fragColor;
layout(location = 1) out vec4 prevSampler;
layout(location = 2) out vec3 sun;

ivec2 neighbourhoodOffsets[8] = ivec2[8](
    ivec2(-1, -1),
    ivec2( 0, -1),
    ivec2( 1, -1),
    ivec2(-1,  0),
    ivec2( 1,  0),
    ivec2(-1,  1),
    ivec2( 0,  1),
    ivec2( 1,  1)
);

float GetLuminance(vec3 color) {
    return dot(color, vec3(0.299, 0.587, 0.114));
}



const float edgeThresholdMin = 0.03;
const float edgeThresholdMax = 0.04;


//TAA From https://github.com/ComplementaryDevelopment/ComplementaryReimagined/blob/e47b8cf55562bcfacee930fb26ee77978e6035d7/shaders/lib/antialiasing/taa.glsl
//and https://sugulee.wordpress.com/2021/06/21/temporal-anti-aliasingtaa-tutorial/
void main(){
    //Normal Stuff
    ivec2 texelCoord = ivec2(gl_FragCoord.xy);
	vec4 color = texture(DiffuseSampler0, texCoord);

    vec4 transparent = texture(TransparentSampler, texCoord);
    vec4 opaque = texture(OpaqueSampler, texCoord);
    float depth = texture(DepthSampler, texCoord).r;
    vec4 normal = texture(NormalSampler, texCoord);
    vec3 viewPos = viewPosFromDepth(depth, texCoord);

    vec3 screenPos = vec3(texCoord, depth);
    vec2 prevCoord = Reproject(screenPos, prevCameraPos, PrevViewMat, PrevProjMat);

    vec2 velocity = (texCoord - prevCoord) * ScreenSize;

    vec4 prevColor = texture(MixedSampler, prevCoord);


    //Taken from Complimentary because I have no idea how the blendFactor works. Couldn't find any documentation
    float blendFactor = float(prevCoord.x > 0.0 && prevCoord.x < 1.0 &&
    prevCoord.y > 0.0 && prevCoord.y < 1.0);

    float blendMinimum = 0.3;
    float blendVariable = 0.25;
    float blendConstant = 0.65;
    float lengthVelocity = length(velocity) * 50;
    blendFactor *= max(exp(-lengthVelocity) * blendVariable + blendConstant - lengthVelocity * 20, blendMinimum);


    //Block Entity Stuff
    vec4 compat = texture(TransparentCompatSampler, texCoord);
    vec4 compat2 = texture(OpaqueCompatSampler, texCoord);

    uint Mat = texture(TransparentMatSampler, texCoord).r;
    uint Mat2 = texture(OpaqueMatSampler, texCoord).r;

    if(transparent.a > 0.0 && isBlock(Mat)){
        color = opaque;
    }


    color *= blur(7.0, 0.001, SSAOSampler, texCoord) * 2.0;

    if(Mat2 != 15){
        if(ShadowToggle == 1) {
            color = getShadow(color, texCoord, viewPos, normal, ScreenSize, viewMatrix, orthographMatrix, NoiseTex, ShadowSampler, ditherSample, sunsetTimer);
        }
    } else {
        vec3 rd = viewDirFromUv(texCoord);
        vec3 lightAngled = getLightAngle(viewMatrix);
        color.rgb += (smoothstep(0.998, 1.0, dot(rd, lightAngled)) + smoothstep(0.7, 1.0, dot(rd, lightAngled)) * 0.6 ) * mix(vec3(1), vec3(0.9921, 0.3686, 0.3254) * 2.4, smoothstep(0.0, 1.0, sunsetTimer));
    }
//

    if(compat.a > 0.0 || compat2.a > 0.0){
        color = compat + compat2;
    }

    fragColor = mix(color, prevColor, blendFactor);
    fragColor = color;

    prevSampler = color;
}