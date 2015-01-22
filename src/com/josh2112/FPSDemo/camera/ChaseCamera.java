package com.josh2112.FPSDemo.camera;

import java.util.logging.Logger;

import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import com.josh2112.FPSDemo.entities.Entity;
import com.josh2112.FPSDemo.math.MathEx;

/**
 * This camera operates like a trackball camera, pitching and yawing around an
 * entity at a fixed distance from that entity. Moving the mouse while grabbed
 * pitches and yaws the camera, and the mouse wheel changes the distance from
 * the subject.
 * 
 * @author jf334
 *
 */
public class ChaseCamera extends Camera {
	
	private static Logger log = Logger.getLogger( ChaseCamera.class.getName() );
	
	private static final float ZOOM_PERCENTAGE = 0.10f;
	private static final float MIN_PITCH = (float)Math.toRadians( 5 );
	private static final float MAX_PITCH = (float)Math.toRadians( 85 );
	
	private Entity subject;
	
	private float followDistance = 10.0f;
	private float pitchRad = (float)Math.toRadians( 30 ), yawRad;
	private boolean limitPitch = true;
	
	public void setSubject( Entity e ) {
		subject = e;
	}
	
	public void update( float elapsedSecs ) {
		int deltaX = Mouse.getDX();
		int deltaY = Mouse.getDY();
		int deltaWheel = (int)Math.signum( Mouse.getDWheel() );
		
		followDistance *= (1.0f - deltaWheel * ZOOM_PERCENTAGE );
		
		if( Mouse.isButtonDown( 0 ) ) {
			pitchRad += deltaY * 0.005f;
			yawRad += -deltaX * 0.005f;	
		}
		
		if( limitPitch ) pitchRad = MathEx.clamp( pitchRad, MIN_PITCH, MAX_PITCH );
		
		orientation.setFromAxisAngle( new Vector4f( MathEx.AxisX.x, MathEx.AxisX.y, MathEx.AxisX.z, -pitchRad ) );
		
		Quaternion quat = new Quaternion();
		quat.setFromAxisAngle( new Vector4f( MathEx.AxisY.x, MathEx.AxisY.y, MathEx.AxisY.z, yawRad ) );
		Quaternion.mul( quat, orientation, orientation );
		
		MathEx.setRotationMatrixFromQuaternion( rotationMatrix, orientation );
	}

	@Override
	public Vector3f getRight() {
		return new Vector3f( (float)Math.cos( -yawRad ), 0, (float)Math.sin( -yawRad ) );
	}

	@Override
	public void putViewMatrix( Matrix4f viewMatrix ) {
		// We want to translate to the subject's location, do our pitch and yaw, 
		// then translate back along the Z axis to the desired follow distance.
		// They have to be done in reverse order -- first translate back, then rotate,
		// then translate to player's position.
		viewMatrix.setIdentity();
		viewMatrix.translate( new Vector3f( 0, 0, -followDistance ) );
		Matrix4f.mul( viewMatrix, rotationMatrix, viewMatrix );
		if( subject != null ) viewMatrix.translate( subject.getLocation().negate( null ) );
	}
}
