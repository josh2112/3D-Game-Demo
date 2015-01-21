package com.josh2112.FPSDemo.modeling;

import org.lwjgl.util.vector.Vector3f;

import com.josh2112.FPSDemo.math.Range;

public class BoundingBox implements Cloneable {
	
	private float minX, maxX, minY, maxY, minZ, maxZ;
	
	public BoundingBox( float minX, float maxX, float minY, float maxY, float minZ, float maxZ ) {
		this.minX = minX;
		this.maxX = maxX;
		this.minY = minY;
		this.maxY = maxY;
		this.minZ = minZ;
		this.maxZ = maxZ;
	}

	public BoundingBox( Range xRange, Range yRange, Range zRange ) {
		this( xRange.getMin(), xRange.getMax(), yRange.getMin(), yRange.getMax(), zRange.getMin(), zRange.getMax() );
	}

	public static BoundingBox containing( Iterable<BoundingBox> bboxes ) {
		BoundingBox containingBBox = bboxes.iterator().next().clone(); 
		for( BoundingBox bbox : bboxes ) {
			containingBBox.minX = Math.min( containingBBox.minX, bbox.minX );
			containingBBox.minY = Math.min( containingBBox.minY, bbox.minY );
			containingBBox.minZ = Math.min( containingBBox.minZ, bbox.minZ );
			containingBBox.maxX = Math.max( containingBBox.maxX, bbox.maxX );
			containingBBox.maxY = Math.max( containingBBox.maxY, bbox.maxY );
			containingBBox.maxZ = Math.max( containingBBox.maxZ, bbox.maxZ );
		}
		return containingBBox;
	}
	
	@Override
	public BoundingBox clone() {
		return new BoundingBox( minX, maxX, minY, maxY, minZ, maxZ );
	}

	public float getMinX() {
		return minX;
	}

	public void setMinX( float minX ) {
		this.minX = minX;
	}

	public float getMinY() {
		return minY;
	}

	public void setMinY( float minY ) {
		this.minY = minY;
	}

	public float getMinZ() {
		return minZ;
	}

	public void setMinZ( float minZ ) {
		this.minZ = minZ;
	}

	public float getMaxX() {
		return maxX;
	}

	public void setMaxX( float maxX ) {
		this.maxX = maxX;
	}

	public float getMaxY() {
		return maxY;
	}

	public void setMaxY( float maxY ) {
		this.maxY = maxY;
	}

	public float getMaxZ() {
		return maxZ;
	}

	public void setMaxZ( float maxZ ) {
		this.maxZ = maxZ;
	}
	
	public Vector3f getDimensions() {
		return new Vector3f( maxX - minX, maxY - minY, maxZ - minZ );
	}
}
