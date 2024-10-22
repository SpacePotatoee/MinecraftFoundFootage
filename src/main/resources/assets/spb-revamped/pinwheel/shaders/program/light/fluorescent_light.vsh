#include veil:camera
#include veil:deferred_utils


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
uniform float warAngle;


out vec4 vertexColor;
out vec2 texCoord0;
out vec4 viewPos;

void main() {
    vec3 cameraPos = VeilCamera.CameraPosition;
    vec3 pos = viewToWorldSpace(Position);
    float dist = viewToPlayerSpace(Position).z;

    #ifdef WARP
        dist *= 0.02 * sin(warAngle * 200);
    #else
        dist *= 0;
    #endif
    pos -= vec3(0.5, 21, 0);
    pos = vec3((pos.x*cos(dist)) - (pos.y * sin(dist)),(pos.y  * cos(dist)) + (pos.x * sin(dist)),pos.z);
    pos += vec3(0.5, 21, 0);

    pos = vec4(vec4(pos - cameraPos, 1.0) * VeilCamera.IViewMat).xyz;
    gl_Position = ProjMat * ModelViewMat * vec4(pos, 1.0);

    vertexColor = Color;
    texCoord0 = UV0;
}