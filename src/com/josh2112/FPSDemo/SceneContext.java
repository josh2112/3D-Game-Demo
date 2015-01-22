package com.josh2112.FPSDemo;


public class SceneContext {

	private static SceneContext instance;
	
	public static SceneContext get() {
		if( instance == null ) instance = new SceneContext();
		return instance;
	}
	
	private Scene scene;
	
	public Scene getScene() {
		return scene;
	}
	public void setScene( Scene scene ) {
		this.scene = scene;
	}
}
