#version 400 core

in vec2 texCoords;
in vec3 normal;
in vec3 toLight;
in vec3 toCamera;
in float visibility;
in vec2 blendMapCoords;

out vec4 out_color;

uniform sampler2D blendTexture, baseTexture, rTexture, gTexture, bTexture;

uniform vec3 lightColor;
uniform vec3 skyColor;

void main() {
	
	vec4 blend = texture( blendTexture, blendMapCoords );
	float baseTexAmt = 1 - blend.r - blend.g - blend.b;
	
	vec4 baseTexColor = texture( baseTexture, texCoords ) * baseTexAmt;
	vec4 rTexColor = texture( rTexture, texCoords ) * blend.r;
	vec4 gTexColor = texture( gTexture, texCoords ) * blend.g;
	vec4 bTexColor = texture( bTexture, texCoords ) * blend.b;
	
	vec4 totalTexColor = baseTexColor + rTexColor + gTexColor + bTexColor;

	vec3 unitNormal = normalize( normal );
	vec3 unitToLight = normalize( toLight );
	
	float diffuseBrightness = max( dot( unitNormal, unitToLight ), 0.2 );
	vec3 diffuse = diffuseBrightness * lightColor;
	
	vec3 unitToCamera = normalize( toCamera );
	vec3 reflectedLightDir = reflect( -unitToLight, unitNormal );
	
	vec4 color = vec4( diffuse, 1.0 ) * totalTexColor;
	
	out_color = mix( vec4( skyColor, 1 ), color, visibility );
}