package com.josh2112.FPSDemo.camera;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import com.josh2112.FPSDemo.math.MathEx;

/**
 * This camera operates much like gluLookAt(). It computes a view
 * transform given a camera location (eye point), look-at point, 
 * and an up vector.
 * @author jf334
 *
 */
public class PointableCamera extends Camera {
	
	protected Vector3f location = new Vector3f();
	
	private Vector3f forward = new Vector3f();
	private Vector3f right = new Vector3f();
	private Vector3f up = new Vector3f();
	
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
		
		MathEx.setRotationMatrixFromVectors( rotationMatrix, right, up, forward );
		MathEx.setQuaternionFromRotationMatrix( orientation, rotationMatrix );
	}

	@Override
	public Vector3f getRight() {
		return right;
	}
	
	public Vector3f getLocation() {
		return location;
	}

	@Override
	public void update( float elapsedSecs ) {
		/*
		if( Mouse.isGrabbed() ) {
			float pitch = Mouse.getDY();
			float yaw = -Mouse.getDX();
			
			Quaternion quat = new Quaternion();

			if( pitch != 0.0f ) {
				quat.setFromAxisAngle( new Vector4f( right.x, right.y, right.z, pitch * 0.01f ) );
				Quaternion.mul( orientation, quat, orientation );
			}
			
			if( yaw != 0.0f ) {
				quat.setFromAxisAngle( new Vector4f( MathEx.AxisY.x, MathEx.AxisY.y, MathEx.AxisY.z, yaw * 0.01f ) );
				Quaternion.mul( orientation, quat, orientation );
			}
			
			MathEx.setRotationMatrixFromQuaternion( rotMatrix, orientation );	
		}
		*/
	}

	@Override
	public void putViewMatrix( Matrix4f viewMatrix ) {
		// Since this camera works off of a location and a view direction,
		// we'll first translate to the location then rotate in the view direction.
		// Because matrix math is backward, we have to rotate then translate.
		viewMatrix.load( rotationMatrix );
		viewMatrix.translate( (Vector3f)getLocation().negate( null ) );
	}
}