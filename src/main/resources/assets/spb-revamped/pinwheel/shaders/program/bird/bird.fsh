#include veil:camera
#include veil:deferred_buffers
#include veil:deferred_utils
#include veil:material

uniform float GameTime;

in vec3 localPos;
in vec3 normal;

void main() {
    fragAlbedo = vec4(0,0,0, 1.0);
    fragNormal = vec4(worldToViewSpaceDirection(normal), 1.0);
    fragMaterial = ivec4(15, 0, 0, 1);

    fragLightMap = vec4(1);
}