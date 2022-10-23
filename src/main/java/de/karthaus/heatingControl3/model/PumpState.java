package de.karthaus.heatingControl3.model;

import javax.inject.Singleton;

@Singleton
public class PumpState {



    /*
     * Pump between Heating and Buffer
     */
    boolean pumpHeating;

    /**
     * Pump for Garage radiator
     */
    boolean pumpGarage;


    /**
     * Pump for House Main circuit
     */
    boolean pumpMainCircuit;

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

    public boolean isPumpMainCircuit() {
        return pumpMainCircuit;
    }

    public PumpState setPumpMainCircuit(boolean pumpMainCircuit) {
        this.pumpMainCircuit = pumpMainCircuit ;
        return this;
    }
}
