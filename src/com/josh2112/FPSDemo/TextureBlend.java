package com.josh2112.FPSDemo;

import org.newdawn.slick.opengl.Texture;

public class TextureBlend {

	private Texture baseTexture, blendTexture, redTexture, greenTexture, blueTexture;

	public TextureBlend( Texture baseTexture, Texture blendTexture, Texture redTexture, Texture greenTexture, Texture blueTexture ) {
		this.baseTexture = baseTexture;
		this.blendTexture = blendTexture;
		this.redTexture = redTexture;
		this.greenTexture = greenTexture;
		this.blueTexture = blueTexture;
	}

	public Texture getBaseTexture() {
		return baseTexture;
	}
	
	public Texture getBlendTexture() {
		return blendTexture;
	}

	public Texture getRedTexture() {
		return redTexture;
	}

	public Texture getGreenTexture() {
		return greenTexture;
	}

	public Texture getBlueTexture() {
		return blueTexture;
	}
}
