#version 460
#include veil:camera

layout(location = 0) in vec3 Position;
layout(location = 1) in vec3 Normal;


struct BirdData {
    vec3 position;
    vec3 rotation; // or whatever your second vec3 represents
};

layout (std430, binding = 0) buffer BirdBuffer {
    BirdData birds[];
} birdBuffer;
uniform float GameTime;

out vec3 localPos;
out vec3 normal;
flat out int InstanceNum;

mat3 rotX(float rad) {
    float s = sin(rad);
    float c = cos(rad);
    return mat3(
    1, 0, 0,
    0, c, -s,
    0, s, c
    );
}

mat3 rotY(float rad) {
    float s = sin(rad);
    float c = cos(rad);
    return mat3(
    c, 0, s,
    0, 1, 0,
    -s, 0, c
    );
}

mat3 rotZ(float rad) {
    float s = sin(rad);
    float c = cos(rad);
    return mat3(
    c, -s, 0,
    s, c, 0,
    0, 0, 1
    );
}
mat3 createLookRotation(vec3 direction) {
    // Normalize the direction vector
    vec3 forward = normalize(direction);

    // Create a right vector perpendicular to both forward and world up
    vec3 worldUp = vec3(0.0, 1.0, 0.0);
    // Handle special case where forward is parallel to world up
    if (abs(dot(forward, worldUp)) > 0.99) {
        worldUp = vec3(0.0, 0.0, 1.0); // Use world Z instead
    }

    vec3 right = normalize(cross(worldUp, forward));

    // Create an up vector perpendicular to forward and right
    vec3 up = normalize(cross(forward, right));

    // Construct rotation matrix
    // The columns represent the transformed basis vectors
    return mat3(
    right,    // First column (right vector)
    up,       // Second column (up vector)
    forward   // Third column (forward vector)
    );
}

void main() {
    vec3 pos = Position;

    vec3 cameraPos = VeilCamera.CameraPosition;

    vec3 rotation = birdBuffer.birds[gl_InstanceID].rotation;
    vec3 position = birdBuffer.birds[gl_InstanceID].position;

    vec3 tempNormal = Normal;


    mat3 rotationMatrix = createLookRotation(rotation);

    float scale = 0.1;
    pos = rotationMatrix * pos * scale;
    tempNormal = normalize(rotationMatrix * tempNormal);

    normal = tempNormal;

    localPos = (pos - cameraPos) + position;

    gl_Position = VeilCamera.ProjMat * VeilCamera.ViewMat * vec4(localPos , 1.0);

    InstanceNum = gl_InstanceID;
}