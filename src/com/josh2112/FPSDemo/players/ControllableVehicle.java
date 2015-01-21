package com.josh2112.FPSDemo.players;

import org.lwjgl.input.Keyboard;

import com.josh2112.FPSDemo.entities.Vehicle;
import com.josh2112.FPSDemo.modeling.Model;

public class ControllableVehicle extends Vehicle implements Controllable {

	private static final float RUN_SPEED = 20;	 // units per second
	private static final float TURN_SPEED = (float)Math.PI; // rad per second
	
	public ControllableVehicle( Model model ) {
		super( model );
	}
	
	@Override
	public void checkInputs() {
		if( Keyboard.isKeyDown( Keyboard.KEY_W )) {
			this.moveVelocity = RUN_SPEED;
		}
		else if( Keyboard.isKeyDown( Keyboard.KEY_S )) {
			this.moveVelocity = -RUN_SPEED;
		}
		else this.moveVelocity = 0;
		
		if( Keyboard.isKeyDown( Keyboard.KEY_A )) {
			this.turnVelocity = TURN_SPEED;
		}
		else if( Keyboard.isKeyDown( Keyboard.KEY_D )) {
			this.turnVelocity = -TURN_SPEED;
		}
		else this.turnVelocity = 0;
	}
	
	@Override
	public void update( float elapsedSecs ) {
		checkInputs();
		super.update( elapsedSecs );
	}

}
