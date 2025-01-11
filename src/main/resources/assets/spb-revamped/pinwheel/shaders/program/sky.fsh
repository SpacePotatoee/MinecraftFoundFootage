#include veil:material
#include veil:deferred_buffers
#include spb-revamped:sky

uniform sampler2D Sampler0;
uniform sampler2D CloudNoise1;
uniform sampler2D CloudNoise2;

uniform vec4 ColorModulator;
uniform float GameTime;
uniform vec2 ScreenSize;
uniform float sunsetTimer;

in vec4 vertexColor;
in vec2 texCoord0;
in vec2 texCoord2;
in vec4 lightmapColor;
in vec3 normal;

void main() {
    vec2 screenUv = gl_FragCoord.xy / ScreenSize;
    vec4 color = getSky(screenUv, sunsetTimer, GameTime, CloudNoise1, CloudNoise2);

    fragAlbedo = vec4(color.rgb, 1.0);
    fragNormal = vec4(normal.rgb, 1.0);
    fragMaterial = ivec4(15, 0, 0, 1);
    fragLightSampler = vec4(1.0);
    fragLightMap = vec4(10.0);
}


