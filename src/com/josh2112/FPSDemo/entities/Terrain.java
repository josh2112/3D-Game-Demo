package com.josh2112.FPSDemo.entities;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import com.josh2112.FPSDemo.Assets;
import com.josh2112.FPSDemo.math.MathEx;
import com.josh2112.FPSDemo.modeling.Material;
import com.josh2112.FPSDemo.modeling.Model;
import com.josh2112.FPSDemo.modeling.ModelLoader;

public class Terrain extends Entity {
	
	public static class HeightMap {
		BufferedImage heightMap;
		float maxHeight;
		int sideLength;
		float textureSideLengthBlocks;
		
		public static HeightMap create( String heightMapName, float maxHeight, int sideLength, float textureSideLengthBlocks ) {
			BufferedImage heightMap = null;
			try {
				heightMap = ImageIO.read( Assets.fileForTexture( heightMapName ) );
			} catch( IOException e ) {
				log.throwing( Terrain.class.getName(), "generate", e );
				return null;
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

	private static Logger log = Logger.getLogger( Terrain.class.getName() );
	
	private float terrainDimension;
	
	private float[][] heights;
	
	public static Terrain generate( ModelLoader modelLoader, Material material, HeightMap heightMap ) {
		
		int size = heightMap.getSideLength();
		
		int numVertices = (size + 1 ) * (size + 1);
		
		float[] vertices = new float[numVertices*3];
		float[] normals = new float[numVertices*3];
		float[] texCoords = new float[numVertices*2]; 
		int[] indices = new int[size*size*6];
		
		float[][] heights = new float[size+1][size+1];
		
		int i = 0;
		for( int z=-size/2; z<=size/2; ++z ) {
			for( int x=-size/2; x<=size/2; ++x ) {
				float height = heightMap.getHeight( x, z );
				heights[x+size/2][z+size/2] = height; 
				vertices[i*3+0] = x;
				vertices[i*3+1] = height;
				vertices[i*3+2] = z;
				Vector3f normal = getNormal( x, z, heightMap );
				normals[i*3+0] = normal.x;
				normals[i*3+1] = normal.y;
				normals[i*3+2] = normal.z;
				texCoords[i*2+0] = x / heightMap.getTextureSideLengthBlocks();
				texCoords[i*2+1] = z / heightMap.getTextureSideLengthBlocks();
				i++;
			}
		}
		
		i = 0;
		for( int z=0; z<size; ++z ) {
			for( int x=0; x<size; ++x ) {
				int base = z*(size+1) + x;
				indices[i++] = base;
				indices[i++] = base+size+1;
				indices[i++] = base+1;
				indices[i++] = base+1;
				indices[i++] = base+size+1;
				indices[i++] = base+size+2;
			}
		}
		
		Model model = modelLoader.loadFromArrays( "terrain", vertices, indices, texCoords, normals );
		model.getMeshes().stream().forEach( m -> m.setMaterial( material ) );
		
		return new Terrain( model, size, heights );
	}
	
	private static Vector3f getNormal( int x, int z, HeightMap heightMap ) {
		float heightL = heightMap.getHeight( x-1, z );
		float heightR = heightMap.getHeight( x+1, z );
		float heightD = heightMap.getHeight( x, z-1 );
		float heightU = heightMap.getHeight( x, z+1 );
		Vector3f normal = new Vector3f( heightL-heightR, 2f, heightD - heightU );
		normal.normalise();
		return normal;
	}

	private Terrain( Model model, float size, float[][] heights  ) {
		super( model );
		this.terrainDimension = size;
		this.heights = heights;
	}
	
	public List<Entity> populateWithPlant( Model plantModel, int num ) {
		List<Entity> plants = new ArrayList<Entity>();
		Random rnd = new Random();
		
		for( int i=0; i<num; ++i ) {
			Entity e = new Entity( plantModel );
			float x = (rnd.nextFloat() - 0.5f ) * terrainDimension;
			float z = (rnd.nextFloat() - 0.5f ) * terrainDimension;
			e.getLocation().translate( x, getTerrainInfoAtXZ( x, z ).w, z );
			e.addToOrientation( MathEx.AxisY, (float)(rnd.nextFloat() * Math.PI) );
			plants.add( e );
		}
		
		return plants;
	}
	
	private float getHeightOfPointInTriangle( Vector3f p1, Vector3f p2, Vector3f p3, float x, float z ) {
		float det = (p2.z - p3.z) * (p1.x - p3.x) + (p3.x - p2.x) * (p1.z - p3.z);

        float l1 = ((p2.z - p3.z) * (x - p3.x) + (p3.x - p2.x) * (z - p3.z)) / det;
        float l2 = ((p3.z - p1.z) * (x - p3.x) + (p1.x - p3.x) * (z - p3.z)) / det;
        float l3 = 1.0f - l1 - l2;

        return l1 * p1.y + l2 * p2.y + l3 * p3.y;
	}
	
	/**
	 * Returns information about the terrain at x,z packed into a 4-d vector.
	 * The x,y,z components are the normal of the terrain at that point and the
	 * w component is the height of the terrain.
	 * @param x x coordinate of terrain
	 * @param z z coordinate of terain
	 * @return normal and height of terrain at given x,z point
	 */
	public Vector4f getTerrainInfoAtXZ( float x, float z ) {
		float u = x + terrainDimension/2, v = z + terrainDimension/2;
		
		int u0 = (int)Math.floor( u );
		int v0 = (int)Math.floor( v );
		int u1 = u0 + 1;
		int v1 = v0 + 1;
		
		if( u0 < 0 || v0 < 0 || u1 >= heights.length || v1 >= heights[0].length ) {
			//return new Vector4f( 0, 1, 0, -1000 ); // Falling off the edge of the world
			return new Vector4f( 0, 1, 0, 0 ); // Just treat map as flat beyond its borders
		}
		
		Vector3f p1 = new Vector3f( u0, heights[u0][v1], v1 );
		Vector3f p2 = new Vector3f( u1, heights[u1][v0], v0 );
		Vector3f p3;
		Vector3f normal;
		
		if( u - (int)u > 1 - (v - (int)v) ) {
			// Lower triangle
			p3 = new Vector3f( u1, heights[u1][v1], v1 );
			normal = Vector3f.cross( Vector3f.sub( p3, p1, null ), Vector3f.sub( p2, p3, null ), null );
			
		} else {
			// Upper triangle
			p3 = new Vector3f( u0, heights[u0][v0], v0 );
			normal = Vector3f.cross( Vector3f.sub( p3, p2, null ), Vector3f.sub( p1, p3, null ), null );
		}
		normal.normalise();
		
		return new Vector4f( normal.x, normal.y, normal.z, getHeightOfPointInTriangle( p1, p2, p3, u, v ) );
	}
}
