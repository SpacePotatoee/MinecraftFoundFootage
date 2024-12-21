#include veil:blend
#include spb-revamped:common

uniform sampler2D DiffuseSampler0;
uniform sampler2D NormalSampler;
uniform sampler2D RNoiseDir;
uniform float GameTime;
uniform vec2 ScreenSize;
uniform float glitchTime;

in vec2 texCoord;
out vec4 fragColor;

//float glitchTime = sin(GameTime * 1000) * 0.5 + 0.5;

vec2 BarrelDistortionCoordinates(vec2 uv){
	vec2 pos = 2.0f * uv - 1.0f;

	float len = distance(pos, vec2(0.0f));
	len = pow(len/2.0f, 1.0f);

	pos = pos + pos * len * len;

	pos = 0.5f * (pos + 1.0f);

	return pos;
}

void main() {
	if(glitchTime > 0) {
		vec4 color = vec4(0);
		vec2 uv2 = vec2(texCoord.x + octave(texCoord.y + GameTime * 2000.0) * 0.01, texCoord.y);
		vec2 offset = uv2 + ((hash12(uv2 * 260.23535 + GameTime * 70.0)) * 0.005) + ((hash12(vec2(GameTime * 4562.0))) * 0.01);

		offset = BarrelDistortionCoordinates(offset);

		offset = mix(texCoord, offset, glitchTime);

		float pixelCount = mix(1000, 700, glitchTime);
		float ratio = 1080.0 / 1920.0;
		float scaledPixelCount = pixelCount * ratio;
		vec2 finalRatio = vec2(pixelCount, scaledPixelCount);

		vec2 uv = (offset) * finalRatio;
		uv = floor(uv) / finalRatio;

		float chromAbb = 0.005 * glitchTime;

		color.r = texture(DiffuseSampler0, uv + chromAbb).r;
		color.g = texture(DiffuseSampler0, uv - chromAbb).g;
		color.ba = texture(DiffuseSampler0, uv).ba;


		fragColor = color;
	} else {
		fragColor = texture(DiffuseSampler0, texCoord);
	}
//	fragColor = vec4(uv, 0.0, 1.0);
}