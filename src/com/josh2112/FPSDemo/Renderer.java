package com.josh2112.FPSDemo;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.GL_TEXTURE1;
import static org.lwjgl.opengl.GL13.GL_TEXTURE2;
import static org.lwjgl.opengl.GL13.GL_TEXTURE3;
import static org.lwjgl.opengl.GL13.GL_TEXTURE4;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Matrix4f;
import org.newdawn.slick.opengl.TextureImpl;

import com.josh2112.FPSDemo.entities.Entity;
import com.josh2112.FPSDemo.modeling.Material;
import com.josh2112.FPSDemo.modeling.Mesh;
import com.josh2112.FPSDemo.modeling.ModelLoader;
import com.josh2112.FPSDemo.modeling.TerrainMaterial;
import com.josh2112.FPSDemo.shaders.HasFog;
import com.josh2112.FPSDemo.shaders.HasLightSource;
import com.josh2112.FPSDemo.shaders.HasModelViewProjectionMatrices;
import com.josh2112.FPSDemo.shaders.HasSpecular;
import com.josh2112.FPSDemo.shaders.HasTextureBlend;
import com.josh2112.FPSDemo.shaders.ShaderProgram;
import com.josh2112.FPSDemo.shaders.TerrainShader;

public class Renderer {

	private static class MeshWithEntity {
		public Mesh mesh;
		public Entity entity;
	}
	
	private static final float FIELD_OF_VIEW_Y_DEG = 60;
	private static final float CLIP_NEAR = 0.1f;
	private static final float CLIP_FAR = 500.0f;
	
	private Scene scene;
	private Map<Material,List<MeshWithEntity>> meshesByMaterial = new HashMap<>();
	
	private Matrix4f projectionMatrix = new Matrix4f();
	private Matrix4f viewMatrix = new Matrix4f();
	private Matrix4f modelMatrix = new Matrix4f();
	
	private ShaderProgram activeShader = null;
	
	public Renderer() {
		glEnable( GL_DEPTH_TEST );
		glEnable( GL_CULL_FACE );
		glCullFace( GL_BACK );
		
		resetProjection();
	}
	
	public void resetProjection() {
		glViewport( 0, 0, Display.getWidth(), Display.getHeight() );
		
		float aspect = (float)Display.getWidth()/Display.getHeight();
		float tanHalfFov = (float)Math.toRadians( FIELD_OF_VIEW_Y_DEG / 2 );
		float clipRange = (CLIP_FAR - CLIP_NEAR);
		
		projectionMatrix.setIdentity();
		projectionMatrix.m00 = 1.0f / (tanHalfFov * aspect);
		projectionMatrix.m11 = 1.0f / tanHalfFov;
		projectionMatrix.m22 = -(CLIP_FAR + CLIP_NEAR) / clipRange;
		projectionMatrix.m23 = -1.0f;
		projectionMatrix.m32 = -2.0f * CLIP_FAR * CLIP_NEAR / clipRange;
		projectionMatrix.m33 = 0.0f;
		
		if( activeShader instanceof HasModelViewProjectionMatrices ) {
			((HasModelViewProjectionMatrices)activeShader).loadProjMatrix( projectionMatrix );
		}
	}
	
	public Scene getScene() {
		return scene;
	}
	
	public void setScene( Scene scene ) {
		this.scene = scene;
		
		glClearColor( scene.getSkyColor().x, scene.getSkyColor().y,
				scene.getSkyColor().z, 1.0f );
		
		meshesByMaterial.clear();
		
		List<Entity> allEntities = new ArrayList<Entity>( scene.getEntities() );
		allEntities.add( scene.getTerrain() );
		if( scene.getPlayer().getModel() != null ) {
			allEntities.add( scene.getPlayer() );
		}
		
		for( Entity entity : allEntities ) {
			for( Mesh mesh : entity.getModel().getMeshes() ) {
				Material material = mesh.getMaterial();
				MeshWithEntity mwe = new MeshWithEntity();
				mwe.mesh = mesh;
				mwe.entity = entity;
				if( !meshesByMaterial.containsKey( material ) ) {
					meshesByMaterial.put( material, new ArrayList<MeshWithEntity>() );
				}
				meshesByMaterial.get( material ).add( mwe );
			}
		}
	}
	
	public void render() {
		glClear( GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT );
		
		scene.getCamera().putViewMatrix( viewMatrix );
		
		if( activeShader instanceof HasModelViewProjectionMatrices ) {
			((HasModelViewProjectionMatrices)activeShader).loadViewMatrix( viewMatrix );
		}
		
		for( Material material : meshesByMaterial.keySet() ) {
			List<MeshWithEntity> meshList = meshesByMaterial.get( material );
			
			if( activeShader != material.getShader() ) prepareShader( material.getShader() );
			prepareMaterial( material );
			
			for( MeshWithEntity mwe : meshList ) {
				render( mwe.mesh, mwe.entity );
			}
		}
		
		prepareShader( null );
		
		if( scene.getHeadsUpDisplay() != null ) {
			glLoadIdentity();
			glOrtho( 0.0, Display.getWidth(), Display.getHeight(), 0.0, -1.0, 1.0 );
			
			glEnable( GL_BLEND );
			glBlendFunc( GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA );
			
			scene.getHeadsUpDisplay().render();
			
			glDisable( GL_BLEND );
		}
        
        glFlush();
	}
	
