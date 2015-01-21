package com.josh2112.FPSDemo.shaders;

import org.lwjgl.util.vector.Vector3f;

public interface HasSpecular {

	public void loadColor( Vector3f color );
	public void loadSpecularParams( float shineDamper, float reflectivity );
}
