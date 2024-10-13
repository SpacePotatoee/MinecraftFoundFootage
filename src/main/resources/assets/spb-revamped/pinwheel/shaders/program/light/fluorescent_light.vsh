layout(location = 0) in vec3 Position;
layout(location = 1) in vec4 Color;
layout(location = 2) in vec2 UV0;

uniform sampler2D Sampler2;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform vec3 ChunkOffset;
uniform mat3 NormalMat;
uniform mat4 orthoMatrix;
uniform mat4 viewRix;

out vec4 vertexColor;
out vec2 texCoord0;
out vec4 viewPos;

void main() {
    vec3 pos = Position + ChunkOffset;


    viewPos = orthoMatrix * viewRix * vec4(pos, 1.0);

    gl_Position = ProjMat * ModelViewMat * vec4(pos, 1.0);
    vertexColor = Color;
    texCoord0 = UV0;

    //gl_Position.xyz = distort(gl_Position.xyz);
}