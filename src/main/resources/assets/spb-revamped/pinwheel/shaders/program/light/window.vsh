layout(location = 0) in vec3 Position;
//layout(location = 1) in vec4 Color;
//layout(location = 2) in vec2 UV0;
//layout(location = 3) in ivec2 UV2;

//uniform sampler2D Sampler2;

//out vec2 texCoord;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
//uniform vec3 ChunkOffset;
//uniform int FogShape;

//out vec2 texCoord0;

void main() {
gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);

//texCoord0 = UV0;
}