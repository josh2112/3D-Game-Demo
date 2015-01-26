package com.josh2112.FPSDemo;

public class Resources {

	public static enum Type {
		Model, Material, VertexShader, FragmentShader, Texture, Font, 
	}
	
	public static String pathForResource( Type type, String resourceName ) {
		switch( type ) {
			case Model:
				return pathForResource( "models", resourceName, "obj" );
			case Material:
				return pathForResource( "models", resourceName, "mtl" );
			case VertexShader:
				return pathForResource( "shaders", resourceName, "vert" );
			case FragmentShader:
				return pathForResource( "shaders", resourceName, "frag" );
			case Texture:
				return pathForResource( "textures", resourceName, "png" );
			case Font:
				return pathForResource( "fonts", resourceName, "ttf" );
			default: return null;
		}
	}
	
	private static String pathForResource( String path, String resourceName, String extension ) {
		return String.format( "resources/%s/%s.%s", path, resourceName, extension );
	}
}
