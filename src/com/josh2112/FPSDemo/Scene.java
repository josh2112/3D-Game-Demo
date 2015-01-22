package com.josh2112.FPSDemo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.opengl.Texture;

import com.josh2112.FPSDemo.camera.Camera;
import com.josh2112.FPSDemo.camera.ChaseCamera;
import com.josh2112.FPSDemo.camera.PointableCamera;
import com.josh2112.FPSDemo.entities.Entity;
import com.josh2112.FPSDemo.entities.Light;
import com.josh2112.FPSDemo.entities.Terrain;
import com.josh2112.FPSDemo.modeling.BoundingBox;
import com.josh2112.FPSDemo.modeling.Material;
import com.josh2112.FPSDemo.modeling.Model;
import com.josh2112.FPSDemo.modeling.ModelLoader;
import com.josh2112.FPSDemo.players.ControllableSphere;
import com.josh2112.FPSDemo.shaders.ShaderCache;
import com.josh2112.FPSDemo.shaders.ShaderProgram;

public class Scene implements OpenGLResource {

	private static final Logger log = Logger.getLogger( Scene.class.getName() );
	
	private ModelLoader modelLoader = new ModelLoader();
	private ShaderCache shaderCache = new ShaderCache();
	private TextureCache textureCache = new TextureCache();
	
	private Vector3f skyColor;
	private float fogDensity = 0.0035f;
	private float fogGradient = 5f;

	private List<Model> models = new ArrayList<>();
    private List<Entity> entities = new ArrayList<>();
    
    private Entity player;
    private Terrain terrain;
    
    private Light light;
    
    private Camera camera = new ChaseCamera();

	public void update( float elapsedSeconds ) {
		player.update( elapsedSeconds );
		for( Entity entity : entities ) entity.update( elapsedSeconds );
		
		camera.update( elapsedSeconds );
		
		player.collide( terrain );
	}

    public Entity createEntity( String modelName ) {
    	Model model = models.stream().filter( m -> m.getName().equals( modelName ) ).findFirst().orElse( null );
        if( model == null ) {
        	System.err.println( String.format( "Scene.CreateEntity(): Can't find model '%s'!", modelName ) );
            return null;
        }
        else return new Entity( model );
    }

    public void setPlayer( Entity player ) {
		this.player = player;
	}

	private void setTerrain( Terrain terrain ) {
		this.terrain = terrain;
	}

	public Entity getTerrain() {
		return terrain;
	}

	public Entity getPlayer() {
		return player;
	}
	
	public Camera getCamera() {
		return camera;
	}

    public Vector3f getSkyColor() {
		return skyColor;
	}
    
    public void setBackgroundColor( Vector3f c ) {
		skyColor = c;
	}

	public void addModel( Model model ) {
		models.add( model );
	}

	public List<Entity> getEntities() {
		return entities;
	}

	private ShaderProgram getShader( String name ) {
		return shaderCache.getShader( name );
	}
	
	private void setLight( Light light ) {
		this.light = light;
	}
	
	public Light getLight() {
		return light;
	}
	
	public float getFogGradient() {
		return fogGradient;
	}

	public float getFogDensity() {
		return fogDensity;
	}


	public Collection<ShaderProgram> getShaders() {
		return shaderCache.getShaders();
	}
	
	@Override
	public void deleteOpenGLResources() {
		modelLoader.deleteOpenGLResources();
		shaderCache.deleteOpenGLResources();
	}
	
	////////////////////////////////////////
	
	static Scene createTruckScene() {
		
		log.info( "Creating demo truck scene" );
		
		Scene scene = new Scene();
		scene.setBackgroundColor( new Vector3f( 0.5f, 0.75f, 0.93f ) );
		
		ShaderProgram simpleShader = scene.getShader( "Simple" );
		
		Texture terrainTexture = scene.textureCache.getTexture( "terrain-grass" );
		Texture aluminumTexture = scene.textureCache.getTexture( "aluminum2" );
		Texture treeTexture = scene.textureCache.getTexture( "lowPolyTree" );
		
		Vector3f darkGreenColor = new Vector3f( 0, 0.4f, 0 );
		Vector3f whiteColor = new Vector3f( 1, 1, 1 );
		
		Material truckBodyMaterial = new Material( simpleShader, aluminumTexture, whiteColor, 10, 1 );
		Material tireMaterial = new Material( simpleShader, null, new Vector3f( 0.5f, 0.5f, 0.5f ), 1, 0.2f );
		Material terrainMaterial = new Material( simpleShader, terrainTexture, darkGreenColor, 1, 0 );
		Material treeMaterial = new Material( simpleShader, treeTexture, darkGreenColor, 1, 0 );
		
		// The tree model is big -- scale it down to 3 units high.
		Model treeModel = scene.modelLoader.loadFromObjFile( "lowPolyTree" );
		treeModel.setMaterial( treeMaterial );
		BoundingBox treeBBox = treeModel.getModelBoundingBox();
		float treeScaleFactor = 3.0f / treeBBox.getMaxY();
		
		//Terrain.HeightMap heightMap = Terrain.HeightMap.create( "heightMap257", 5, 100, 8 );
		Terrain.HeightMap heightMap = Terrain.HeightMap.create( "heightMap10x10", 5, 10, 5 );
		Terrain terrain = Terrain.generate( scene.modelLoader, terrainMaterial, heightMap );
		scene.setTerrain( terrain );
		
		List<Entity> trees = terrain.populateWithPlant( treeModel, 50 );
		trees.forEach( t -> t.setScale( treeScaleFactor ) );
		//scene.getModeledEntities().addAll( trees );
		
		//Model truckModel = scene.modelLoader.loadFromObjFile( "truck" );
		//truckModel.getMeshes().stream().filter( m -> m.getName().startsWith( "Cube" ) ).forEach( m -> m.setMaterial( truckBodyMaterial ) );
		//truckModel.getMeshes().stream().filter( m -> m.getName().startsWith( "Cylinder" ) ).forEach( m -> m.setMaterial( tireMaterial ) );
		//scene.addModel( truckModel );
		//ControllableVehicle player = new ControllableVehicle( truckModel );
		
		Model sphereModel = scene.modelLoader.loadFromObjFile( "sphere" );
		sphereModel.setMaterial( truckBodyMaterial );
		scene.addModel( sphereModel );
		ControllableSphere player = new ControllableSphere( sphereModel );
		player.getLocation().translate( 0, 0.5f, 0 );
	    
		scene.setPlayer( player );
	    
	    Light light = new Light( new Vector3f( -50, 50, 50 ), new Vector3f( 1, 1, 1 ) );  
	    scene.setLight( light );
	    
	    //PointableCamera camera = new PointableCamera();
	    //camera.lookAt( new Vector3f( -2, 2, 10 ), player.getLocation() );
	    ChaseCamera camera = new ChaseCamera();
	    camera.setSubject( player );
	    scene.camera = camera;
		
		return scene;
	}
}
