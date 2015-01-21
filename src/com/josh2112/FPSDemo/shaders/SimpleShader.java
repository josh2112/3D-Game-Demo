package com.josh2112.FPSDemo.shaders;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import com.josh2112.FPSDemo.entities.Light;
import com.josh2112.FPSDemo.modeling.ModelLoader;

public class SimpleShader extends ShaderProgram implements HasModelViewProjectionMatrices, HasLightSource,
		HasSpecular, HasFog {

	private int projMatrixId, viewMatrixId, modelMatrixId;
	private int lightPosId, lightColorId;
	private int colorId, shineDamperId, reflectivityId;
	private int fogDensityId, fogGradientId, skyColorId;
	
	public SimpleShader() {
		super( "SimpleShader" );
	}
	
	@Override
	protected void bindAttributes() {
		bindAttribute( ModelLoader.ATTR_POSITIONS, "in_position" );
		bindAttribute( ModelLoader.ATTR_TEXCOORDS, "in_texCoords" );
		bindAttribute( ModelLoader.ATTR_NORMALS, "in_normal" );
	}

	@Override
	protected void getAllUniformLocations() {
		projMatrixId = getUniformLocation( "projMatrix" );
		viewMatrixId = getUniformLocation( "viewMatrix" );
		modelMatrixId = getUniformLocation( "modelMatrix" );
		
		lightPosId = getUniformLocation( "lightPosition" );
		lightColorId = getUniformLocation( "lightColor" );
		
		colorId = getUniformLocation( "color" );
		shineDamperId = getUniformLocation( "shineDamper" );
		reflectivityId = getUniformLocation( "reflectivity" );
		
		fogDensityId = getUniformLocation( "fogDensity" );
		fogGradientId = getUniformLocation( "fogGradient" );
		skyColorId = getUniformLocation( "skyColor" );
	}
	
	@Override
	public void loadProjMatrix( Matrix4f matrix ) {
		loadUniform( projMatrixId, matrix );
	}
	
	@Override
	public void loadViewMatrix( Matrix4f matrix ) {
		loadUniform( viewMatrixId, matrix );
	}
	
	@Override
	public void loadModelMatrix( Matrix4f matrix ) {
		loadUniform( modelMatrixId, matrix );
	}
	
	@Override
	public void loadLight( Light light ) {
		loadUniform( lightPosId, light.getLocation() );
		loadUniform( lightColorId, light.getColor() );
	}

	@Override
	public void loadSpecularParams( float shineDamper, float reflectivity ) {
		loadUniform( shineDamperId, shineDamper );
		loadUniform( reflectivityId, reflectivity );
	}

	@Override
	public void loadColor( Vector3f color ) {
		loadUniform( colorId, color );
	}

	@Override
	public void loadFogParams( float fogDensity, float fogGradient ) {
		loadUniform( fogDensityId, fogDensity );
		loadUniform( fogGradientId, fogGradient );
	}

	@Override
	public void loadSkyColor( Vector3f skyColor ) {
		loadUniform( skyColorId, skyColor );
	}
}
