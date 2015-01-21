package com.josh2112.FPSDemo.shaders;

import org.lwjgl.util.vector.Vector3f;

public interface HasFog {

	public void loadSkyColor( Vector3f skyColor );
	public void loadFogParams( float fogDensity, float fogGradient );
}
