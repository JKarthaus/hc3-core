package de.karthaus.heatingControl3.model;

import javax.inject.Singleton;

@Singleton
public class HeatingControlContext {

	private boolean shutdownRequestet;
	private double temp_combustionChamber;
	private double flowTemperature;
	private double returnTemperature;

	private double garageTemperature;

	private double bufferTemperature;

	public double getTemp_combustionChamber() {
		return temp_combustionChamber;
	}

	public void setTemp_combustionChamber(double temp_combustionChamber) {
		this.temp_combustionChamber = temp_combustionChamber;
	}

	public double getFlowTemperature() {
		return flowTemperature;
	}

	public void setFlowTemperature(double flowTemperature) {
		this.flowTemperature = flowTemperature;
	}

	public double getReturnTemperature() {
		return returnTemperature;
	}

	public void setReturnTemperature(double returnTemperature) {
		this.returnTemperature = returnTemperature;
	}

	public double getGarageTemperature() {
		return garageTemperature;
	}

	public void setGarageTemperature(double garageTemperature) {
		this.garageTemperature = garageTemperature;
	}

	public double getBufferTemperature() {
		return bufferTemperature;
	}

	public void setBufferTemperature(double bufferTemperature) {
		this.bufferTemperature = bufferTemperature;
	}

	public boolean isShutdownRequestet() {
		return shutdownRequestet;
	}

	public void setShutdownRequestet(boolean shutdownRequestet) {
		this.shutdownRequestet = shutdownRequestet;
	}

}
