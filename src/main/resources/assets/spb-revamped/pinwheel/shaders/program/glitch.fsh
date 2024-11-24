#include veil:blend
#include spb-revamped:common

uniform sampler2D DiffuseSampler0;
uniform sampler2D NoEscape;
uniform float GameTime;

in vec2 texCoord;
out vec4 fragColor;

vec2 BarrelDistortionCoordinates(vec2 uv){
	vec2 pos = 2.0f * uv - 1.0f;

	float len = distance(pos, vec2(0.0f));
	len = pow(len/2.0f, 1.0f);

	pos = pos + pos * len * len;

	pos = 0.5f * (pos + 1.0f);

	return pos;
}

void main() {
	vec2 uv = BarrelDistortionCoordinates(vec2(texCoord.x + octave(texCoord.y + GameTime * 2000) * 0.01, texCoord.y));

	vec2 offset = uv + ((hash12(uv * 260.23535 + GameTime * 70.0)) * 0.005) + ((hash12(vec2(GameTime * 4562.0))) * 0.01);

	float red = texture(NoEscape, offset + 0.001).r;
	float green = texture(NoEscape, offset - 0.001).g;
	float blue = texture(NoEscape, offset).b;


	fragColor = vec4(red, green, blue, 1.0);
//	fragColor = vec4(uv, 0.0, 1.0);
}