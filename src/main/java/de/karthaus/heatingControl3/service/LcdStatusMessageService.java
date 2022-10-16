package de.karthaus.heatingControl3.service;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.karthaus.heatingControl3.model.HeatingControlContext;
import de.karthaus.heatingControl3.model.PumpState;
import de.karthaus.heatingControl3.producer.LcdStatusMessageProducer;
import io.micronaut.scheduling.annotation.Scheduled;

// 012345678901234567890
// .....AUSSCHALTEN.....
// HC3->25.11 22:32:12
// Ofen:35 Buffer:42
// Garage:17
// Ofen:OFF Buffer:OFF
@Singleton
public class LcdStatusMessageService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private LcdStatusMessageProducer lcdStatusMessageProducer;

    private HeatingControlContext heatingControlContext;

    private PumpState pumpState;

    private DateTimeFormatter formatter;

    /**
     * @param lcdStatusMessageProducer
     * @param heatingControlContext
     */
    public LcdStatusMessageService(
            LcdStatusMessageProducer lcdStatusMessageProducer,
            HeatingControlContext heatingControlContext,
            PumpState pumpState) {
        this.lcdStatusMessageProducer = lcdStatusMessageProducer;
        this.heatingControlContext = heatingControlContext;
        this.pumpState = pumpState;

        formatter = DateTimeFormatter.ISO_LOCAL_TIME;
    }

    @Scheduled(fixedDelay = "3s")
    public void PublishToLcd() {
        String text;
        // -- Check if Shutdown requestet...
        if (heatingControlContext.isShutdownRequestet()
                && !pumpState.isPumpGarage()
                && !pumpState.isPumpHeating()) {
            text = "1=     HC3";
            lcdStatusMessageProducer.send(text.getBytes());
            text = "2=.....AUSSCHALTEN.....";
            lcdStatusMessageProducer.send(text.getBytes());
            logger.info("Send Shutdown Message to LCD...");
            return;
        }
        // -- Build Status Messages...
        Instant instant = Instant.now();
        ZoneId zoneId = ZoneId.of("Europe/Berlin");
        ZonedDateTime zdt = ZonedDateTime.ofInstant(instant, zoneId);
        text = "1=HC3->" + zdt.truncatedTo(ChronoUnit.SECONDS).format(formatter);
        logger.info("Sending {} to lcd queue " + text);
        lcdStatusMessageProducer.send(text.getBytes());
        // --
        text = "2="
                + "Of:"
                + String.format("%.1f", heatingControlContext.getTemp_combustionChamber())
                + " Pu:"
                + String.format("%.1f", heatingControlContext.getBufferTemperature());
        logger.info("Sending {} to lcd queue " + text);
        lcdStatusMessageProducer.send(text.getBytes());
        // --
        text = "3=Garage:" + String.format("%.1f", heatingControlContext.getGarageTemperature());
        logger.info("Sending {} to lcd queue " + text);
        lcdStatusMessageProducer.send(text.getBytes());
        // --
        text = "4=Of:";
        if (pumpState.isPumpHeating()) {
            text += "+";
        } else {
            text += "-";
        }
        text += " Pu:";
        if (pumpState.isPumpGarage()) {
            text += "+";
        } else {
            text += "-";
        }
        text += " Hau:";
        if (pumpState.isPumpMainCircuit()) {
            text += "+";
        } else {
            text += "-";
        }
        logger.info("Sending {} to lcd queue " + text);
        lcdStatusMessageProducer.send(text.getBytes());
    }

}
