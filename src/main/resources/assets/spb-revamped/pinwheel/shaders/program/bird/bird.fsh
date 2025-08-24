#include veil:camera
#include veil:deferred_buffers
#include veil:deferred_utils
#include veil:material

uniform float GameTime;
uniform int NumOfInstances;

in vec3 localPos;
in vec3 normal;
flat in int InstanceNum;

void main() {
    vec3 startColor = vec3(0.0, 0.0, 0.0);
    vec3 endColor = vec3(0.1, 0.1, 0.1);

    vec3 color = mix(startColor, endColor, min(1, (InstanceNum / NumOfInstances) / 10));
    fragAlbedo = vec4(color, 1.0);
    fragNormal = vec4(worldToViewSpaceDirection(normal), 1.0);
    fragMaterial = ivec4(15, 0, 0, 1);

    fragLightMap = vec4(1.0, 1.0, 1.0, 1.0);
}