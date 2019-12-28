package de.karthaus.heatingControl3.controller;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.JSchException;

import de.karthaus.heatingControl3.model.HeatingControlContext;
import de.karthaus.heatingControl3.model.PumpState;
import de.karthaus.heatingControl3.service.HostShutdownService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;

@Controller("/hc3-core")
public class RestController {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private HeatingControlContext heatingControlContext;
	private PumpState pumpState;
	private HostShutdownService hostShutdownService;

	public RestController(
			HeatingControlContext heatingControlContext,
			PumpState pumpState,
			HostShutdownService hostShutdownService) {
		this.heatingControlContext = heatingControlContext;
		this.pumpState = pumpState;
		this.hostShutdownService = hostShutdownService;
	}

	@Get(value = "/state", produces = MediaType.APPLICATION_JSON)
	StateDTO getState() {
		logger.debug("deliver heatingControl3 State");
		return getDtoFromContext();
	}

	@Get(value = "/shutdown", produces = MediaType.TEXT_PLAIN)
	HttpResponse shutdown() {
		logger.info("Shutdown Service called...");
		heatingControlContext.setShutdownRequestet(true);
		try {
			hostShutdownService.startShutdownProcess();
		} catch (JSchException | IOException e) {
			logger.error(e.getMessage());
			return HttpResponse.serverError(e.getMessage());
		}
		return HttpResponse.ok();
	}

	/**
	 * 
	 * @return
	 */
	private StateDTO getDtoFromContext() {
		StateDTO result = new StateDTO();
		result.setReturnTemperature(heatingControlContext.getReturnTemperature());
		result.setTemp_combustionChamber(heatingControlContext.getTemp_combustionChamber());
		result.setGarageTemperature(heatingControlContext.getGarageTemperature());
		result.setBufferTemperature(heatingControlContext.getBufferTemperature());
		result.setFlowPumpON(pumpState.isPumpHeating());
		result.setGaragePumpON(pumpState.isPumpGarage());
		return result;
	}

	protected class StateDTO {
		private double temp_combustionChamber;
		private double flowTemperature;
		private double returnTemperature;

		private double bufferTemperature;
		private double garageTemperature;

		private boolean flowPumpON;
		private boolean garagePumpON;

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

		public boolean isFlowPumpON() {
			return flowPumpON;
		}

		public void setFlowPumpON(boolean flowPumpON) {
			this.flowPumpON = flowPumpON;
		}

		public boolean isGaragePumpON() {
			return garagePumpON;
		}

		public void setGaragePumpON(boolean garagePumpON) {
			this.garagePumpON = garagePumpON;
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
	}

}
