package com.josh2112.FPSDemo;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.EXTTextureFilterAnisotropic;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

public class TextureCache implements OpenGLResource {

	private static float maxAnisotropy = 1.0f;
	
	private Map<String,Texture> textureIdsByName = new HashMap<String,Texture>();
	
	public TextureCache() {
		FloatBuffer fl = BufferUtils.createFloatBuffer( 16 );
		glGetFloat( EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT, fl );
		maxAnisotropy = fl.get();
	}
	
	public Texture getTexture( String name ) {
		if( !textureIdsByName.containsKey( name ) ) {
			textureIdsByName.put( name, loadTexture( name ) ); 
		}
		return textureIdsByName.get( name );
	}
	
	private Texture loadTexture( String name ) {
		Texture tex = null;
		
		try {
			tex = TextureLoader.getTexture( "PNG", new FileInputStream( Resources.pathForResource( Resources.Type.Texture, name ) ) );
			glTexParameterf( GL_TEXTURE_2D, EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT, maxAnisotropy );
			glBindTexture( GL_TEXTURE_2D, 0 );
			
		} catch( IOException e ) {
			System.err.println( "Error loading texture " + name + ":" );
			e.printStackTrace();
			System.exit( -1 );
		}
		
		return tex;
	}

	@Override
	public void deleteOpenGLResources() {
		for( Texture tex : textureIdsByName.values() ) glDeleteTextures( tex.getTextureID() );
	}
}
