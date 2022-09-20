package de.karthaus.heatingControl3.service;

import io.micronaut.scheduling.annotation.Scheduled;

import javax.inject.Singleton;

@Singleton
public class InfluxDbService {

    @Scheduled(fixedDelay = "1m")
    public void persistContext() {
        // TODO : implement me
    }




}
