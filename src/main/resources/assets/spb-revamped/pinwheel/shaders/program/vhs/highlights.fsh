uniform sampler2D DiffuseSampler0;
uniform sampler2D HandDepth;
in vec2 texCoord;
out vec4 fragColor;


void main(){
	vec4 Color = texture(DiffuseSampler0, texCoord);
    float Brightness = 1.09 * dot(Color.rgb, vec3(0.2126, 0.7152, 0.0722));
    float handDepth = texture(HandDepth, texCoord).r;
    
	
	if(handDepth >= 1.0 && Brightness > 1.0){
        fragColor = vec4(Color.rgb, 1.0);
    }
    else{
        fragColor = vec4(0.0, 0.0, 0.0, 1.0);
    }
}







