package com.josh2112.FPSDemo;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;


public class MainGameLoop {
	
	private static final String appName = "LWJGL Demo";

	public static void main( String[] args ) throws IOException {
		LogManager.getLogManager().readConfiguration( new FileInputStream( "assets/logging.properties" ) );
		
		Logger log = Logger.getLogger( MainGameLoop.class.getName() );
		
		DisplayManager.create( appName );
		
		// TODO: load scene from file
		Scene scene = Scene.createTruckScene();
		
		Renderer renderer = new Renderer();
		renderer.setScene( scene );
		
		SceneContext.get().setScene( scene );
		
		DisplayManager.resetTime();
		
		log.info( "Starting main loop" );
		
		while( !Display.isCloseRequested() ) {
			if( Display.wasResized() ) renderer.resetProjection();
			
			scene.update( DisplayManager.getElapsedSeconds() );
			
			renderer.render();
			
			DisplayManager.update();
		}
		
		log.info( "Main loop ended, cleaning up resources and closing display" );
		
		scene.deleteOpenGLResources();
		DisplayManager.close();
		
		log.info( "Done." );
	}
}
