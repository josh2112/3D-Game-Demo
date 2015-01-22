package com.josh2112.FPSDemo.shaders;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.josh2112.FPSDemo.OpenGLResource;

public class ShaderCache implements OpenGLResource {
	
	private Map<String, ShaderProgram> shaderProgramsByName = new HashMap<String,ShaderProgram>();
	
	public ShaderProgram getShader( String name ) {
		if( !shaderProgramsByName.containsKey( name ) ) {
			shaderProgramsByName.put( name, loadShaderProgram( name ) ); 
		}
		return shaderProgramsByName.get( name );
	}
	
	private ShaderProgram loadShaderProgram( String name ) {
		if( name.equals( "Simple" ) ) return new SimpleShader();
		else if( name.equals( "Terrain" ) ) return new TerrainShader();
		return null;
	}

	@Override
	public void deleteOpenGLResources() {
		for( ShaderProgram program : shaderProgramsByName.values() ) program.deleteOpenGLResources();
	}

	public Collection<ShaderProgram> getShaders() {
		return shaderProgramsByName.values();
	}
}
