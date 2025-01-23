#include veil:camera

layout (triangles) in;
layout (triangle_strip, max_vertices = 3) out;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform vec3 ChunkOffset;
uniform mat3 NormalMat;

in vec4 VertexColor[];
in vec2 TexCoord0[];
in vec2 TexCoord2[];
in vec4 LightmapColor[];
in vec3 normall[];
in vec3 WorldPos[];
in vec3 position[];

out vec4 vertexColor;
out vec2 texCoord0;
out vec2 texCoord2;
out vec4 lightmapColor;
out vec3 normal;
out vec3 worldPos;
out vec3 Pos;
out mat3 TBN;

void main() {

    //TBN MATRIX
    vec3 edge0 = gl_in[1].gl_Position.xyz - gl_in[0].gl_Position.xyz;
    vec3 edge1 = gl_in[2].gl_Position.xyz - gl_in[0].gl_Position.xyz;
    vec2 deltaUV0 = TexCoord0[1] - TexCoord0[0];
    vec2 deltaUV1 = TexCoord0[2] - TexCoord0[0];

    float invDet = 1.0f / (deltaUV0.x * deltaUV1.y - deltaUV1.x * deltaUV0.y);

    vec3 tangent = vec3(invDet * (deltaUV1.y * edge0 - deltaUV0.y * edge1));
    vec3 biTangent = vec3(invDet * (-deltaUV1.x * edge0 + deltaUV0.x * edge1));
//    vec3 biTangent = cross(normall[1], tangent);

    vec3 T = normalize((VeilCamera.ViewMat * vec4(tangent, 0.0)).xyz);
    vec3 B = normalize((VeilCamera.ViewMat * vec4(biTangent, 0.0)).xyz);
    vec3 N = normall[1];

//    TBN = mat3(
//        T.x, B.x, N.x,
//        T.y, B.y, N.y,
//        T.z, B.z, N.z
//    );
    TBN = mat3(T, B, N);
    TBN = transpose(TBN);


    gl_Position = ProjMat * ModelViewMat * gl_in[0].gl_Position;
    vertexColor = VertexColor[0];
    texCoord0 = TexCoord0[0];
    texCoord2 = TexCoord2[0];
    lightmapColor = LightmapColor[0];
    normal = normall[0];
    worldPos = WorldPos[0];
    Pos = position[0];
    EmitVertex();

    gl_Position = ProjMat * ModelViewMat * gl_in[1].gl_Position;
    vertexColor = VertexColor[1];
    texCoord0 = TexCoord0[1];
    texCoord2 = TexCoord2[1];
    lightmapColor = LightmapColor[1];
    normal = normall[1];
    worldPos = WorldPos[1];
    Pos = position[1];
    EmitVertex();

    gl_Position = ProjMat * ModelViewMat * gl_in[2].gl_Position;
    vertexColor = VertexColor[2];
    texCoord0 = TexCoord0[2];
    texCoord2 = TexCoord2[2];
    lightmapColor = LightmapColor[2];
    normal = normall[2];
    worldPos = WorldPos[2];
    Pos = position[2];
    EmitVertex();

    EndPrimitive();
}