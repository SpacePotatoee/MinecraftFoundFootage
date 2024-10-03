#include veil:blend

uniform sampler2D Sampler0;

uniform vec4 ColorModulator;

out vec4 fragColor;

in vec4 vertexColor;
in vec2 texCoord0;
in vec4 overlayColor;

void main() {
	vec4 color = texture(Sampler0, texCoord0);
	if (color.a < 0.1) {
		discard;
	}
	color *= vertexColor * ColorModulator;

	fragColor = color;
}