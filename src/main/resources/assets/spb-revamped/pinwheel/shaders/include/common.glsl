//Conversions from https://www.shadertoy.com/view/3lycWz

vec3 rgb2yuv(vec3 rgb){
    float y = 0.299*rgb.r + 0.587*rgb.g + 0.114*rgb.b;
    return vec3(y, 0.493*(rgb.b-y), 0.877*(rgb.r-y));
}

vec3 yuv2rgb(vec3 yuv){
    float y = yuv.x;
    float u = yuv.y;
    float v = yuv.z;

    return vec3(
    y + 1.0/0.877*v,
    y - 0.39393*u - 0.58081*v,
    y + 1.0/0.493*u
    );
}


vec4 blur(float kernalSize, float offset, sampler2D textureSampler, vec2 texCoord){
    vec4 blur = vec4(0);
    float halfSize = (kernalSize - 1) / 2;
    float coeff = 1 / (kernalSize * kernalSize);
    vec2 dx = vec2(offset, 0.0);
    vec2 dy = vec2(0.0, offset);

    for (float x = -halfSize; x <= halfSize; x++) {
        for (float y = -halfSize; y <= halfSize; y++) {
            blur += (coeff * 0.5) * texture(textureSampler, texCoord + x * dx + y * dy);
        }
    }
    return blur;
}