	private void prepareShader( ShaderProgram shader ) {
		if( activeShader != null ) activeShader.stop();
		activeShader = shader;
		
		if( activeShader != null ) {
			activeShader.start();
        	if( activeShader instanceof HasModelViewProjectionMatrices ) {
				((HasModelViewProjectionMatrices)activeShader).loadProjMatrix( projectionMatrix );
				((HasModelViewProjectionMatrices)activeShader).loadViewMatrix( viewMatrix );
			}
        	
			if( activeShader instanceof HasLightSource ) {
				((HasLightSource)activeShader).loadLight( scene.getLight() );
			}
			
			if( activeShader instanceof HasFog ) {
				((HasFog)activeShader).loadFogParams( scene.getFogDensity(), scene.getFogGradient() );
				((HasFog)activeShader).loadSkyColor( scene.getSkyColor() );
			}
			
			if( activeShader instanceof HasTextureBlend ) {
				((TerrainShader)activeShader).loadTerrainDimension( scene.getTerrain().getTerrainDimension() );
			}
		}
	}
	
	private void prepareMaterial( Material material ) {
		if( activeShader instanceof HasSpecular ) {
			((HasSpecular)activeShader).loadColor( material.getColor() );
			((HasSpecular)activeShader).loadSpecularParams( material.getShineDamper(),
					material.getReflectivity() );
		}
		if( material.getTexture() != null ) {
			glActiveTexture( GL_TEXTURE0 );
			glBindTexture( GL_TEXTURE_2D, material.getTexture().getTextureID() );
		}
		else if( material instanceof TerrainMaterial ) {
			((TerrainShader)activeShader).connectTextureUnits();
			TextureBlend terrainMaterial = ((TerrainMaterial)material).getTextureBlend();
			glActiveTexture( GL_TEXTURE0 );
			glBindTexture( GL_TEXTURE_2D, terrainMaterial.getBlendTexture().getTextureID() );
			glActiveTexture( GL_TEXTURE1 );
			glBindTexture( GL_TEXTURE_2D, terrainMaterial.getBaseTexture().getTextureID() );
			glActiveTexture( GL_TEXTURE2 );
			glBindTexture( GL_TEXTURE_2D, terrainMaterial.getRedTexture().getTextureID() );
			glActiveTexture( GL_TEXTURE3 );
			glBindTexture( GL_TEXTURE_2D, terrainMaterial.getGreenTexture().getTextureID() );
			glActiveTexture( GL_TEXTURE4 );
			glBindTexture( GL_TEXTURE_2D, terrainMaterial.getBlueTexture().getTextureID() );
		} else {
			glBindTexture( GL_TEXTURE_2D, 0 );
		}
	}

	private void render( Mesh mesh, Entity entity ) {
		modelMatrix.setIdentity();
        modelMatrix.translate( entity.getLocation() );
        
        Matrix4f.mul( modelMatrix, entity.getRotationMatrix(), modelMatrix );
        
        modelMatrix.scale( entity.getScale() );   

        if( activeShader instanceof HasModelViewProjectionMatrices ) {
    		((HasModelViewProjectionMatrices)activeShader).loadModelMatrix( modelMatrix );
    	}
        
        glBindVertexArray( mesh.getVertexArrayId() );
        
        glEnableVertexAttribArray( ModelLoader.ATTR_POSITIONS );
        if( mesh.hasTextureCoordinates() ) glEnableVertexAttribArray( ModelLoader.ATTR_TEXCOORDS );
        if( mesh.hasNormals() ) glEnableVertexAttribArray( ModelLoader.ATTR_NORMALS );

        glDrawElements( GL_TRIANGLES, mesh.getVertexCount(), GL_UNSIGNED_INT, 0 );

        glDisableVertexAttribArray( ModelLoader.ATTR_POSITIONS );
        if( mesh.hasTextureCoordinates() ) glDisableVertexAttribArray( ModelLoader.ATTR_TEXCOORDS );
        if( mesh.hasNormals() ) glDisableVertexAttribArray( ModelLoader.ATTR_NORMALS );
        
        glBindVertexArray( 0 );
 	}

	public void setWireframe( boolean wireframe ) {
		if( wireframe ) {
			glPolygonMode( GL_FRONT_AND_BACK, GL_LINE );
		} else {
			glPolygonMode( GL_FRONT_AND_BACK, GL_FILL );
		}
	}
}
