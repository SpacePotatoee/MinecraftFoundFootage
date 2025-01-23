#include veil:camera
#include veil:deferred_utils
#include veil:fog
#include veil:light

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
uniform float warpAngle;

out vec4 vertexColor;
out vec2 texCoord0;
out vec2 texCoord2;
out vec4 lightmapColor;
out vec3 normal;


void main() {
    vec3 cameraPos = VeilCamera.CameraPosition;
    vec3 pos = Position + ChunkOffset;
    float dist = pos.z;
    pos = playerSpaceToWorldSpace(pos);




    #ifdef WARP
    dist *= 0.02 * sin(warpAngle * 200.0);
    #else
    dist *= 0;
    #endif
    pos -= vec3(0.5, 21.0, 0.0);
	pos = vec3((pos.x*cos(dist)) - (pos.y * sin(dist)),(pos.y  * cos(dist)) + (pos.x * sin(dist)),pos.z);
    pos += vec3(0.5, 21.0, 0.0);

    pos = pos - cameraPos;
    gl_Position = ProjMat * ModelViewMat * vec4(pos, 1.0);

    vertexColor = Color;
    texCoord0 = UV0;
    texCoord2 = minecraft_sample_lightmap_coords(UV2);
    lightmapColor = pow(texture(Sampler2, texCoord2), vec4(3));
    normal = NormalMat * Normal;
}