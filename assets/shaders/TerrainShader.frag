#version 400 core

in vec2 texCoords;
in vec3 normal;
in vec3 toLight;
in vec3 toCamera;
in float visibility;

out vec4 out_color;

uniform sampler2D texSampler;
uniform vec3 lightColor;

uniform vec3 skyColor;

void main() {

	vec3 unitNormal = normalize( normal );
	vec3 unitToLight = normalize( toLight );
	
	float diffuseBrightness = max( dot( unitNormal, unitToLight ), 0.2 );
	vec3 diffuse = diffuseBrightness * lightColor;
	
	vec3 unitToCamera = normalize( toCamera );
	vec3 reflectedLightDir = reflect( -unitToLight, unitNormal );
	
	vec4 color = vec4( diffuse, 1.0 ) * texture( texSampler, texCoords );
	
	out_color = mix( vec4( skyColor, 1 ), color, visibility );
}