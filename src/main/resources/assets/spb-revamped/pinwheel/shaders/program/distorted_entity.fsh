#version 150
#include veil:fog

uniform sampler2D Sampler0;

uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;

in float vertexDistance;
in vec4 vertexColor;
in vec4 lightMapColor;
in vec4 overlayColor;
in vec2 texCoord0;
in vec4 normal;

out vec4 fragColor;

void main() {
    vec4 color = texture(Sampler0, texCoord0);
    if (color.a < 0.1) {
        discard;
    }
    color *= vertexColor * ColorModulator;
    // Random function from https://thebookofshaders.com/10/
    color.rgb += fract(sin(vertexDistance)*10000.0) / 20;
    //color.rgb += vec3(fract(sin(vertexDistance + 3)*1000.0) / 30, fract(sin(vertexDistance + 10)*10000.0) / 20, fract(sin(vertexDistance + 4)*10000.0) / 20);
    fragColor = linear_fog(color, vertexDistance, FogStart, FogEnd, FogColor);
}
