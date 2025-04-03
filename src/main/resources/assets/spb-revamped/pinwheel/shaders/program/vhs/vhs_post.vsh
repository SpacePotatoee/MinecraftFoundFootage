layout(location = 0) in vec3 Position;

uniform float DistortionStrength;

out vec2 texCoord;

void main() {
	float distToCenter = length(Position.xy);
	vec2 pos = normalize(Position.xy);

	
	
    gl_Position = vec4(Position.x, Position.y, 0, mix(1.0, 0.70, DistortionStrength));
    texCoord = Position.xy/2.0 + 0.5;
}














