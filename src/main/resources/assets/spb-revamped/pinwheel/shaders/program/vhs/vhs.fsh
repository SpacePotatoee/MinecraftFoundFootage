#include veil:camera
#include veil:deferred_utils

#define OFFSET vec2(0.1965249, 0.6546237)

uniform sampler2D DiffuseSampler0;
uniform sampler2D DiffuseDepthSampler;
uniform sampler2D NoiseTexture;
uniform sampler2D MidSampler;
uniform sampler2D HandSampler;
uniform sampler2D WaterSampler;
uniform sampler2D MixedSampler;
uniform sampler2D HandDepth;
uniform vec2 Velocity;
uniform float Test;
uniform float GameTime;
uniform int FogToggle;

#define FOG_COLOR vec4(0.8, 0.8, 0.8, 1.0)


in vec2 texCoord;
out vec4 fragColor;

vec4 colorCorrect(vec4 color, float c, float b, float g, float s){
	vec3 grey = color.rgb * vec3(0.299,0.587,0.114);
	vec3 mixed = mix(grey, color.rgb, s);
	return pow(clamp(c*(color - 0.5) + 0.5 + b, 0, 1), vec4(g));
}


float random (vec2 st) {
	float p = fract(sin(dot(st.xy,vec2(50,3780.233)))*4463.5453123);
    return p;
}

float hash12n(vec2 p)
{
	p  = fract(p * vec2(5.3987, 5.4421));
    p += dot(p.yx, p.xy + vec2(21.5351, 14.3137));
	return fract(p.x * p.y * 95.4307);
}

float hash12n2(vec2 p)
{
	p  = fract(p * vec2(0.3987, 50.4421));
    p += dot(p.yx, p.xy + GameTime + vec2(100.5351, 100.3137));
	return fract(p.x + (GameTime * 800) * p.y + (GameTime * 800) * 95.4307);
}

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

float blendOverlay(float base, float blend) {
	return base<0.5?(2.0*base*blend):(1.0-2.0*(1.0-base)*(1.0-blend));
}

vec3 blendOverlay(vec3 base, vec3 blend) {
	return vec3(blendOverlay(base.r,blend.r),blendOverlay(base.g,blend.g),blendOverlay(base.b,blend.b));
}

float brightness(vec4 color){
	return (color.r + color.g + color.b)/3;
}


void main(){
	float handDepth = texture(HandDepth, texCoord).r;
	vec2 uv169 = texCoord * 3;
	float dist2 = texCoord.x - 0.5f;
	vec4 baseColor = texture(DiffuseSampler0, texCoord);
	vec4 water = texture(WaterSampler, texCoord);
	float depth = texture(DiffuseDepthSampler, texCoord).r;
	vec3 positionVS = viewPosFromDepthSample(depth, texCoord);
	float worldDepth = length(positionVS);

	vec3 camPos = VeilCamera.CameraPosition;


	vec4 ChromAbb = vec4(1);
	ChromAbb.r = texture(DiffuseSampler0, vec2(texCoord.x + dist2*0.01f,texCoord.y)).r;
	ChromAbb.g = texture(DiffuseSampler0, vec2(texCoord.x - dist2*0.012f,texCoord.y)).g;
	ChromAbb.b = texture(DiffuseSampler0, vec2(texCoord.x - dist2*0.01f,texCoord.y)).b;

	vec4 blur = vec4(0);
	const float kernalSize = 21;
	const float halfSize = 11;
	const float coeff = 1 / (kernalSize * kernalSize);
	const vec2 dx = vec2(0.001, 0.0);
	const vec2 dy = vec2(0.0, 0.001);

	for(float x = -halfSize; x<= halfSize; x++){
		for(float y = -halfSize; y<= halfSize; y++){
			blur += (coeff * 0.15) * texture(MixedSampler, texCoord + x * dx + y * dy);
		}
	}

	//Initialize
	vec3 ro = camPos;
	vec3 rd = viewDirFromUv(texCoord);
	float travDist = 0.0;
	float hitDist = 0.0;
	vec4 col = vec4(0);
	bool inside = false;
	float fog = 0.0;
	vec3 p;

    if (FogToggle == 1){
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
	}
	
	fragColor = baseColor;

	if (brightness(water) != 0){
		//fragColor = texture(WaterSampler, texCoord);
		fragColor = vec4(0.0, 0.0, 0.0, 1.0);
		const float kernalSize2 = 3;
		const float halfSize2 = 1;
		const float coeff2 = 1 / (kernalSize2 * kernalSize2);
		const vec2 dx2 = vec2(0.0002, 0.0);
		const vec2 dy2 = vec2(0.0, 0.0002);
		for(float x = -halfSize2; x<= halfSize2; x++){
				for(float y = -halfSize2; y<= halfSize2; y++){
					fragColor += (coeff2 * 0.8) * texture(WaterSampler, texCoord + x * dx2 + y * dy2);
			}
		}
	}

	if (FogToggle == 1){
		fragColor = mix(fragColor, FOG_COLOR, fog);
	}

	//fragColor -= vec4(vec3(random(uv169 + GameTime)), 1) * 0.06;
	fragColor += vec4(vec3(hash12n2(floor(texCoord*1000))), 1) * 0.06;
	fragColor += blur;
	if (handDepth != 1){
		fragColor = texture(HandSampler, texCoord);
	}
//	else if()
	//fragColor = texture(MidSampler, texCoord);
}





