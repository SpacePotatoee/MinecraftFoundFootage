uniform sampler2D DiffuseSampler0;
in vec2 texCoord;
out vec4 fragColor;

void main(){

	//fragColor = vec4(1, 1, 1, 1);
	fragColor = vec4(0.529, 0.808, 0.922, 1);
	//fragColor = vec4(1.0, 0.63, 0.3, 1.0) * 0.7;
}