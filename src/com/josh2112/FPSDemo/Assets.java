package com.josh2112.FPSDemo;

import java.io.File;

public class Assets {

	public static String pathForTexture( String texName ) {
		return "assets/textures/" + texName + ".png";
	}
	
	public static File fileForTexture( String texName ) {
		return new File( pathForTexture( texName ) );
	}
}
