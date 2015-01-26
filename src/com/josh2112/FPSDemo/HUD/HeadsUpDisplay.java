package com.josh2112.FPSDemo.HUD;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import java.awt.Font;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.logging.Logger;

import org.lwjgl.opengl.Display;
import org.newdawn.slick.Color;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.opengl.TextureImpl;

import com.josh2112.FPSDemo.Resources;
import com.josh2112.FPSDemo.Smoother;
import com.josh2112.FPSDemo.entities.Entity;

@SuppressWarnings( "deprecation" )
public class HeadsUpDisplay extends Entity {
	
	private static Logger log = Logger.getLogger( HeadsUpDisplay.class.getName() );
	
	private TrueTypeFont font;
	
	private Smoother fpsSmoother = new Smoother( 20 );
	
	public HeadsUpDisplay() {
		
		try {
			InputStream inputStream = new FileInputStream( Resources.pathForResource( Resources.Type.Font, "DroidSansMono" ) );
			Font awtFont = Font.createFont( Font.TRUETYPE_FONT, inputStream ).deriveFont( Font.BOLD ).deriveFont( 12f );
			font = new TrueTypeFont( awtFont, true );
		} catch( Exception e ) {
			log.throwing( log.getName(), "ctor", e );
		}
	}
	
	@Override
	public void update( float elapsedSecs ) {
		if( elapsedSecs > 0 ) fpsSmoother.add( 1.0f / elapsedSecs );
	}
	
	public void render() {
		if( font != null ) {
			// If slick-util thinks the font texture is already bound then it won't
			// bind it. But it'll be wrong about what texture is bound since we're using
			// glBindTexture() directly to do our binding. Reset slick-util's bound texture
			// here so it always uses the correct one.
			TextureImpl.bindNone();
			glActiveTexture( GL_TEXTURE0 );
			
			// Put FPS in upper-right
			String fpsText = Math.round( fpsSmoother.getAverage() ) + " FPS";
			float width = font.getWidth( fpsText );
			font.drawString( Display.getWidth()-width-5, 5, fpsText, Color.yellow );
			
			// Put instructions in lower-left
			String instructions = "WASD to move ball, LMB + drag to pitch/yaw, wheel to zoom";
			float height = font.getHeight( instructions );
			font.drawString( 5, Display.getHeight()-height-5, instructions, Color.yellow );
		}
	} 
}
