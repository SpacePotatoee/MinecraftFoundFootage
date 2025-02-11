layout(location = 0) in vec3 Position;
layout(location = 0) out vec2 texCoord;



void main() {
	float distToCenter = length(Position.xy);
	vec2 pos = normalize(Position.xy);

	
	
    gl_Position = vec4(Position.x, Position.y, 0, 0.70);
    texCoord = Position.xy/2.0 + 0.5;
}














