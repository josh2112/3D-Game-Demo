#version 400 core

in vec3 in_position;
in vec2 in_texCoords;
in vec3 in_normal;

out vec2 texCoords;
out vec2 blendMapCoords;
out vec3 normal;
out vec3 toLight;
out vec3 toCamera;
out float visibility;

uniform mat4 projMatrix, viewMatrix, modelMatrix;
uniform vec3 lightPosition;
uniform float fogDensity, fogGradient;
uniform float terrainDimension;

void main() {
	
	vec4 positionInWorld = modelMatrix * vec4( in_position, 1.0 );
	vec4 positionInCamera = viewMatrix * positionInWorld;

	gl_Position = projMatrix * positionInCamera;
	texCoords = in_texCoords;
	
	normal = (modelMatrix * vec4( in_normal, 0 )).xyz;
	toLight = lightPosition - positionInWorld.xyz;
	toCamera = (inverse( viewMatrix ) * vec4( 0, 0, 0, 1 )).xyz - positionInWorld.xyz;
	
	float dist = length( positionInCamera.xyz );
	visibility = clamp( exp( -pow( dist * fogDensity, fogGradient ) ), 0, 1 );
	
	blendMapCoords = vec2( (in_position.x + terrainDimension / 2.0f) / terrainDimension,
							(in_position.z + terrainDimension / 2.0f) / terrainDimension );
}