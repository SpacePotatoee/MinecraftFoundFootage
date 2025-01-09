#include spb-revamped:common

uniform sampler2D DiffuseSampler0;
uniform sampler2D HighlightsSampler;

in vec2 texCoord;
out vec4 fragColor;

void main(){
    fragColor = texelBlur(4, DiffuseSampler0) * 25;
}







