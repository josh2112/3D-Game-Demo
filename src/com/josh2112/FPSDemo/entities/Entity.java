package com.josh2112.FPSDemo.entities;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import com.josh2112.FPSDemo.math.MathEx;
import com.josh2112.FPSDemo.modeling.Model;

public class Entity {

	protected Model model;
	
	protected Vector3f location = new Vector3f();
    protected Vector3f scale = new Vector3f( 1.0f, 1.0f, 1.0f );
    	
    protected Quaternion orientation = new Quaternion();
    protected Matrix4f rotationMatrix = new Matrix4f();
	
    public Entity() {
    }
    
    public Entity( Model model ) {
        this.model = model;
    }

	public Model getModel() {
		return model;
	}
    
    public Vector3f getLocation() {
		return location;
	}
	
	public void addToOrientation( Vector3f axis, float angleRad ) {
		Quaternion diff = new Quaternion();
		diff.setFromAxisAngle( new Vector4f( axis.x, axis.y, axis.z, angleRad ) );
		addToOrientation( diff );
	}
	
	public void addToOrientation( Quaternion diff ) {
		Quaternion.mul( orientation, diff, orientation );
		MathEx.setRotationMatrixFromQuaternion( rotationMatrix, orientation );	
	}
	
	public Matrix4f getRotationMatrix() {
		return rotationMatrix;
	}
	
	public Vector3f getScale() {
		return scale;
	}
	
	public void setScale( float scaleX, float scaleY, float scaleZ ) {
		scale.set( scaleX, scaleY, scaleZ );
	}
	
	public void setScale( Vector3f newScale ) {
		scale.set( newScale );
	}
	
	public void setScale( float newScale ) {
		scale.set( newScale, newScale, newScale );
	}
	
	public void update( float elapsedSecs ) { }
	
	public void collide( Terrain terrain ) {
		// Do nothing by default
	}
}
