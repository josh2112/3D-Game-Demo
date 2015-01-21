package com.josh2112.FPSDemo.camera;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

public interface Camera {

	public Vector3f getLocation();
	public Matrix4f getRotationMatrix();
	
	public Vector3f getForward();
	public Vector3f getRight();
	public Vector3f getUp();
	
	public void update( float elapsedSecs );
}
