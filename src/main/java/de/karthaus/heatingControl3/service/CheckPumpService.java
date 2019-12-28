package de.karthaus.heatingControl3.service;

import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.karthaus.heatingControl3.model.HeatingControlContext;
import de.karthaus.heatingControl3.producer.PumpMessageProducer;
import io.micronaut.context.annotation.Value;
import io.micronaut.scheduling.annotation.Scheduled;

@Singleton
public class CheckPumpService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Value("${hc3.pump.heating.switch-on-temp}")
	protected int switchTemp;

	private HeatingControlContext heatingControlContext;

	private boolean pumpOn = false;

	/**
	 * 
	 * @param heatingControlContext
	 */
	public CheckPumpService(HeatingControlContext heatingControlContext) {
		this.heatingControlContext = heatingControlContext;
	}

	public boolean checkCanShutdown() {
		boolean result = false;
		//TODO : implement this
		return result;
	}

	/**
	 * 
	 */
	@Scheduled(fixedDelay = "30s", initialDelay = "5s")
	public void checkPump() {
		logger.debug("Check Pump condition Temp : {} > {} ", heatingControlContext.getTemp_combustionChamber(), switchTemp);

		// Condition I switch Pump ON
		if (heatingControlContext.getTemp_combustionChamber() > switchTemp && pumpOn == false) {
			pumpOn = true;
			logger.info("Switch PUMP -> ON");

		}

		// ConditionII switch Pump OFF
		if (heatingControlContext.getTemp_combustionChamber() < switchTemp && pumpOn == true) {
			pumpOn = false;
			logger.info("Switch PUMP -> OFF");
		}

	}

}
