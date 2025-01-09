#include veil:blend
#include spb-revamped:common

#define OFFSET vec2(0.1965249, 0.6546237)

uniform sampler2D DiffuseSampler0;
uniform sampler2D HighlightsSampler;

uniform vec2 ScreenSize;

in vec2 texCoord;
out vec4 fragColor;

vec3 BloomLod(float scale, vec2 offset){
	vec3 color = vec3(0.0);
	vec2 uv = ((texCoord - offset) * scale);
	if(uv.x > 1.0 || uv.y > 1.0 || uv.x < 0.0 || uv.y < 0.0){
		color = vec3(0.0, 0.0, 0.0);
	} else {
		color += texture(DiffuseSampler0, uv).rgb;
	}

	return color;
}

void main() {
	vec4 color = texture(DiffuseSampler0, texCoord);
//	vec4 highlights = texture(HighlightsSampler, texCoord);

	vec4 highlights = vec4(0.0);
	float scale = 2.0;
	float offset = 0;
	for(int i = 0; i < 6; i++) {
		vec2 uv = (vec2(texCoord.x + offset * scale, texCoord.y)) / scale;
		highlights += texture(HighlightsSampler, uv) * smoothstep(0.5, 0.1, float(i / 5));
		offset = (1.0 - (1.0/ scale));
		scale *= 2.0;
	}


	fragColor = color + highlights;
}