package com.josh2112.FPSDemo;

import java.io.File;
import java.util.logging.Logger;

import org.apache.commons.lang3.SystemUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.PixelFormat;

public class DisplayManager {
	
	private static final Logger log = Logger.getLogger( DisplayManager.class.getName() );
	
	private static final int WIDTH = 640;
	private static final int HEIGHT = 480;
	private static final int TARGET_FPS = 120;
	
	private static long lastFrameTimeMillis;
	private static float deltaSecs;
			
	public static boolean create( String title ) {
		
		log.info( "Creating display" );
		
		try {
			ContextAttribs attribs = null;

			if( SystemUtils.IS_OS_WINDOWS ) {
				log.info( "Configuring for Windows" );
				attribs = configureLibrariesAndOpenGL( "windows", false, false, true );
			}
			else if( SystemUtils.IS_OS_MAC_OSX ) {
				log.info( "Configuring for OS X" );
				attribs = configureLibrariesAndOpenGL( "macosx", true, true, false );
			}
			
			Display.setTitle( title );
			Display.setResizable( true );
			Display.setDisplayMode( new DisplayMode( WIDTH, HEIGHT ) );
			Display.create( new PixelFormat().withSamples( 4 ), attribs );
		}
		catch( LWJGLException e ) {
			e.printStackTrace();
			return false;
		}
		
		log.info( "Display open." );
		
		return true;
	}
	
	private static ContextAttribs configureLibrariesAndOpenGL( String osName,
			boolean forwardCompatible, boolean profileCore, boolean profileCompatibility ) {
		System.setProperty( "org.lwjgl.librarypath", new File(
				"../ThirdParty/lwjgl-2.9.2/lwjgl-2.9.2/native/" + osName ).getAbsolutePath());
		
		return new ContextAttribs( 3, 2 )
			.withForwardCompatible( forwardCompatible )
			.withProfileCore( profileCore )
			.withProfileCompatibility( profileCompatibility  );
	}

	public static void update() {
		Display.sync( TARGET_FPS );
		Display.update();
		
		long currentTimeMillis = getCurrentTimeMillis();
		deltaSecs = (currentTimeMillis - lastFrameTimeMillis ) / 1000f;
		lastFrameTimeMillis = currentTimeMillis;
	}
	
	public static float getElapsedSeconds() {
		return deltaSecs;
	}
	
	public static void close() {
		Display.destroy();
	}
	
	private static long getCurrentTimeMillis() {
		return Sys.getTime()*1000/Sys.getTimerResolution();
	}

	public static void resetTime() {
		lastFrameTimeMillis = getCurrentTimeMillis();
	}
}
