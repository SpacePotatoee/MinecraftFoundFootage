uniform sampler2D MidSampler;
uniform sampler2D WaterSampler;
uniform sampler2D DiffuseSampler0;
uniform sampler2D HandDepth;

in vec2 texCoord;
out vec4 fragColor;

float blendOverlay(float base, float blend) {
    return base<0.5?(2.0*base*blend):(1.0-2.0*(1.0-base)*(1.0-blend));
}

vec3 blendOverlay(vec3 base, vec3 blend) {
    return vec3(blendOverlay(base.r,blend.r),blendOverlay(base.g,blend.g),blendOverlay(base.b,blend.b));
}

float brightness(vec4 color){
    return (color.r + color.g + color.b)/3;
}

void main() {
    vec4 Main = texture(DiffuseSampler0, texCoord);
    vec4 Water = texture(WaterSampler, texCoord);
    float handDepth = texture(HandDepth, texCoord).r;


    if(handDepth == 1) {
        if(brightness(Water) < 0.00001){
            fragColor = Main;
        }
        else{
            fragColor = Water;
        }
        //fragColor = vec4(blendOverlay(Main, Water), 1);
    }
}
