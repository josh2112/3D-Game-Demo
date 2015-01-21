package com.josh2112.FPSDemo.camera;

import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

import com.josh2112.FPSDemo.SceneContext;

public class ChaseCamera extends ThirdPersonCamera {
	
	private static final float ZOOM_PERCENTAGE = 0.10f;
	
	float maxFollowDistance = 10.0f;
	float heightAboveTarget = 1.0f;
	float minHeight = 1.0f;
	
	public void update( float elapsedSecs ) {
		
		Vector3f lookAt = SceneContext.get().getPlayer().getLocation();
		
		int wheelDelta = Mouse.getDWheel();
		if( wheelDelta < 0 ) {
			maxFollowDistance *= (1.0f + ZOOM_PERCENTAGE);
			heightAboveTarget *= (1.0f + ZOOM_PERCENTAGE);
		}
		else if( wheelDelta > 0 ) {
			maxFollowDistance *= (1.0f - ZOOM_PERCENTAGE);
			heightAboveTarget *= (1.0f - ZOOM_PERCENTAGE);
		}
		
		// Calculate the vector from the lookAt point to the camera location and
		// put us 'followDistance' units down on that vector.
		Vector3f toCamera = (Vector3f)Vector3f.sub( location, lookAt, null );
		if( wheelDelta != 0 || toCamera.length() > maxFollowDistance ) {
			Vector3f scaledToCamera = (Vector3f)toCamera.normalise( null ).scale( maxFollowDistance );
			Vector3f.add( lookAt, scaledToCamera, location );
			location.y = lookAt.y + heightAboveTarget;
			if( location.y < minHeight ) location.y = minHeight;
		}
		
		lookAt( lookAt );
	}
}
