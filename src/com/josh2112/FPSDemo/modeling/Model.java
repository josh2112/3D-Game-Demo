package com.josh2112.FPSDemo.modeling;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Model {
	
	private String name;
	private List<Mesh> meshes = new ArrayList<Mesh>();
	
	public Model( String name, Mesh mesh ) {
		this.name = name;
		meshes.add( mesh );
	}
	
	public Model( String name, List<Mesh> mesh ) {
		this.name = name;
		meshes.addAll( mesh );
	}
	
	public List<Mesh> getMeshes() {
		return meshes;
	}
	
	public String getName() {
		return name;
	}

	public BoundingBox getModelBoundingBox() {
		List<BoundingBox> bboxes = getMeshes().stream().map( m -> m.getBoundingBox() ).collect( Collectors.toList() );
		return BoundingBox.containing( bboxes );
	}

	public void setMaterial( Material mat ) {
		getMeshes().stream().forEach( m -> m.setMaterial( mat ) );
	}
}
