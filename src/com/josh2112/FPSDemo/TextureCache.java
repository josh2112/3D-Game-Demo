package com.josh2112.FPSDemo;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glDeleteTextures;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

public class TextureCache implements OpenGLResource {

	private Map<String,Texture> textureIdsByName = new HashMap<String,Texture>();
	
	public Texture getTexture( String name ) {
		if( !textureIdsByName.containsKey( name ) ) {
			textureIdsByName.put( name, loadTexture( name ) ); 
		}
		return textureIdsByName.get( name );
	}
	
	private Texture loadTexture( String name ) {
		Texture tex = null;
		
		try {
			tex = TextureLoader.getTexture( "PNG", new FileInputStream( "assets/textures/" + name + ".png" ) );
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
