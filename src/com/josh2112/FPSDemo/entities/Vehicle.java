package com.josh2112.FPSDemo.entities;

import java.util.logging.Logger;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import com.josh2112.FPSDemo.math.MathEx;
import com.josh2112.FPSDemo.modeling.Model;

public class Vehicle extends Entity {
	
	protected Logger log = Logger.getLogger( Vehicle.class.getName() );
	
	protected float moveVelocity = 0, turnVelocity = 0;
	
	public Vehicle( Model model ) {
		super( model );
	}
	
	@Override
	public void update( float elapsedSecs ) {
		// Rotate player in around Y (vertical) axis
		addToOrientation( MathEx.AxisY, -turnVelocity * elapsedSecs );
		
		Matrix4f m = getRotationMatrix();
		Vector3f forward = new Vector3f( m.m02, -m.m12, -m.m22 );
		
		float moveDist = moveVelocity * elapsedSecs;
		super.getLocation().translate( moveDist * forward.x, 0, moveDist * forward.z );
	}
}
