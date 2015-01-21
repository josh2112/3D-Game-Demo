package com.josh2112.FPSDemo.math;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;

public class MathEx {
	public static final Vector3f AxisX = new Vector3f( 1, 0, 0 );
	public static final Vector3f AxisY = new Vector3f( 0, 1, 0 );
	public static final Vector3f AxisZ = new Vector3f( 0, 0, 1 );
	
	public static float clamp( float val, float min, float max ) {
		return Math.min( max, Math.max( min, val ) ); 
	};
	
	public static void applyRotation( Matrix4f matrix, Vector3f rotationAngles ) {
		applyRotation( matrix, rotationAngles.x, rotationAngles.y, rotationAngles.z );
	}
	
	public static void applyRotation( Matrix4f matrix, float rotx, float roty, float rotz ) {
		matrix.rotate( rotx, AxisX );
		matrix.rotate( roty, AxisY );
		matrix.rotate( rotz, AxisZ );
	}
	
	public static void setRotationMatrixFromVectors( Matrix4f m, Vector3f right, Vector3f up, Vector3f forward ) {
		m.m00 = right.x;
		m.m10 = right.y;
		m.m20 = right.z;
		m.m30 = 0;
		m.m01 = up.x;
		m.m11 = up.y;
		m.m21 = up.z;
		m.m31 = 0;
		m.m02 = -forward.x;
		m.m12 = -forward.y;
		m.m22 = -forward.z;
		m.m32 = 0;
		m.m03 = 0;
		m.m13 = 0;
		m.m23 = 0;
		m.m33 = 1;
	}

	public static void setQuaternionFromRotationMatrix( Quaternion quat, Matrix4f m ) {
		quat.w = (float)Math.sqrt(1.0 + m.m00 + m.m11 + m.m22) / 2.0f;
		float w4 = (4.0f * quat.w);
		quat.x = (m.m21 - m.m12) / w4;
		quat.y = (m.m02 - m.m20) / w4;
		quat.z = (m.m10 - m.m01) / w4;
	}
	
	public static void setRotationMatrixFromQuaternion( Matrix4f m, Quaternion quat ) {
		float xx = quat.x * quat.x;
		float xy = quat.x * quat.y;
		float xz = quat.x * quat.z;
		float xw = quat.x * quat.w;
		float yy = quat.y * quat.y;
		float yz = quat.y * quat.z;
		float yw = quat.y * quat.w;
		float zz = quat.z * quat.z;
		float zw = quat.z * quat.w;
		m.m00 = 1 - 2 * (yy + zz);
		m.m01 = 2 * (xy - zw);
		m.m02 = 2 * (xz + yw);
		m.m03 = 0;
		m.m10 = 2 * (xy + zw);
		m.m11 = 1 - 2 * (xx + zz);
		m.m12 = 2 * (yz - xw);
		m.m13 = 0;
		m.m20 = 2 * (xz - yw);
		m.m21 = 2 * (yz + xw);
		m.m22 = 1 - 2 * (xx + yy);
		m.m23 = 0;
		m.m30 = 0;
		m.m31 = 0;
		m.m32 = 0;
		m.m33 = 1;
	}
	
	public static Vector3f scale( Vector3f vec, float scale, Vector3f result ) {
		if( result == null ) result = new Vector3f( vec );
		else result.set( vec );
		result.scale( scale );
		return result;
	}
}
