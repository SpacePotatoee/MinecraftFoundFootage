#include veil:material
#include spb-revamped:sky

uniform sampler2D Sampler0;
uniform sampler2D CloudNoise1;
uniform sampler2D CloudNoise2;

uniform vec4 ColorModulator;
uniform float gameTime;
uniform vec2 ScreenSize;
uniform float sunsetTimer;

in vec4 vertexColor;
in vec2 texCoord0;
in vec2 texCoord2;
in vec4 lightmapColor;
in vec3 normal;

out vec4 fragColor;

void main() {
    vec2 screenUv = gl_FragCoord.xy / ScreenSize;
    vec4 color = getSky(screenUv, sunsetTimer, gameTime, CloudNoise1, CloudNoise2);
    fragColor = vec4(color.rgb, 1.0);
}


