#include veil:blend
#include spb-revamped:common

#define OFFSET vec2(0.1965249, 0.6546237)

uniform sampler2D DiffuseSampler0;
uniform sampler2D HighlightsSampler;

in vec2 texCoord;
out vec4 fragColor;

void main() {
	vec4 color = texture(DiffuseSampler0, texCoord);
	vec4 highlights = blur(5.0, 0.002, HighlightsSampler, texCoord);

	fragColor = color + highlights;
}