uniform sampler2D DiffuseSampler0;
uniform sampler2D HandDepth;
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

void main(){
	vec4 color = texture(DiffuseSampler0, texCoord);


    color = vec4(0, 0, 0, 1);
    float scale = 2.0;
    float offset = 0;
    for(int i = 0; i < 6; i++){
        color.rgb += BloomLod(scale, vec2(offset, 0.0));
        offset = (1.0 - (1.0/ scale));
        scale *= 2.0;
    }

    float Brightness = 1.00 * dot(color.rgb, vec3(0.2126, 0.7152, 0.0722));
    float handDepth = texture(HandDepth, texCoord).r;
    
	
	if(Brightness >= 1.0){
        fragColor = vec4(color.rgb, 1.0);
    }
    else{
        fragColor = vec4(0.0, 0.0, 0.0, 1.0);
    }
}







