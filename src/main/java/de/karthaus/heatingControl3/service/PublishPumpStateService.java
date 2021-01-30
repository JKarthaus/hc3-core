package de.karthaus.heatingControl3.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.karthaus.heatingControl3.model.HeatingControlContext;
import de.karthaus.heatingControl3.model.PumpState;
import de.karthaus.heatingControl3.producer.PumpMessageProducer;
import io.micronaut.context.annotation.Value;
import io.micronaut.scheduling.annotation.Scheduled;

public class PublishPumpStateService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private PumpMessageProducer pumpMessageProducer;

	@Value("${hc3.pump.garage.relais-id}")
	protected Integer garagePumpGpioPin;

	@Value("${hc3.pump.heating.relais-id}")
	protected Integer heatingPumpGpioPin;

	private PumpState localPumpState;

	private PumpState pumpState;

	private HeatingControlContext heatingControlContext;

	/**
	 *
	 * This class holds a local PumpState Object and compares this to
	 * the PumpState object from the Global heatingControlConext.
	 * If the Pump States differ then the new State send out to Message Bus via
	 * the Messageproducer
	 * @param pumpMessageProducer
	 * @param pumpState
	 * @param heatingControlContext
	 */
	public PublishPumpStateService(
			PumpMessageProducer pumpMessageProducer,
			PumpState pumpState,
			HeatingControlContext heatingControlContext) {
		this.pumpMessageProducer = pumpMessageProducer;
		this.heatingControlContext = heatingControlContext;
		this.pumpState = pumpState;
	}

	/**
	 * 
	 */
	@Scheduled(fixedDelay = "5s", initialDelay = "5s")
	public void checkState() {
		// check if shutdown is requestet...
		if (heatingControlContext.isShutdownRequestet()) {
			sendMessage(garagePumpGpioPin, false);
			sendMessage(heatingPumpGpioPin, false);
			logger.info("Shutdown requestet -> Switch all Pumps off");
			return;
		}
		// --
		boolean stateChanged = false;
		if (localPumpState == null) {
			localPumpState = new PumpState();
			logger.info("Send initial Pump State out of Message Bus");
			localPumpState.setPumpGarage(pumpState.isPumpGarage());
			localPumpState.setPumpHeating(pumpState.isPumpHeating());
			sendMessage(garagePumpGpioPin, localPumpState.isPumpGarage());
			sendMessage(heatingPumpGpioPin, localPumpState.isPumpHeating());
		} else {
			if (localPumpState.isPumpGarage() != pumpState.isPumpGarage()) {
				logger.info("State for Pump Garage changed from {} to {} ", localPumpState.isPumpGarage(), pumpState.isPumpGarage());
				localPumpState.setPumpGarage(pumpState.isPumpGarage());
				sendMessage(garagePumpGpioPin, localPumpState.isPumpGarage());
				logger.info("Change State of Garage Pump to {}", pumpState.isPumpGarage());
				stateChanged = true;
			}
			if (localPumpState.isPumpHeating() != pumpState.isPumpHeating()) {
				logger.info("State for Pump Heating changed from {} to {} ", localPumpState.isPumpHeating(), pumpState.isPumpHeating());
				localPumpState.setPumpHeating(pumpState.isPumpHeating());
				sendMessage(heatingPumpGpioPin, localPumpState.isPumpHeating());
				logger.info("Change State of Heating Pump to {}", pumpState.isPumpHeating());
				stateChanged = true;
			}
		}
		if (!stateChanged) {
			logger.debug("Nothing changed on Pump state...");
		}
	}

	/**
	 * 
	 * @param relaisId
	 * @param booleanState
	 */
	private void sendMessage(int relaisId, boolean booleanState) {
		String state = "OFF";
		if (booleanState) {
			state = "ON";
		}
		String data = "" + relaisId + "=" + state;
		pumpMessageProducer.send(data.getBytes());
	}

}
