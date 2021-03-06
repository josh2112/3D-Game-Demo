#version 400 core

in vec2 texCoords;
in vec3 normal;
in vec3 toLight;
in vec3 toCamera;
in float visibility;

out vec4 out_color;

uniform sampler2D texSampler;
uniform vec3 lightColor;

uniform vec3 modelColor;
uniform float shineDamper;
uniform float reflectivity;

uniform vec3 skyColor;

void main() {

	vec3 unitNormal = normalize( normal );
	vec3 unitToLight = normalize( toLight );
	
	float diffuseBrightness = max( dot( unitNormal, unitToLight ), 0.1 );
	vec3 diffuse = diffuseBrightness * lightColor;
	
	vec3 unitToCamera = normalize( toCamera );
	vec3 reflectedLightDir = reflect( -unitToLight, unitNormal );
	
	float specularBrightness = max( dot( reflectedLightDir, unitToCamera ), 0.0 );
	vec3 specular = pow( specularBrightness, shineDamper ) * reflectivity * lightColor;

	// TODO: Figure out how to use model color instead of texture if texture not set
	
	// Texture
	vec4 color = vec4( diffuse, 1.0 ) * texture( texSampler, texCoords ) + vec4( specular, 1.0 );
	
	// No texture
	//vec4 color = vec4( diffuse, 1.0 ) * vec4( modelColor, 1.0 ) + vec4( specular, 1.0 );
	
	out_color = mix( vec4( skyColor, 1 ), color, visibility );
}