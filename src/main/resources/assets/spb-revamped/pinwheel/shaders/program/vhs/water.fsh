#include veil:deferred_utils
#include veil:material
#include veil:camera
#include veil:fog


uniform sampler2D DiffuseSampler0;
uniform sampler2D WaterFrameBuffer;
uniform sampler2D HandDepth;
uniform sampler2D WaterDepth;
uniform sampler2D UnderWaterBuffer;
uniform sampler2D WaterTexture;
uniform sampler2D CausticsTexture;
uniform sampler2D ShadowSampler;
uniform usampler2D MaterialSampler;
uniform sampler2D NormalTexture;
uniform float GameTime;
uniform mat4 viewMatrix;
uniform mat4 orthographMatrix;
uniform vec3 lightAngled;

in vec2 texCoord;
in vec3 pos;
out vec4 fragColor;

float blendSoftLight(float base, float blend) {
    return (blend<0.5)?(2.0*base*blend+base*base*(1.0-2.0*blend)):(sqrt(base)*(2.0*blend-1.0)+2.0*base*(1.0-blend));
}

vec3 blendSoftLight(vec3 base, vec3 blend) {
    return vec3(blendSoftLight(base.r,blend.r),blendSoftLight(base.g,blend.g),blendSoftLight(base.b,blend.b));
}

float brightness(vec4 color){
    return (color.r + color.g + color.b) / 3;
}

vec4 colorCorrect(vec4 color, float c, float b, float g){
    vec3 grey = color.rgb * vec3(0.299,0.587,0.114);
    return pow(clamp(c*(color - 0.5) + 0.5 + b, 0, 1), vec4(g));
}
vec4 saturation(vec4 color, float s){
    float grey = dot(color.rgb, vec3(0.299,0.587,0.114));
    return vec4(mix(vec3(grey), color.rgb, s), 1);
}

void main() {
    vec3 cameraPos = VeilCamera.CameraPosition;
    float isReflective = texture(WaterFrameBuffer, texCoord).r;
    float handDepth = texture(HandDepth, texCoord).r;
    float waterDepth = texture(WaterDepth, texCoord).r;
    vec4 underWater = texture(UnderWaterBuffer, texCoord);
    uint material = texture(MaterialSampler, texCoord).r;

    vec2 color = vec2(0);
    vec2 color2 = vec2(0);

    vec4 normal = vec4(0);
    vec4 normal2 = vec4(0);

    if(isReflective > 0 && handDepth == 1 && isBlock(material)) {
        vec3 viewPos = viewPosFromDepth(waterDepth, texCoord);
        vec3 playerSpace = viewToPlayerSpace(viewPos);
        vec3 worldPos = playerSpace + cameraPos;

        color = texture(WaterTexture, worldPos.xz * 0.1 + vec2(GameTime * 50)).rg - 0.5;
        color2 = texture(WaterTexture, worldPos.xz * 0.1 - vec2(0,GameTime * 50)).rg - 0.5;

        color = color + color2;

        vec4 surface = texture(DiffuseSampler0, texCoord + color * 0.02) - 0.3;
        fragColor = clamp(mix(vec4(0.0, 0.48, 0.5, 1) + surface, (vec4(0.1, 0.48, 0.6,1) + surface) * brightness(surface), 0.1 - brightness(surface)), 0, 1);

        vec4 caustics = clamp(texture(CausticsTexture, (worldPos.xz * 0.2) + GameTime * 100 + color * 0.05) - length(playerSpace) * 0.15, 0, 1) * brightness(underWater);
        caustics += clamp(texture(CausticsTexture, (worldPos.xz * 0.1) - vec2(GameTime * 160, GameTime * 80) + color * 0.05) - length(playerSpace) * 0.15, 0, 1) * brightness(underWater);
        fragColor = vec4(blendSoftLight(fragColor.rgb, caustics.rgb), 1);
        fragColor = saturation(fragColor, 1.5);
    }



}
