#include veil:deferred_utils
#include veil:camera

uniform sampler2D DiffuseSampler0;

uniform sampler2D MainSampler;
uniform float GameTime;

out vec4 fragColor;
in vec2 texCoord;

const float kernalSize = 11;
const float halfSize = 5;
const float coeff = 1 / (kernalSize * kernalSize);
const vec2 dx = vec2(0.0001, 0.0);
const vec2 dy = vec2(0.0, 0.0001);

void main() {
    vec4 mainTexture = texture(MainSampler, texCoord);

//    for (float x = - halfSize; x <= halfSize; x++) {
//        for (float y = - halfSize; y <= halfSize; y++) {
//            fragColor += (coeff * 1) * texture(DiffuseSampler0, texCoord + x * dx + y * dy);
//        }
//    }
//    fragColor *= mainTexture;
    fragColor = texture(DiffuseSampler0, texCoord);
}