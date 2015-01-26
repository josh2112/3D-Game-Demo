package com.josh2112.FPSDemo.modeling;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.opengl.Texture;

import com.josh2112.FPSDemo.Resources;

public class ObjModelLoader {
	
	public static class FaceDef {
		public int iv, it, in;
		public FaceDef( int iv, int it, int in ) { this.iv = iv; this.it = it; this.in = in; }
	}
	
	public static class Material {
		public String name;
		public Vector3f color;
		public Texture texture;
	}
	
	public static class ObjMesh {
		private List<FaceDef> faceVertices = new ArrayList<>();
		private boolean hasTexCoords, hasNormals;

		public String name;
		public Material material;
		public float[] vertexBuf, texCoordBuf, normalBuf;
		public int[] indexBuf;
		
		public boolean hasTexCoords() { return hasTexCoords; }
		public boolean hasNormals() { return hasNormals; }
		
		private void processFaces( List<Vector3f> allVertices, List<Vector2f> allTexCoords, List<Vector3f> allNormals ) {
			
			int minIv, maxIv, minIt, maxIt, minIn, maxIn;
			minIv = maxIv = faceVertices.get( 0 ).iv;
			minIt = maxIt = faceVertices.get( 0 ).it;
			minIn = maxIn = faceVertices.get( 0 ).in;
			for( FaceDef faceDef : faceVertices ) {
				if( faceDef.iv < minIv ) minIv = faceDef.iv;
				else if( faceDef.iv > maxIv ) maxIv = faceDef.iv;
				if( faceDef.it < minIt ) minIt = faceDef.it;
				else if( faceDef.it > maxIt ) maxIt = faceDef.it;
				if( faceDef.in < minIn ) minIn = faceDef.in;
				else if( faceDef.in > maxIn ) maxIn = faceDef.in;
			}
			
			hasTexCoords = maxIt > 0;
			hasNormals = maxIn > 0;
			
			indexBuf = new int[faceVertices.size()];
			vertexBuf = new float[faceVertices.size()*3];
			texCoordBuf = new float[hasTexCoords ? faceVertices.size()*2 : 0];
			normalBuf = new float[hasNormals ? faceVertices.size()*3 : 0];
			
			int idx = 0;
			for( FaceDef faceVtx : faceVertices ) {
				int vtxIdx = (int)faceVtx.iv;
				int texIdx = (int)faceVtx.it;
				int nmlIdx = (int)faceVtx.in;
				
				indexBuf[idx] = idx;
				
				Vector3f vertex = allVertices.get( vtxIdx );
				vertexBuf[idx*3+0] = vertex.x;
				vertexBuf[idx*3+1] = vertex.y;
				vertexBuf[idx*3+2] = vertex.z;
				
				if( hasTexCoords ) {
					Vector2f texCoord = allTexCoords.get( texIdx );
					texCoordBuf[idx*2+0] = texCoord.x;
					texCoordBuf[idx*2+1] = texCoord.y;
				}
				
				if( hasNormals ) {
					Vector3f normal = allNormals.get( nmlIdx );
					normalBuf[idx*3+0] = normal.x;
					normalBuf[idx*3+1] = normal.y;
					normalBuf[idx*3+2] = normal.z;
				}
				
				idx++;
			}
		}
	}
	
	public List<Material> materials = new ArrayList<>();
	public List<ObjMesh> meshes = new ArrayList<>();
	
	private List<Vector3f> vertices = new ArrayList<>();
	private List<Vector2f> texCoords = new ArrayList<>(); 
	private List<Vector3f> normals = new ArrayList<>();
	
	public ObjModelLoader( String modelName ) {
		String path = Resources.pathForResource( Resources.Type.Model, modelName );
		
		ObjMesh currentMesh = null;
		
		try {
			for( String line : Files.readAllLines( Paths.get( path ) ) ) {
				currentMesh = parseObjLine( currentMesh, line.trim() );
			}
			if( currentMesh != null ) meshes.add( currentMesh );
			
			for( ObjMesh mesh : meshes ) {
				mesh.processFaces( vertices, texCoords, normals );
			}
			
		} catch( IOException e ) {
			System.err.println( "ObjLoader.loadObject: Unable to read file " + path );
			e.printStackTrace();
		}
	}

	private ObjMesh parseObjLine( ObjMesh currentMesh, String line ) {
		String[] tokens = line.split( "\\s+" );
		
		if( tokens[0].equals( "mtllib" ) ) loadMaterial( tokens[1].split( "\\." )[0] );
		else if( tokens[0].equals( "o" ) ) currentMesh = startObject( currentMesh, tokens[1] );
		else if( tokens[0].equals( "usemtl" ) ) setMaterial( currentMesh, tokens[1] );
		else if( tokens[0].equals( "v" ) ) vertices.add( parseVector( tokens[1], tokens[2], tokens[3] ) );
		else if( tokens[0].equals( "vt" ) ) texCoords.add( parseVector( tokens[1], tokens[2] ) );
		else if( tokens[0].equals( "vn" ) ) normals.add( parseVector( tokens[1], tokens[2], tokens[3] ) );
		else if( tokens[0].equals( "f" ) ) addFace( currentMesh, tokens[1], tokens[2], tokens[3] );
		
		return currentMesh;
	}

	private ObjMesh startObject( ObjMesh currentMesh, String name ) {
		if( currentMesh != null ) meshes.add( currentMesh );
		ObjMesh newMesh = new ObjMesh();
		newMesh.name = name;
		return newMesh;
	}

	private void setMaterial( ObjMesh currentMesh, String matName ) {
		currentMesh.material = materials.stream().filter( m -> m.name == matName ).findFirst().orElse( null );
	}

	private void addFace( ObjMesh currentMesh, String v1, String v2, String v3 ) {
		String[] vertexDefs = new String[] { v1, v2, v3 };
		for( String vertexDef : vertexDefs ) {
			Stream<Integer> values = Arrays.stream( vertexDef.split( "/" ) ).map( v ->
				 v.isEmpty() ? -1 : Integer.parseInt( v ) - 1 );
			List<Integer> result = values.collect( Collectors.toList() );
			currentMesh.faceVertices.add( new FaceDef( result.get( 0 ), result.get( 1 ), result.get( 2 ) ) );
		}
	}

	private void loadMaterial( String matName ) {
		String path = Resources.pathForResource( Resources.Type.Material, matName );
		
		Material material = new Material();
		
		try {
			for( String line : Files.readAllLines( Paths.get( path ) ) ) {
				parseMaterialLine( material, line.trim() );
			}
		} catch (IOException e) {
			System.err.println( "ObjLoader.loadMaterial: Unable to read file " + path );
			e.printStackTrace();
		}
	}

	private void parseMaterialLine( Material material, String line ) {
		String[] tokens = line.split( "\\s+" );
		
		if( tokens[0].equals( "newmtl" ) ) {
			if( material != null ) materials.add( material );
			material = new Material();
			material.name = tokens[1];
		}
		else if( tokens[0].equals( "Kd" ) ) {
			material.color = parseVector( tokens[1], tokens[2], tokens[3] );
		}
	}
	
	private Vector3f parseVector( String x, String y, String z ) {
		return new Vector3f( Float.parseFloat( x ),
				Float.parseFloat( y ), Float.parseFloat( z ) );
	}
	
	private Vector2f parseVector( String u, String v ) {
		return new Vector2f( Float.parseFloat( u ), Float.parseFloat( v ) );
	}
}
