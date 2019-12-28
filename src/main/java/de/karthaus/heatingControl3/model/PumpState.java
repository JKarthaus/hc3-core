package de.karthaus.heatingControl3.model;

import javax.inject.Singleton;

@Singleton
public class PumpState {

	boolean pumpHeating;

	boolean pumpGarage;
	public boolean isPumpHeating() {
		return pumpHeating;
	}

	public void setPumpHeating(boolean pumpHeating) {
		this.pumpHeating = pumpHeating;
	}

	public boolean isPumpGarage() {
		return pumpGarage;
	}

	public void setPumpGarage(boolean pumpGarage) {
		this.pumpGarage = pumpGarage;
	}

}
