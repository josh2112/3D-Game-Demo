package com.josh2112.FPSDemo.entities;

import org.lwjgl.util.vector.Vector3f;

public class Light extends Entity {

	private Vector3f color;

	public Vector3f getColor() {
		return color;
	}

	public void setColor( Vector3f color ) {
		this.color = color;
	}
	
	public Light( Vector3f location, Vector3f color ) {
		this.location = location;
		this.color = color;
	}
}
