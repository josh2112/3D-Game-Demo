package com.josh2112.FPSDemo.entities;

import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import org.newdawn.slick.util.Log;

import com.josh2112.FPSDemo.math.MathEx;
import com.josh2112.FPSDemo.modeling.Model;
import com.sun.javafx.css.CalculatedValue;

public class Sphere extends Entity {

	private static float MASS = 1;
	private static float FRICTION = 1f;
	
	private static Vector3f F_GRAVITY = new Vector3f( 0, -9.8f * MASS, 0 );
	
	private float radius;
	
	protected boolean isFalling = false;
	protected Vector3f terrainNormal = new Vector3f( 0, 1, 0 );
	
	protected Vector3f force = new Vector3f();
	private Vector3f velocity = new Vector3f();
	
	private Quaternion rollQuat = new Quaternion();
	
	public Sphere( Model model ) {
		super( model );
		
		Vector3f bbDims = model.getModelBoundingBox().getDimensions();
		radius = Math.max( bbDims.x, Math.max( bbDims.y, bbDims.z ) ) / 2.0f;
	}
	
	@Override
	public void update( float elapsedSecs ) {
		this.force.set( 0, 0, 0 );
		addForces();
		
		Vector3f accel = (Vector3f)force.scale( 1.0f/MASS );
		
		// TODO: Tackle advanced Verlet integration which allows forces to change based on position:
		// http://gamedev.stackexchange.com/questions/15708/how-can-i-implement-gravity
		
		// location += elapsedSecs * ( velocity + elapsedSecs * accel / 2 );
		Vector3f halfVel = Vector3f.add( velocity, MathEx.scale( accel, elapsedSecs / 2.0f, null ), null );
		Vector3f.add( location, MathEx.scale( halfVel, elapsedSecs, null ), location );
		
		// velocity += elapsedSecs * accel;
		Vector3f.add( velocity, MathEx.scale( accel, elapsedSecs, null ), velocity );
		
		// If velocity is too small, put it out of its misery. Otherwise, calculate
		// how much we should rotate the sphere model.
		if( velocity.length() < 0.05f ) {
			velocity.set( 0, 0, 0 );
		} else {
			// Rolling axis is velocity crossed with up
			Vector3f rollAxis = Vector3f.cross( velocity, MathEx.AxisY, null );
			if( rollAxis.length() > 0 ) {
				rollAxis.normalise();
				rollQuat.setFromAxisAngle( new Vector4f( rollAxis.x, rollAxis.y, rollAxis.z,
						velocity.length()*elapsedSecs*radius*(float)Math.PI ) );
				
				addToOrientation( rollQuat );
			}
		}
	}
	
	protected void addForces() {
		if( !isFalling ) {
			// Add rolling friction proportional to velocity
			// force += -velocity * friction
			Vector3f.add( this.force, MathEx.scale( this.velocity.negate( null ), FRICTION, null ), this.force );
			
			// Add sliding force due to gravity on a sloped surface
			Vector3f.add( this.force, calculateSlidingForce(), this.force ); 
		}
		else {
			// Add gravity
			Vector3f.add( this.force, F_GRAVITY, this.force );
		} 
	}

	/**
	 * Calculates the sliding force (the force due to gravity pulling us down a slope).
	 * This is done by calculating the component of gravity along the anti-normal (the
	 * surface normal negated), then subtracting that from the full force of gravity to
	 * obtain the component of gravity along the slope.
	 * @return
	 */
	private Vector3f calculateSlidingForce() {
		Vector3f antiNormal = terrainNormal.negate( null );
		
		float theta = Vector3f.angle( antiNormal, F_GRAVITY );
		float fn = (float)( Math.cos( theta ) * F_GRAVITY.length() );
		antiNormal.scale( fn );
		
		Vector3f fs = Vector3f.sub( F_GRAVITY, antiNormal, null );
		return fs;
	}

	@Override
	public void collide( Terrain terrain ) {
		Vector4f terrainInfoBelow = terrain.getTerrainInfoAtXZ( getLocation().x, getLocation().z );
		terrainNormal.set( terrainInfoBelow.x, terrainInfoBelow.y, terrainInfoBelow.z ); 
		
		/* TODO: Fix this
		// Reverse the plane normal and scale it by the radius, then add it to the center of the
		// sphere. The resulting x,z will be where the sphere _should_ intersect the terrain.
		// Look up terrain info for that point and elevate the sphere by the difference between
		// the calculated height at that point (the y) and the terrain height.
		
		Vector3f terrainNormalReversedScaled = (Vector3f)
				((Vector3f)new Vector3f( terrainInfoBelow.x, terrainInfoBelow.y, terrainInfoBelow.z ).negate()).scale( radius );
		Vector3f sphereTerrainContactPoint = Vector3f.sub( getLocation(), terrainNormalReversedScaled, null );
		
		Vector4f terrainInfoTangent = terrain.getTerrainInfoAtXZ( sphereTerrainContactPoint.x, sphereTerrainContactPoint.z );
		float heightDiff = terrainInfoTangent.y - sphereTerrainContactPoint.y;
		
		getLocation().y += heightDiff;
		*/
		
		float sphereContactY = getLocation().y - radius;
		
		if( sphereContactY > terrainInfoBelow.w ) {
			isFalling = true;
		}
		else {
			isFalling = false;
			if( getLocation().y - 0.5f < terrainInfoBelow.w ) {
				// We're in the ground, put us back on top
				getLocation().y = terrainInfoBelow.w + 0.5f;
			}
		}
	}
}
