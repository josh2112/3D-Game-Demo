package com.josh2112.FPSDemo.modeling;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;

import com.josh2112.FPSDemo.OpenGLResource;
import com.josh2112.FPSDemo.math.Range;

public class ModelLoader implements OpenGLResource {

	public static final int ATTR_POSITIONS = 0;
	public static final int ATTR_TEXCOORDS = 1;
	public static final int ATTR_NORMALS = 2;
	
	private List<Integer> vertexArrayIds = new ArrayList<Integer>();
	private List<Integer> bufferIds = new ArrayList<Integer>();
	
	public Model loadFromArrays( String modelName, float[] vertices, int[] indices, float[] texCoords, float[] normals ) {
		int vaId = glGenVertexArrays();
		vertexArrayIds.add( vaId );
		glBindVertexArray( vaId );
		storeIndices( indices );
		storeAttributeListFloat( ATTR_POSITIONS, 3, vertices );
		if( texCoords != null ) storeAttributeListFloat( ATTR_TEXCOORDS, 2, texCoords );
		if( normals != null ) storeAttributeListFloat( ATTR_NORMALS, 3, normals );
		glBindVertexArray( 0 );
		
		return new Model( modelName, new Mesh( modelName, vaId, indices.length,
				texCoords != null, normals != null, calculateBoundingBox( vertices ) ) );
	}
	
	public Model loadFromObjFile( String modelName ) {
		ObjModelLoader modelLoader = new ObjModelLoader( modelName );
		
		IntBuffer buf = BufferUtils.createIntBuffer( modelLoader.meshes.size() );
		glGenVertexArrays( buf );
		int[] vaoIds = new int[modelLoader.meshes.size()];
		buf.get( vaoIds );
		
		List<Mesh> meshes = new ArrayList<Mesh>();
		
		int vaIndex = 0;
		for( ObjModelLoader.ObjMesh modelObj : modelLoader.meshes ) {
			
			int vaId = vaoIds[vaIndex++];
			vertexArrayIds.add( vaId );
			glBindVertexArray( vaId );
			storeIndices( modelObj.indexBuf );
			storeAttributeListFloat( ATTR_POSITIONS, 3, modelObj.vertexBuf );
			if( modelObj.hasTexCoords() ) storeAttributeListFloat( ATTR_TEXCOORDS, 2, modelObj.texCoordBuf );
			if( modelObj.hasNormals() ) storeAttributeListFloat( ATTR_NORMALS, 3, modelObj.normalBuf );
			meshes.add( new Mesh( modelObj.name, vaId, modelObj.indexBuf.length, modelObj.hasTexCoords(),
					modelObj.hasNormals(), calculateBoundingBox( modelObj.vertexBuf ) ) );
		}
		glBindVertexArray( 0 );
		
		return new Model( modelName, meshes );
	}
	
	private BoundingBox calculateBoundingBox( float[] vertices ) {
		Range xRange = getAxisRange( vertices, 0 );
		Range yRange = getAxisRange( vertices, 1 );
		Range zRange = getAxisRange( vertices, 2 );
		return new BoundingBox( xRange, yRange, zRange );
	}
	
	private Range getAxisRange( float[] vertices, int axis ) {
		float max = vertices[axis], min = vertices[axis];
		for( int i=axis; i<vertices.length; i += 3 ) {
			if( vertices[i] < min ) min = vertices[i];
			else if( vertices[i] > max ) max = vertices[i];
		}
		return new Range( min, max );
	}

	private void storeAttributeListFloat( int attribNum, int itemSize, float[] data ) {
		FloatBuffer buf = BufferUtils.createFloatBuffer( data.length );
		buf.put( data );
		buf.flip();
		
		int bufId = glGenBuffers();
		bufferIds.add( bufId );
		glBindBuffer( GL_ARRAY_BUFFER, bufId );
		glBufferData( GL_ARRAY_BUFFER, buf, GL_STATIC_DRAW );
		glVertexAttribPointer( attribNum, itemSize, GL_FLOAT, false, 0, 0 );
		glBindBuffer( GL_ARRAY_BUFFER, 0 );
	}
	
	private void storeIndices( int[] data ) {
		IntBuffer buf = BufferUtils.createIntBuffer( data.length );
		buf.put( data );
		buf.flip();
		 
		int bufId = glGenBuffers();
		bufferIds.add( bufId );
		glBindBuffer( GL_ELEMENT_ARRAY_BUFFER, bufId );
		glBufferData( GL_ELEMENT_ARRAY_BUFFER, buf, GL_STATIC_DRAW );
	}

	@Override
	public void deleteOpenGLResources() {
		for( int vertexArrayId : vertexArrayIds ) glDeleteVertexArrays( vertexArrayId );
		for( int bufferId : bufferIds ) glDeleteBuffers( bufferId );
	}
}
