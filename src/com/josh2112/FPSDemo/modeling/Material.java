package com.josh2112.FPSDemo.modeling;

import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.opengl.Texture;

import com.josh2112.FPSDemo.shaders.ShaderProgram;

public class Material {

	protected ShaderProgram shader;

	private Vector3f color;
	private Texture texture;
	
	private float shineDamper, reflectivity;
	
	public Material( ShaderProgram shader, Texture texture, Vector3f color, float shineDamper, float reflectivity ) {
		this.shader = shader;
		this.texture = texture;
		this.color = color;
		this.shineDamper = shineDamper;
		this.reflectivity = reflectivity;
	}

    public ShaderProgram getShader() {
		return shader;
	}
	
	public void setShader( ShaderProgram shader ) {
		this.shader = shader;
	}
		
	public Texture getTexture() {
		return texture;
	}
	
	public Vector3f getColor() {
		return color;
	}
	
	public void setColor( Vector3f c ) {
		color = c;
	}

	public void setTexture( Texture texture ) {
		this.texture = texture;
	}

	public float getShineDamper() {
		return shineDamper;
	}

	public void setShineDamper( float shineDamper ) {
		this.shineDamper = shineDamper;
	}

	public float getReflectivity() {
		return reflectivity;
	}

	public void setReflectivity( float reflectivity ) {
		this.reflectivity = reflectivity;
	}
}
