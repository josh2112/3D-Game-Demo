package com.josh2112.FPSDemo.players;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;

import com.josh2112.FPSDemo.SceneContext;
import com.josh2112.FPSDemo.entities.Sphere;
import com.josh2112.FPSDemo.math.MathEx;
import com.josh2112.FPSDemo.modeling.Model;

public class ControllableSphere extends Sphere implements Controllable {

	private static float MOVE_FORCE = 20;
	private static float FALLING_MOVE_FORCE = 5;
	
	public ControllableSphere( Model model ) {
		super( model );
	}

	@Override
	public void checkInputs() {
		this.force.set( 0, 0, 0 );
		
		Vector3f right = SceneContext.get().getRenderer().getCamera().getRight();
		Vector3f forward = Vector3f.cross( MathEx.AxisY, right, null );
		
		float moveForce = isFalling ? FALLING_MOVE_FORCE : MOVE_FORCE;
		
		if( Keyboard.isKeyDown( Keyboard.KEY_W )) {
			Vector3f.add( this.force, MathEx.scale( forward, moveForce, null ), this.force );
		}
		else if( Keyboard.isKeyDown( Keyboard.KEY_S )) {
			Vector3f.add( this.force, MathEx.scale( forward, -moveForce, null ), this.force );
		}
		
		if( Keyboard.isKeyDown( Keyboard.KEY_A )) {
			Vector3f.add( this.force, MathEx.scale( right, -moveForce, null ), this.force );
		}
		else if( Keyboard.isKeyDown( Keyboard.KEY_D )) {
			Vector3f.add( this.force, MathEx.scale( right, moveForce, null ), this.force );
		}
	}
	
	@Override
	public void update( float elapsedSecs ) {
		checkInputs();
		super.update( elapsedSecs );
	}

}
