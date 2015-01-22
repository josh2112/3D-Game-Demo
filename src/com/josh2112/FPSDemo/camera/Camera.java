package com.josh2112.FPSDemo.camera;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;

public abstract class Camera {
	
	protected Quaternion orientation = new Quaternion();
	protected Matrix4f rotationMatrix = new Matrix4f();

	public abstract Vector3f getRight();
	
	public void update( float elapsedSecs ) { }
	
	public abstract void putViewMatrix( Matrix4f viewMatrix );
}
