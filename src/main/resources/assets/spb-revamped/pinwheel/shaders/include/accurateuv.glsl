#include veil:deferred_utils

vec2 getAccurateUV(vec3 worldPos, vec3 normal){
    vec3 worldNormal = viewToWorldSpaceDirection(normal);

    return (fract(worldPos.xz) * clamp(worldNormal.y, 0.0, 1.0)) +                         //Positive
           (fract(worldPos.zy) * clamp(worldNormal.x, 0.0, 1.0)) +                         //Positive
           (fract(vec2(-worldPos.x, worldPos.y)) * clamp(worldNormal.z, 0.0, 1.0)) +       //Positive
           (-fract(vec2(worldPos.x, -worldPos.z)) * clamp(worldNormal.y, -1.0, 0.0)) +     //Negative
           (-fract(vec2(-worldPos.z, worldPos.y)) * clamp(worldNormal.x, -1.0, 0.0)) +     //Negative
           (-fract(vec2(worldPos.x, worldPos.y)) * clamp(worldNormal.z, -1.0, 0.0));       //Negative
}