#include veil:deferred_utils
#include veil:camera
#include veil:material

uniform sampler2D DiffuseSampler0;
uniform sampler2D DiffuseDepthSampler;
uniform usampler2D OpaqueMatSampler;
uniform usampler2D TransparentMatSampler;

uniform sampler2D NormalSampler;
uniform sampler2D DepthSampler;
uniform sampler2D HandSampler;
uniform sampler2D RNoiseDir;
uniform float GameTime;

uniform vec3 samples[50];

out vec4 fragColor;
in vec2 texCoord;

vec3 projectAndDivide(mat4 projectionMat, vec3 position){
    vec4 homogenousPos = projectionMat * vec4(position, 1.0);
    return homogenousPos.xyz / homogenousPos.w;
}

vec3 viewToScreenSpace(vec3 viewPos){
    vec3 ndcPos = projectAndDivide(VeilCamera.ProjMat, viewPos);
    return ndcPos * 0.5 + 0.5;
}

const int QUALITY = 10;

void main() {
    float depthSample = texture(DiffuseDepthSampler, texCoord).r;
    vec4 mainTexture = texture2D(DiffuseSampler0, texCoord);
    float depth = texture(DepthSampler, texCoord).r;
    float handDepth = texture(HandSampler, texCoord).r;

    vec3 viewPos = viewPosFromDepth(depth, texCoord);
    vec3 normal = normalize(texture(NormalSampler, texCoord).rgb);

    uint material = texture2D(OpaqueMatSampler, texCoord).r;
    uint material2 = texture2D(TransparentMatSampler, texCoord).r;

    if(depthSample < 1.0 && material != 15 && !isEntity(material2)){
        vec3 randDir = normalize(vec3(texture(RNoiseDir, texCoord * 100.0).rgb * 2.0 - 1.0));
        vec3 tangent = normalize(cross(normal, normalize(randDir)));
        vec3 bitangent = normalize(cross(normal, tangent));

        mat3 TBN = mat3(tangent, bitangent, normal);
        TBN = transpose(TBN);

        float occlusion = 0.0;
        vec3 samplePos = vec3(0.0);
        for (int i = 0; i < QUALITY; i++) {
            samplePos = samples[i] * TBN;

            //Add the view Pos to it
            vec3 worldSamplePos = samplePos + viewPos;

            //To screen space
            vec3 screenSamplePos = viewToScreenSpace(worldSamplePos);

            float sampleDepth = texture(DepthSampler, screenSamplePos.xy).r + 0.0001;

            vec3 viewPos2 = viewPosFromDepth(sampleDepth, screenSamplePos.xy);
            //            vec3 worldSamplePos2 = viewToWorldSpace(viewPos2);

//&& (screenSamplePos.z - sampleDepth) < 0.0001
            if (screenSamplePos.z > sampleDepth) {
                float dist = smoothstep(0.0, 1.0, 1.0 / length(viewPos - viewPos2));
                occlusion += 1.5 * dist;
            }

        }
        occlusion /= QUALITY;
        fragColor = vec4(vec3(1.0 - occlusion * 1), 1.0);
//        fragColor = vec4(randDir * TBN, 1.0);
    }else {
        fragColor = vec4(1.0);
    }


}
