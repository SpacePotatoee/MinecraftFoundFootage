#version 460
#include veil:camera

layout(location = 0) in vec3 Position;
layout(location = 1) in vec3 Normal;

layout (std430, binding = 0) buffer MyBuffer { vec3 position[]; } myBuffer;

uniform float GameTime;
uniform sampler2D WindNoise;
uniform float density;
uniform float grassHeight;


//tbh i change this caus it looks a bit better now ik it ain't gonna be accepted but ayyyyyyyyyy it's for the funsies
uint triple32(uint x){ x^=x>>17; x*=0xed5ad4bbU; x^=x>>11; x*=0xac4c1b51U; x^=x>>15; x*=0x31848babU; x^=x>>14; return x; }
float hash12(vec2 p){ vec3 p3=fract(vec3(p.xyx)*0.1031); p3+=dot(p3,p3.yzx+33.33); return fract((p3.x+p3.y)*p3.z); }

mat2 rot2D_rad(float r){ float s=sin(r), c=cos(r); return mat2(c,-s,s,c); }

float getGrassHeightGradient(float h){ return h / max(grassHeight, 0.0001); }

out vec3 localPos;
out vec3 normal;

float mod289(float x){return x - floor(x * (1.0/289.0)) * 289.0;}
vec4  mod289(vec4 x){return x - floor(x * (1.0/289.0)) * 289.0;}
vec4  perm(vec4 x){return mod289(((x * 34.0) + 1.0) * x);}
float perlinNoise(vec3 p){
    vec3 a=floor(p); vec3 d=p-a; d=d*d*(3.0-2.0*d);
    vec4 b=a.xxyy+vec4(0.0,1.0,0.0,1.0);
    vec4 k1=perm(b.xyxy); vec4 k2=perm(k1.xyxy+b.zzww);
    vec4 c=k2+a.zzzz; vec4 k3=perm(c); vec4 k4=perm(c+1.0);
    vec4 o1=fract(k3*(1.0/41.0)); vec4 o2=fract(k4*(1.0/41.0));
    vec4 o3=o2*d.z + o1*(1.0-d.z); vec2 o4=o3.yw*d.x + o3.xz*(1.0-d.x);
    return o4.y*d.y + o4.x*(1.0-d.y);
}

void main(){
    vec3 pos = Position;
    vec3 nrm = Normal;

    vec3 cam = VeilCamera.CameraPosition;
    cam.x = mod(cam.x, 1); cam.z = mod(cam.z, 1);

    vec3 offset = myBuffer.position[gl_InstanceID];

    vec3 cell   = offset + floor(VeilCamera.CameraPosition);
    float r1    = hash12(cell.xz);
    float r2    = hash12(cell.xz + vec2(37.2, 5.1));

    const float PI = 3.141592653589793;
    float yaw = r1 * (2.0 * PI);
    mat2 R = rot2D_rad(yaw);
    pos.xz = R * pos.xz;
    nrm.xz = R * nrm.xz;

    float t = clamp(pos.y / max(grassHeight, 0.0001), 0.0, 1.0);

    float maxTwist = mix(0.25, 0.6, r2);
    float twist = maxTwist * t;
    mat2 Trot = rot2D_rad(twist);
    pos.xz = Trot * pos.xz;
    nrm.xz = Trot * nrm.xz;

    float dir = r2 * (2.0 * PI);
    vec2  leanDir = vec2(cos(dir), sin(dir));
    float leanAmp = mix(0.03, 0.08, r2) * grassHeight;
    float k = t * t;
    pos.xz += leanDir * (leanAmp * k);

    vec3 leanNudge = normalize(vec3(leanDir * 0.5, 1.0));
    nrm = normalize(mix(nrm, leanNudge, 0.15 * k));

    vec3 worldPos = (pos - cam) + offset + VeilCamera.CameraPosition;
    float grassGradient = getGrassHeightGradient(pos.y);

    float windStrength = 1.0;
    #ifdef LEVEL324
    windStrength = 1.5;
    #endif

    float windtexture = texture(WindNoise, (worldPos.xz * 0.03) + GameTime * 100 + r1 * 0.1).r - 0.3;
    float heightTexture = clamp(
    (texture(WindNoise, (cell.xz * 0.1)).r * texture(WindNoise, (cell.xz * 0.01)).r),
    0.0, 1.0
    );
    heightTexture = 2.5 * (heightTexture - 0.5) + 0.5;

    vec3 lp = (pos - cam) + offset;
    lp.y  += heightTexture * grassGradient;
    lp.xz -= 2.0 * (grassGradient * grassGradient) * (windtexture * windStrength) * (grassHeight + heightTexture);

    nrm.y = 2.0 * (grassGradient * grassGradient) * (windtexture * windStrength) * (grassHeight + heightTexture);
    nrm = normalize(nrm);

    localPos = lp;
    normal   = nrm;

    float _noop = perlinNoise(vec3(worldPos.x, 0.0, worldPos.z) * 0.05);
    if (_noop >  2e9) localPos += vec3(0.0);

    gl_Position = VeilCamera.ProjMat * VeilCamera.ViewMat * vec4(localPos, 1.0);
}
