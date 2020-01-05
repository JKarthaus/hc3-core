package de.karthaus.heatingControl3.service;

import javax.inject.Singleton;

import de.karthaus.heatingControl3.model.PumpState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.karthaus.heatingControl3.model.HeatingControlContext;
import io.micronaut.context.annotation.Value;
import io.micronaut.scheduling.annotation.Scheduled;

/**
 * Main Class for heatingControl3
 * Buisiness logic
 *
 * @see "https://github.com/JKarthaus/hc3-core"
 */
@Singleton
public class CheckPumpService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${hc3.pump.garage.min-temp}")
    protected int garageMinTemp;

    private HeatingControlContext heatingControlContext;
    private PumpState pumpState;

     /**
     * @param heatingControlContext
     */
    public CheckPumpService(
            HeatingControlContext heatingControlContext,
            PumpState pumpState) {
        this.heatingControlContext = heatingControlContext;
        this.pumpState = pumpState;
    }

    public boolean checkCanShutdown() {
        boolean result = false;
        //TODO : implement this
        return result;
    }

    /**
     * Check of main Conditions
     * The resulting Pump State is stored in the Pump State Object
     */
    @Scheduled(fixedDelay = "30s", initialDelay = "5s")
    public void checkPump() {
        logger.debug("Check Pump condition Temp : {} > {} ", heatingControlContext.getTemp_combustionChamber(), garageMinTemp);

        // Condition I switch Pump ON
        if (heatingControlContext.getTemp_combustionChamber() > heatingControlContext.getBufferTemperature()) {
            pumpState.setPumpHeating(Boolean.TRUE);
            logger.info("Pump Heating -> ON");
            pumpState.setPumpGarage(Boolean.TRUE);
            logger.info("Pump Garage -> ON because Heating is ON");
        } else {
            pumpState.setPumpHeating(Boolean.FALSE);
            logger.info("Pump Heating OFF, becauce Temp Combustion Chamber {} Temp Buffer {}",
                    heatingControlContext.getTemp_combustionChamber(),
                    heatingControlContext.getBufferTemperature());
            // ConditionII switch Pump OFF
            if (heatingControlContext.getGarageTemperature() < garageMinTemp) {
                pumpState.setPumpGarage(Boolean.TRUE);
                logger.info("Pump Garage -> ON because the Temperature in Garage is lower than {}", garageMinTemp);
            } else {
                pumpState.setPumpGarage(Boolean.FALSE);
                logger.info("Pump Garage -> OFF because the Temperature in Garage {} is higher than the Minumum Temp {}",
                        heatingControlContext.getGarageTemperature(),
                        garageMinTemp);
            }
        }
    }
}
