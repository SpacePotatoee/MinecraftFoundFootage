#include veil:blend
#include spb-revamped:common

uniform sampler2D DiffuseSampler0;
uniform sampler2D NormalSampler;
uniform sampler2D RNoiseDir;
uniform float GameTime;

in vec2 texCoord;
out vec4 fragColor;

void main() {
	vec3 randTexture = texture(RNoiseDir, texCoord * 100.0).rgb * 2 - 1;

	vec3 normal = normalize(texture(NormalSampler, texCoord).rgb);
	vec3 randDir = normalize(randTexture);
	vec3 tangent = normalize(cross(normal, normalize(randDir)));
	vec3 bitangent = normalize(cross(normal, tangent));

	mat3 TBN = mat3(tangent, bitangent, normal);
	TBN = transpose(TBN);


	fragColor = vec4(randTexture * TBN, 1.0);
}