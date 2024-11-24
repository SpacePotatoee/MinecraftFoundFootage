#include veil:material
#include spb-revamped:shadows
#include spb-revamped:sky
#include spb-revamped:puddles
#include spb-revamped:common

uniform sampler2D DiffuseSampler0;
uniform sampler2D DepthSampler;
uniform sampler2D OpaqueSampler;
uniform sampler2D TransparentSampler;
uniform sampler2D TransparentDepthSampler;
uniform sampler2D NormalSampler;
uniform sampler2D HandDepth;

uniform sampler2D SSAOSampler;
uniform sampler2D ShadowSampler;
uniform sampler2D NoiseTex;

uniform sampler2D TransparentCompatSampler;
uniform sampler2D OpaqueCompatSampler;
uniform usampler2D TransparentMatSampler;
uniform usampler2D OpaqueMatSampler;
uniform sampler2D CloudNoise1;
uniform sampler2D CloudNoise2;

uniform mat4 viewMatrix;
uniform mat4 orthographMatrix;
uniform vec3 lightAngle;
uniform int ShadowToggle;
uniform float GameTime;
uniform float sunsetTimer;


in vec2 texCoord;
out vec4 fragColor;


void main(){
    //Normal Stuff
	vec4 color = texture(DiffuseSampler0, texCoord);
    vec4 transparent = texture(TransparentSampler, texCoord);
    vec4 opaque = texture(OpaqueSampler, texCoord);
    float depth = texture(DepthSampler, texCoord).r;
    vec4 normal = texture(NormalSampler, texCoord);
    vec3 viewPos = viewPosFromDepth(depth, texCoord);

    //Block Entity Stuff
    vec4 compat = texture(TransparentCompatSampler, texCoord);
    vec4 compat2 = texture(OpaqueCompatSampler, texCoord);

    uint Mat = texture(TransparentMatSampler, texCoord).r;
    uint Mat2 = texture(OpaqueMatSampler, texCoord).r;

    if(transparent.a > 0.0 && isBlock(Mat)){
        color = opaque;
    }


    color *= blur(7.0, 0.001, SSAOSampler, texCoord) * 2.0;

    if(ShadowToggle == 1){
        color = getShadow(color, texCoord, viewPos, normal, viewMatrix, orthographMatrix, NoiseTex, ShadowSampler, sunsetTimer);
    }

    if(compat.a > 0.0 || compat2.a > 0.0){
        if(Mat == 15 || Mat2 == 15){
            color = getSky(texCoord, sunsetTimer, GameTime, CloudNoise1, CloudNoise2);
        }
        else{
            color = compat + compat2;
        }
    }

    fragColor = color;
}