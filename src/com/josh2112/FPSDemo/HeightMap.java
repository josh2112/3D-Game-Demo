package com.josh2112.FPSDemo;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.josh2112.FPSDemo.entities.Terrain;
import com.josh2112.FPSDemo.math.MathEx;

public class HeightMap {
	
	private BufferedImage heightMap;
	private float maxHeight;
	private int sideLength;
	private float textureSideLengthBlocks;
	
	public static HeightMap create( String heightMapName, float maxHeight, int sideLength, float textureSideLengthBlocks ) {
		BufferedImage heightMap = null;
		try {
			heightMap = ImageIO.read( Assets.fileForTexture( heightMapName ) );
		}
		catch( IOException e ) {
			System.err.println( "Failed to load image " + heightMapName ); 
		}
		return new HeightMap( heightMap, maxHeight, sideLength, textureSideLengthBlocks );
	}
	
	private HeightMap( BufferedImage heightMap, float maxHeight, int sideLength, float textureSideLengthBlocks ) {
		this.heightMap = heightMap;
		this.maxHeight = maxHeight;
		this.sideLength = sideLength;
		this.textureSideLengthBlocks = textureSideLengthBlocks;
	}
	
	public float getMaxHeight() {
		return maxHeight;
	}

	public int getSideLength() {
		return sideLength;
	}

	public float getTextureSideLengthBlocks() {
		return textureSideLengthBlocks;
	}
	
	// Using blue as the height
	public float getHeight( float x, float z ) {
		int u = (int)Math.round( MathEx.clamp( (x + sideLength/2.0f)/sideLength * heightMap.getWidth(), 0, heightMap.getWidth()-1 ) );
		int v = (int)Math.round( MathEx.clamp( (z + sideLength/2.0f)/sideLength * heightMap.getHeight(), 0, heightMap.getHeight()-1 ) );
		return (float)( heightMap.getRGB( u, v ) & 0xff ) / 255.0f * maxHeight;
	}
}