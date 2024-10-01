#include veil:camera
#include veil:deferred_utils

#define OFFSET vec2(0.1965249, 0.6546237)

uniform sampler2D DiffuseSampler0;
uniform sampler2D DiffuseDepthSampler;
uniform sampler2D NoiseTexture;
uniform float GameTime;


in vec2 texCoord;
out vec4 fragColor;


float map(vec3 p){
return p.y - 21.75;
}

float noise3D(vec3 p){
float z = p.z + GameTime *1000;
vec2 z1 = (floor(z) * OFFSET + p.xz)/5;
vec2 z2 = ((floor(z) + 1.0) * OFFSET + p.xz)/5;
float n1 = texture(NoiseTexture, z1).r;
float n2 = texture(NoiseTexture, z2).r;
float ratio = fract(z);
return mix(n1, n2, ratio);
}


void main() {
vec4 baseColor = texture(DiffuseSampler0, texCoord);
float depth = texture(DiffuseDepthSampler, texCoord).r;
vec3 positionVS = viewPosFromDepthSample(depth, texCoord);
float worldDepth = length(positionVS);

vec3 camPos = VeilCamera.CameraPosition;

//Initialize
vec3 ro = camPos;
vec3 rd = viewDirFromUv(texCoord);
float travDist = 0.0;
float hitDist = 0.0;
vec4 col = vec4(0);
bool inside = false;
float fog = 0.0;
vec3 p;

//Raymarching
for(int i = 0; i < 250; i++){
if(inside == false){
if(worldDepth > 500){
break;
}

p = ro + rd * travDist;
float d = map(p);
travDist += d;


if(d < 0.001){
hitDist = travDist;
inside = true;
}

}
else{
float noise = noise3D(p);
fog += 0.001 * noise;
p = ro + rd * travDist;
travDist += 0.05;
if(travDist > worldDepth || fog >= 1 || travDist > 50 || p.y < 0){
break;
}
}
}

	fragColor = vec4(vec3(fog), 1.0);
}






