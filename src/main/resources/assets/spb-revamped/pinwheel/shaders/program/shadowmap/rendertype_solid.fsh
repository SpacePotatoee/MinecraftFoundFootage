#include veil:camera

uniform sampler2D Sampler0;
uniform mat4 viewRix;
uniform mat4 orthoMatrix;

uniform vec4 ColorModulator;

in vec4 vertexColor;
in vec2 texCoord0;
in vec4 viewPos;

out vec4 fragColor;

void main() {
	vec4 color = texture(Sampler0, texCoord0) * vertexColor * ColorModulator;
	if (color.a < 0.5) {
		discard;
	}

	fragColor = color;
}