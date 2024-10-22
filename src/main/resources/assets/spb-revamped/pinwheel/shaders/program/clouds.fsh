#include veil:deferred_utils
#include veil:camera
#include spb-revamped:sky

uniform sampler2D DiffuseSampler0;
uniform sampler2D OpaqueNormalSampler;
uniform sampler2D TransparentNormalSampler;
uniform sampler2D CloudNoise1;
uniform sampler2D CloudNoise2;
uniform sampler2D DepthSampler;
uniform sampler2D WindowSampler;
uniform sampler2D HandSampler;
uniform float GameTime;

out vec4 fragColor;
in vec2 texCoord;


void main() {
    vec4 mainTexture = texture2D(DiffuseSampler0, texCoord);
    float depth = texture(DepthSampler, texCoord).r;
    float handDepth = texture(HandSampler, texCoord).r;
    vec4 window = texture2D(WindowSampler, texCoord);

    vec3 viewPos = viewPosFromDepth(depth, texCoord);
    vec3 playerSpace = viewToPlayerSpace(viewPos) + VeilCamera.CameraPosition;



    if (window.a > 0 && handDepth >= 1){

        vec3 color = getSky(texCoord, GameTime, CloudNoise1, CloudNoise2);
        fragColor = vec4(color, 1.0);

//        vec3 rd = viewDirFromUv(texCoord);
//
//        vec2 uv = (rd.xz * 0.5) / rd.y + GameTime * 10;
//        vec2 uv2 = (rd.xz * 0.7) / rd.y - GameTime * 10;
//
//        vec4 noise = texture2D(CloudNoise1, uv);
//        vec4 noise2 = texture2D(CloudNoise2, uv2);
//        vec4 clouds = rd.y > 0.0 ? vec4(noise * noise2) : vec4(0);
//        float cloudFog = 1.0 + (1.0 / rd.y);
//
//        clouds.a = clouds.b;
//        clouds.a = clamp((clouds.a - 0.3) * 5, 0.0, 2.0);
//        clouds.rgb = vec3(1.0);
//        clouds.rgb *= 1.0 - clamp((clouds.a - 0.5) * 0.1, 0, 0.25);
////
//        vec4 sunset = vec4(mix(SkyColor2, vec3(0.9921, 0.3686, 0.3254) - 0.1,clamp(rd.z + rd.y * smoothstep(0.0, 1.0, 0.5*sin(GameTime * 600) + 0.5) * 3, 0, 1)), 1.0);
//        vec4 day = vec4(SkyColor - rd.y*0.4, 1.0);
//        fragColor = mix(day, sunset, smoothstep(0.0, 1.0, 0.5*sin(GameTime * 600) + 0.5));
////        fragColor = mix(day, sunset, 1);
//        fragColor.rgb = mix(fragColor.rgb, clouds.rgb, min(clouds.a, 1.0) / max(1.0, cloudFog * 0.25));
//
////        fragColor = clouds;
    }
    else{
        fragColor = mainTexture;
    }


}
