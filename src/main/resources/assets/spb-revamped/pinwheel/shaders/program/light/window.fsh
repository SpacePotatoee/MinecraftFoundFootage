#include veil:material
#include veil:deferred_buffers

uniform sampler2D DiffuseSampler0;
in vec2 texCoord;

void main(){
	fragColor = vec4(1);
	fragMaterial = ivec4(15, 0, 0, 1);
}