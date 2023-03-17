package de.karthaus.heatingControl3.service;

import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.WriteApi;
import com.influxdb.client.domain.WritePrecision;
import de.karthaus.heatingControl3.model.HeatingControlContext;
import io.micronaut.context.annotation.Value;
import io.micronaut.scheduling.annotation.Scheduled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;
import java.time.Instant;

@Singleton
public class InfluxDbService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${hc3.influxdb.url}")
    protected String influxDbUrl;

    @Value("${hc3.influxdb.username}")
    protected String influxDbUsername;

    @Value("${hc3.influxdb.password}")
    protected String influxDbPassword;

    private HeatingControlContext heatingControlContext;
    private de.karthaus.heatingControl3.model.PumpState pumpState;

    public InfluxDbService(HeatingControlContext heatingControlContext, de.karthaus.heatingControl3.model.PumpState pumpState) {
        this.heatingControlContext = heatingControlContext;
        this.pumpState = pumpState;
    }

    InfluxDBClient influxDBClient;

    WriteApi writeApi;


    @PostConstruct
    private void init() {
        logger.info("Try to connect to InfluxDB at:{}", influxDbUrl);
        influxDBClient = InfluxDBClientFactory.createV1(
                influxDbUrl,
                influxDbUsername,
                influxDbPassword.toCharArray(),
                "hc3",
                "autogen"
        );
        writeApi = influxDBClient.makeWriteApi();
    }


    @Scheduled(fixedDelay = "1m")
    public void persistContext() {
        if (!influxDBClient.ping()) {
            logger.warn("influxdb is NOT Connected - try to connect");
            init();
            return;
        }
        InfluxTemperature temperature = new InfluxTemperature();
        temperature.location = "bufferTemp";
        temperature.value = heatingControlContext.getBufferTemperature();
        temperature.time = Instant.now();
        writeApi.writeMeasurement(WritePrecision.NS, temperature);
        //-
        temperature = new InfluxTemperature();
        temperature.location = "garageTemp";
        temperature.value = heatingControlContext.getGarageTemperature();
        temperature.time = Instant.now();
        writeApi.writeMeasurement(WritePrecision.NS, temperature);
        //-
        temperature = new InfluxTemperature();
        temperature.location = "combustionChamberTemp";
        temperature.value = heatingControlContext.getTemp_combustionChamber();
        temperature.time = Instant.now();
        writeApi.writeMeasurement(WritePrecision.NS, temperature);
        //-
        temperature = new InfluxTemperature();
        temperature.location = "outdoorTemp";
        temperature.value = heatingControlContext.getOutdoorTemperature();
        temperature.time = Instant.now();
        writeApi.writeMeasurement(WritePrecision.NS, temperature);
        // --------------
        InfluxPumpState influxPumpState = new InfluxPumpState();
        if (pumpState.isPumpHeating()) {
            influxPumpState.state = 1;
        } else {
            influxPumpState.state = 0;
        }
        influxPumpState.location = "heating";
        influxPumpState.time = Instant.now();
        writeApi.writeMeasurement(WritePrecision.NS, influxPumpState);
        // -
        influxPumpState = new InfluxPumpState();
        if (pumpState.isPumpGarage()) {
            influxPumpState.state = 1;
        } else {
            influxPumpState.state = 0;
        }
        influxPumpState.location = "garage";
        influxPumpState.time = Instant.now();
        writeApi.writeMeasurement(WritePrecision.NS, influxPumpState);
        // --------------
        logger.info("Write Measurements pumpState and temperature.");
    }


    @Measurement(name = "temperature")
    private static class InfluxTemperature {
        @Column(tag = true)
        String location;
        @Column
        Double value;
        @Column(timestamp = true)
        Instant time;
    }

    @Measurement(name = "pumpState")
    private static class InfluxPumpState {
        @Column(tag = true)
        String location;
        @Column
        int state;

        @Column(timestamp = true)
        Instant time;
    }


}
