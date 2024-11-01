layout(location = 0) in vec3 Position;
layout(location = 0) out vec2 texCoord;

uniform sampler2D NoiseTexture;

void main() {
	float distToCenter = length(Position.xy);
	vec2 pos = normalize(Position.xy);

	
	
    gl_Position = vec4(Position.x/1.08, Position.y, 0, 0.92);
    texCoord = Position.xy/2 + 0.5;
}














