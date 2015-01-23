package com.josh2112.FPSDemo.modeling;

import org.lwjgl.util.vector.Vector3f;

import com.josh2112.FPSDemo.TextureBlend;
import com.josh2112.FPSDemo.shaders.ShaderProgram;

public class TerrainMaterial extends Material {

	private TextureBlend textureBlend;
	
	public TerrainMaterial( ShaderProgram shader, TextureBlend textureBlend, Vector3f color ) {
		super( shader, null, color, 1, 0 );
		this.textureBlend = textureBlend;
	}
	
	public TextureBlend getTextureBlend() {
		return textureBlend;
	}
}
