package com.josh2112.FPSDemo.shaders;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import com.josh2112.FPSDemo.OpenGLResource;
import com.josh2112.FPSDemo.Resources;

public abstract class ShaderProgram implements OpenGLResource {

	private int programId, vtxShaderId, fragShaderId;
	
	private static FloatBuffer matrix4fBuf = BufferUtils.createFloatBuffer( 16 );
	
	public ShaderProgram( String shaderFilename ) {
		init( Resources.pathForResource( Resources.Type.VertexShader, shaderFilename ),
				Resources.pathForResource( Resources.Type.FragmentShader, shaderFilename ) );
	}
	
	public ShaderProgram( String vtxShaderPath, String fragShaderPath ) {
		init( vtxShaderPath, fragShaderPath );
	}
	
	private void init( String vtxShaderPath, String fragShaderPath ) {
		vtxShaderId = loadShader( vtxShaderPath, GL_VERTEX_SHADER );
		fragShaderId = loadShader( fragShaderPath, GL_FRAGMENT_SHADER );
		
		programId = glCreateProgram();
		glAttachShader( programId, vtxShaderId );
		glAttachShader( programId, fragShaderId );
		
		bindAttributes();
		
		glLinkProgram( programId );
		glValidateProgram( programId );
		
		getAllUniformLocations();
	}
	
	protected abstract void bindAttributes();
	
	protected void bindAttribute( int attribute, String variableName ) {
		glBindAttribLocation( programId, attribute, variableName );
	}
	
	protected int getUniformLocation( String uniformName ) {
		return glGetUniformLocation( programId, uniformName );
	}
	
	protected abstract void getAllUniformLocations();
	
	protected void loadUniform( int location, int value ) {
		glUniform1i( location, value );
	}
	
	protected void loadUniform( int location, float value ) {
		glUniform1f( location, value );
	}
	
	protected void loadUniform( int location, Vector3f value ) {
		glUniform3f( location, value.getX(), value.getY(), value.getZ() );
	}
	
	protected void loadUniform( int location, boolean value ) {
		glUniform1f( location, value ? 1.0f : 0.0f );
	}
	
	protected void loadUniform( int location, Matrix4f value ) {
		value.store( matrix4fBuf );
		matrix4fBuf.flip();
		glUniformMatrix4( location, false, matrix4fBuf );
	}
	
	public void start() { glUseProgram( programId ); }
	public void stop() { glUseProgram( 0 ); }
	
	private static int loadShader( String path, int type ) {
		String source = null;
		try {
			source = Files.readAllLines( Paths.get( path ) ).stream().collect( Collectors.joining( "\n" ) );
		} catch( IOException e ) {
			e.printStackTrace();
			System.exit( -1 );
		}
		
		int shaderId = glCreateShader( type );
		glShaderSource( shaderId, source );
		glCompileShader( shaderId );
		if( glGetShaderi( shaderId, GL_COMPILE_STATUS ) == GL_FALSE ) {
			System.err.println( "Error compiling shader at " + path );
			System.out.println( glGetShaderInfoLog( shaderId, 500 ));
			System.exit( -1 );
		}
		return shaderId;
	}

	@Override
	public void deleteOpenGLResources() {
		stop();
		glDetachShader( programId, vtxShaderId );
		glDetachShader( programId, fragShaderId );
		glDeleteShader( vtxShaderId );
		glDeleteShader( fragShaderId );
		glDeleteProgram( programId );
	}
}
