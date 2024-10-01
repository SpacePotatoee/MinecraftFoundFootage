#include veil:blend

uniform sampler2D Sampler0;

uniform vec4 ColorModulator;

out vec4 fragColor;

in vec4 vertexColor;
in vec2 texCoord0;
in vec4 overlayColor;

void main() {
	fragColor = vec4(1,1,1,1);
}