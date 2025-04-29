#include veil:deferred_utils

vec2 getAccurateUV(vec3 worldPos, vec3 normal){
    vec3 worldNormal = viewToWorldSpaceDirection(normal);

    return (fract(vec2(worldPos.x, -worldPos.z)) * clamp(worldNormal.y, 0.0, 1.0)) +       //Positive Y (UP)
           (fract(vec2(-worldPos.z, worldPos.y)) * clamp(worldNormal.x, 0.0, 1.0)) +       //Positive X (EAST)
           (fract(worldPos.xy) * clamp(worldNormal.z, 0.0, 1.0)) +                         //Positive Z (SOUTH)
           (-fract(worldPos.xz) * clamp(worldNormal.y, -1.0, 0.0)) +                       //Negative Y (DOWN)
           (-fract(worldPos.zy) * clamp(worldNormal.x, -1.0, 0.0)) +                       //Negative X (WEST)
           (-fract(vec2(-worldPos.x, worldPos.y)) * clamp(worldNormal.z, -1.0, 0.0));      //Negative Z (NORTH)
}