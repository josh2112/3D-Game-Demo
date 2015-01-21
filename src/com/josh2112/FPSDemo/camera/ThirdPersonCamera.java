package com.josh2112.FPSDemo.camera;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;

import com.josh2112.FPSDemo.entities.Entity;
import com.josh2112.FPSDemo.math.MathEx;

public class ThirdPersonCamera implements Camera {
	
	protected Vector3f location = new Vector3f();
	
	private Vector3f forward = new Vector3f();
	private Vector3f right = new Vector3f();
	private Vector3f up = new Vector3f();
	
	private Quaternion quat = new Quaternion();
	private Matrix4f rotMatrix = new Matrix4f();
	
	public void lookAt( Vector3f lookAtPoint ) {
		lookAt( location, lookAtPoint );
	}
	
	public void lookAt( Vector3f eyePoint, Vector3f lookAtPoint ) {
		lookAt( eyePoint, lookAtPoint, MathEx.AxisY );
	}
	
	public void lookAt( Vector3f eyePoint, Vector3f lookAtPoint, Vector3f upDir ) {
		location.set( eyePoint );
		
		Vector3f.sub( lookAtPoint, location, forward );
		forward.normalise();
		Vector3f.cross( forward, upDir, right );
		right.normalise();
		Vector3f.cross( right, forward, up );
		
		MathEx.setRotationMatrixFromVectors( rotMatrix, right, up, forward );
		MathEx.setQuaternionFromRotationMatrix( quat, rotMatrix );
	}
	
	public Matrix4f getRotationMatrix() {
		return rotMatrix;
	}

	@Override
	public Vector3f getForward() {
		return forward;
	}

	@Override
	public Vector3f getRight() {
		return right;
	}

	@Override
	public Vector3f getUp() {
		return up;
	}
	
	@Override
	public Vector3f getLocation() {
		return location;
	}

	@Override
	public void update( float elapsedSecs ) {
		
	}
}