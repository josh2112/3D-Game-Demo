package com.josh2112.FPSDemo.shaders;

import org.lwjgl.util.vector.Matrix4f;

public interface HasModelViewProjectionMatrices {

	public void loadProjMatrix( Matrix4f matrix );
	public void loadViewMatrix( Matrix4f matrix );
	public void loadModelMatrix( Matrix4f matrix );
}
