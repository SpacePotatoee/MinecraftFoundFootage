#include veil:material
#include veil:deferred_buffers
#include veil:blend
#include spb-revamped:common

uniform sampler2D Sampler0;

uniform vec4 ColorModulator;
uniform float GameTime;
uniform float StaticTimer;

in vec4 vertexColor;
in vec2 texCoord0;
in vec2 texCoord2;
in vec4 overlayColor;
in vec4 lightmapColor;
in vec3 normal;
in vec3 Pos;

void main() {
    vec4 color = texture(Sampler0, texCoord0);
    vec3 staticColor = vec3(0);

    if (color.a < 0.1) {
        discard;
    }

    color *= vertexColor * ColorModulator;
    color.rgb = mix(overlayColor.rgb, color.rgb, overlayColor.a);

    //If static timer is active
    if(StaticTimer > 0.0 && StaticTimer <= 1.0){
        staticColor = vec3(hash12(texCoord0 * 260.235 + GameTime * 70), hash12(texCoord0 * 937.275 + GameTime * 70), hash12(texCoord0 * 33.352 + GameTime * 70));
        color.rgb = mix(staticColor, color.rgb, StaticTimer);
    }

    fragAlbedo = color;
    fragNormal = vec4(normal, 1.0);
    fragMaterial = ivec4(ENTITY_TRANSLUCENT, TRANSLUCENT_TRANSPARENCY, 0, 1);
    fragLightSampler = vec4(texCoord2, 0.0, 1.0);
    fragLightMap = lightmapColor;
}



