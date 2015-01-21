package com.josh2112.FPSDemo;

import com.josh2112.FPSDemo.camera.Camera;
import com.josh2112.FPSDemo.entities.Entity;

public class SceneContext {

	private static SceneContext instance;
	
	public static SceneContext get() {
		if( instance == null ) instance = new SceneContext();
		return instance;
	}
	
	private Renderer renderer;
	private Entity player;
	
	public Renderer getRenderer() {
		return renderer;
	}
	public void setRenderer( Renderer renderer ) {
		this.renderer = renderer;
	}
	public Entity getPlayer() {
		return player;
	}
	public void setPlayer( Entity player ) {
		this.player = player;
	}
}
