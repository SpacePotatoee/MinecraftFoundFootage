//https://blog.simonrodriguez.fr/articles/2016/07/implementing_fxaa.html
const float edgeThresholdMin = 0.03125;
const float edgeThresholdMax = 0.0625;


float rgb2luma(vec3 rgb){
    return sqrt(dot(rgb, vec3(0.299, 0.587, 0.114)));
}

vec4 fxaa(vec2 texCoord, sampler2D DiffuseSampler0){
    vec3 colorCenter = texture(DiffuseSampler0,texCoord).rgb;

    float lumaCenter = rgb2luma(colorCenter);

    float lumaDown = rgb2luma(textureOffset(DiffuseSampler0,texCoord,ivec2(0,-1)).rgb);
    float lumaUp = rgb2luma(textureOffset(DiffuseSampler0,texCoord,ivec2(0,1)).rgb);
    float lumaLeft = rgb2luma(textureOffset(DiffuseSampler0,texCoord,ivec2(-1,0)).rgb);
    float lumaRight = rgb2luma(textureOffset(DiffuseSampler0,texCoord,ivec2(1,0)).rgb);

    float lumaMin = min(lumaCenter,min(min(lumaDown,lumaUp),min(lumaLeft,lumaRight)));
    float lumaMax = max(lumaCenter,max(max(lumaDown,lumaUp),max(lumaLeft,lumaRight)));

    float lumaRange = lumaMax - lumaMin;

    if(lumaRange < max(edgeThresholdMin, lumaMax * edgeThresholdMax)){
        return vec4(colorCenter, 1.0);
    }
    else{
        vec4 blur = vec4(0.0);
        const float kernalSize = 3.0;
        const float halfSize = 1.0;
        const float coeff = 1.0 / (kernalSize * kernalSize);
        const vec2 dx = vec2(0.001, 0.0);
        const vec2 dy = vec2(0.0, 0.001);

        for (float x = -halfSize; x <= halfSize; x++) {
            for (float y = -halfSize; y <= halfSize; y++) {
                blur += coeff * texture(DiffuseSampler0, texCoord + x * dx + y * dy);
            }
        }

        return blur;
    }
}