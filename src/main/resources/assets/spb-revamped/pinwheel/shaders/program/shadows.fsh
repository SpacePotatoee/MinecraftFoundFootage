#include veil:deferred_utils
#include veil:camera

uniform sampler2D Mid2Sampler;
uniform sampler2D DiffuseSampler0;
uniform sampler2D NormalSampler;
uniform sampler2D HandDepth;
uniform sampler2D DepthSampler;
uniform sampler2D ShadowSampler;
uniform mat4 orthoMatrix;
uniform mat4 viewRix;

out vec4 fragColor;
in vec2 texCoord;


void main() {
    vec4 mainTexture = texture(DiffuseSampler0, texCoord);
    float depth = texture(DepthSampler, texCoord).r;
    vec3 viewPos = viewPosFromDepthSample(depth, texCoord);
    vec3 playerSpace = viewToPlayerSpace(viewPos);
    vec3 shadowViewPos = (viewRix * vec4(playerSpace, 1.0)).xyz;
    vec4 homogenousPos = orthoMatrix * vec4(shadowViewPos, 1.0);
    vec3 shadowNdcPos = homogenousPos.xyz / homogenousPos.w;
    vec3 shadowScreenSpace = shadowNdcPos * 0.5 + 0.5;

    float shadowDepth = shadowScreenSpace.z;
    float shadowSampler = texture(ShadowSampler, shadowScreenSpace.xy).r;
    float shadow = step(shadowDepth, shadowSampler);
    mainTexture.rgb *= shadow;

    fragColor = mainTexture;
}
