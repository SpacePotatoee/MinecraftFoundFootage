#include veil:deferred_utils

uniform sampler2D DiffuseSampler0;
uniform sampler2D OpaqueNormalSampler;
uniform sampler2D TransparentNormalSampler;

out vec4 fragColor;
in vec2 texCoord;

float avgColor(vec3 color){
    return (color.r + color.g + color.b)/3;
}

void main() {

    vec3 Opaque = texture(OpaqueNormalSampler, texCoord).rgb;
    vec3 Transparent = texture(TransparentNormalSampler, texCoord).rgb;

    Opaque = viewToWorldSpaceDirection(Opaque);
    Transparent = viewToWorldSpaceDirection(Transparent);

    if(avgColor(Transparent) > 0) {
        fragColor = mix(vec4(Opaque, 0.0), vec4(Transparent, 0.0), 0.7);
    }
    else{
        fragColor = vec4(Opaque, 0.0);
    }

    //fragColor = vec4(Opaque, 1.0);
}
