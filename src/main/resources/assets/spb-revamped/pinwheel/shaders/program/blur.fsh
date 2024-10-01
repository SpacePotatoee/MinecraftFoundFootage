uniform sampler2D Mid2Sampler;
uniform sampler2D DiffuseSampler0;

out vec4 fragColor;

in vec2 texCoord;

void main() {
    vec4 Texture = texture(DiffuseSampler0, texCoord);
    
    vec4 blur = vec4(0);
	const float kernalSize = 31;
    const float halfSize = 15;
    const float coeff = 1 / (kernalSize * kernalSize);
    const vec2 dx = vec2(0.0009, 0.0);
    const vec2 dy = vec2(0.0, 0.0009);
	
	for(float x = -halfSize; x<= halfSize; x++){
        for(float y = -halfSize; y<= halfSize; y++){
            blur += coeff * texture(Mid2Sampler, texCoord + x * dx + y * dy);
        }
    }
    fragColor = blur * Texture;
}
