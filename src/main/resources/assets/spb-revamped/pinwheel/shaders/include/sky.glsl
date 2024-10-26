#include veil:deferred_utils
#include veil:camera

const vec3 SkyColor = vec3(0.42,0.85,1.1);
const vec3 SkyColor2 = vec3(1.0, 0.8, 0.2);

vec3 getSky(vec2 texCoord, float sunsetTimer, float GameTime, sampler2D CloudNoise1, sampler2D CloudNoise2){
    vec3 color = vec3(0);
    vec3 rd = viewDirFromUv(texCoord);

    vec2 uv = (rd.xz * 0.5) / rd.y + GameTime * 10;
    vec2 uv2 = (rd.xz * 0.7) / rd.y - GameTime * 10;

    vec4 noise = texture2D(CloudNoise1, uv);
    vec4 noise2 = texture2D(CloudNoise2, uv2);
    vec4 clouds = rd.y > 0.0 ? vec4(noise * noise2) : vec4(0);
    float cloudFog = 1.0 + (1.0 / rd.y);

    clouds.a = clouds.b;
    clouds.a = clamp((clouds.a - 0.3) * 5, 0.0, 2.0);
    clouds.rgb = vec3(1.0);
    clouds.rgb *= 1.0 - clamp((clouds.a - 0.5) * 0.1, 0, 0.25);

    vec3 sunset = vec3(mix(SkyColor2, vec3(0.9921, 0.3686, 0.3254) - 0.1,clamp(rd.z + rd.y * (3 * smoothstep(0.0, 1.0, sunsetTimer)), 0, 1)));
    vec3 day = vec3(SkyColor - rd.y*0.4);
    color = mix(day, sunset, smoothstep(0.0, 1.0, sunsetTimer));
    //        fragColor = mix(day, sunset, 1);
    color = mix(color.rgb, clouds.rgb, min(clouds.a, 1.0) / max(1.0, cloudFog * 0.25));

    return color;
}