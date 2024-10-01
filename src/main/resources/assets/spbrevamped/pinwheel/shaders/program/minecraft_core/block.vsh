#include veil:light
#include veil:camera
#include veil:deferred_utils

layout(location = 0) in vec3 Position;
layout(location = 1) in vec4 Color;
layout(location = 2) in vec2 UV0;
layout(location = 3) in ivec2 UV2;
layout(location = 4) in vec3 Normal;

uniform sampler2D Sampler2;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform vec3 ChunkOffset;
uniform mat3 NormalMat;
uniform mat4 orthoMatrix;
uniform mat4 viewRix;

out vec4 vertexColor;
out vec2 texCoord0;
out vec2 texCoord2;
out vec4 lightmapColor;
out vec3 normal;
out vec3 viewPos;

float bias(vec3 pos){
    float num = length(pos.xy) + 0.1;
    num *= num;
    return 1 / 1024 * num / 0.1;
}

void main() {
    vec3 pos = Position + ChunkOffset;


    viewPos = (ModelViewMat * vec4(pos, 1.0)).xyz;


    gl_Position = ProjMat * ModelViewMat * vec4(pos, 1.0);
    normal = NormalMat * Normal;
    vertexColor = Color;
    texCoord0 = UV0;
    texCoord2 = minecraft_sample_lightmap_coords(UV2);
    lightmapColor = texture(Sampler2, texCoord2);
}