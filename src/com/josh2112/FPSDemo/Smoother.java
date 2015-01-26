package com.josh2112.FPSDemo;

/**
 * This class implements a smoother for double values, meaning it returns
 * the average of the last [X] values fed to it.
 * 
 * @author jf334
 *
 */
public class Smoother {
	
	private double[] values;
	private int idx = 0;
	private boolean isInitialied = false;
	private double total;
	
	public Smoother( int length ) {
		values = new double[length];
	}
	
	public void add( double value ) {
		if( !isInitialied ) {
			for( int i=0; i<values.length; ++i ) values[i] = value;
			total = value * values.length;
			isInitialied = true;
		}
		else {
			total -= values[idx];
			total += value;
			values[idx++] = value;
			if( idx >= values.length ) idx = 0;
		}
	}
	
	public double getAverage() {
		return total / values.length;
	}
}
