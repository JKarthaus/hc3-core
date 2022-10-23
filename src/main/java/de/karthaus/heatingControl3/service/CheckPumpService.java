package de.karthaus.heatingControl3.service;

import de.karthaus.heatingControl3.model.HeatingControlContext;
import de.karthaus.heatingControl3.model.PumpState;
import io.micronaut.context.annotation.Value;
import io.micronaut.scheduling.annotation.Scheduled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.time.LocalTime;


/**
 * Main Class for heatingControl3
 * Buisiness logic
 *
 * @see "https://github.com/JKarthaus/hc3-core"
 */
@Singleton
public class CheckPumpService {

    public static final String MANUAL_PUMP_STATE_ON = "on";
    public static final String MANUAL_PUMP_STATE_OFF = "off";
    public static final String MANUAL_PUMP_STATE_AUTO = "auto";


    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${hc3.pump.garage.min-temp}")
    protected int garageMinTemp;

    @Value("${hc3.pump.heating.min-diff-temp}")
    protected int minDiffTemp;

    @Value("${hc3.pump.main-circuit.switch-on-temp}")
    protected int mainCircuitPumpSwitchOnTemp;

    @Value("${hc3.pump.main-circuit.time-range-from}")
    protected String mainCircuitPumpTimeRangeFrom;

    @Value("${hc3.pump.main-circuit.time-range-until}")
    protected String mainCircuitPumpTimeRangeUntil;

    private String manualPumpState;

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
        this.manualPumpState = MANUAL_PUMP_STATE_AUTO;
    }

    public boolean checkCanShutdown() {
        boolean result = false;
        //TODO : implement this
        return result;
    }

    public String getManualPumpState() {
        return manualPumpState;
    }

    public void setManualPumpState(String manualPumpState) {
        this.manualPumpState = manualPumpState;
        if (this.manualPumpState.equalsIgnoreCase(MANUAL_PUMP_STATE_ON)) {
            pumpState.setPumpMainCircuit(true);
        }
        if (this.manualPumpState.equalsIgnoreCase(MANUAL_PUMP_STATE_OFF)) {
            pumpState.setPumpMainCircuit(false);
        }
    }

    /**
     * Check of main Conditions
     * The resulting Pump State is stored in the Pump State Object
     */
    @Scheduled(fixedDelay = "30s", initialDelay = "5s")
    public void checkPump() {
        logger.info("Check Pump conditions... Temp");
        /*
         * Condition for heating Pump and garage Pump
         *
         * if temperature Combustion Chamber > (temperature Buffer + 5)
         *      heatingPump ->  ON
         *      garagePump  ->  ON
         * else
         *      heatingPump ->  OFF
         *      if garageTemp < minTemp
         *          garagePump  ->  ON
         *      else
         *          garagePump  ->  OFF
         *
         */
        if (heatingControlContext.getTemp_combustionChamber()
                > (heatingControlContext.getBufferTemperature() + minDiffTemp)
        ) {
            pumpState.setPumpHeating(Boolean.TRUE);
            logger.info("Pump Heating -> ON");
            pumpState.setPumpGarage(Boolean.TRUE);
            logger.info("Pump Garage -> ON because Heating is ON");
        } else {
            pumpState.setPumpHeating(Boolean.FALSE);
            logger.info("Pump Heating OFF, becauce Temp Combustion Chamber Temp:{} not > Temp Buffer:{} + {}",
                    heatingControlContext.getTemp_combustionChamber(),
                    heatingControlContext.getBufferTemperature(),
                    minDiffTemp
            );
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
        /*
         * Condition for mainCircuitPump
         *
         * if state = automatic
         *
         * if   temperatureBuffer > minMainCircuitTemp
         *      AND actualTime > minTimeRange
         *      AND actualTime < maxTimeRange
         *          mainCircuitPump ->  ON
         * else
         *          mainCircuitPump ->  OFF
         */
        if (this.manualPumpState.equalsIgnoreCase(MANUAL_PUMP_STATE_AUTO)) {
            if (heatingControlContext.getBufferTemperature() > mainCircuitPumpSwitchOnTemp
                    && LocalTime.now().isAfter(LocalTime.parse(mainCircuitPumpTimeRangeFrom))
                    && LocalTime.now().isBefore(LocalTime.parse(mainCircuitPumpTimeRangeUntil))
            ) {
                pumpState.setPumpMainCircuit(Boolean.TRUE);
                logger.info("Pump mainCircuit -> ON because we are in time range from:{} until:{} and temp Buffer > {}",
                        mainCircuitPumpTimeRangeFrom,
                        mainCircuitPumpTimeRangeUntil,
                        mainCircuitPumpSwitchOnTemp);
            } else {
                pumpState.setPumpMainCircuit(Boolean.FALSE);
                logger.info("Pump mainCircuit -> OFF because we are out of time range from:{} until:{} or temp Buffer < {}",
                        mainCircuitPumpTimeRangeFrom,
                        mainCircuitPumpTimeRangeUntil,
                        mainCircuitPumpSwitchOnTemp);

            }
        } else {
            logger.info("MainCircuitPump is NOT in Automatic Mode -> State is: Manually:{}", getManualPumpState());
        }
    }
}
