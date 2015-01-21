package com.josh2112.FPSDemo.modeling;


public class Mesh {
	
	private String name;
	private int vertexArrayId;
	private int vertexCount;
	
	private boolean hasTextureCoords;
	private boolean hasNormals;
	
	private Material material;
	
	private BoundingBox boundingBox;
	
	public Mesh( String name, int vaoId, int vtxCount, boolean hasTexCoords, boolean hasNormals,
			BoundingBox boundingBox ) {
		this.name = name;
		this.vertexArrayId = vaoId;
		this.vertexCount = vtxCount;
		this.hasTextureCoords = hasTexCoords;
		this.hasNormals = hasNormals;
		this.boundingBox = boundingBox;
	}
	
	public String getName() { return name; }
	public int getVertexArrayId() { return vertexArrayId; }
	public int getVertexCount() { return vertexCount; }

	public boolean hasTextureCoordinates() {
		return hasTextureCoords;
	}
	
	public boolean hasNormals() {
		return hasNormals;
	}
	
	public Material getMaterial() {
		return material;
	}

	public void setMaterial( Material material ) {
		this.material = material;
	}

	public BoundingBox getBoundingBox() {
		return boundingBox;
	}
}
