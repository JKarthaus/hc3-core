package de.karthaus.heatingControl3.producer;

import io.micronaut.configuration.rabbitmq.annotation.Binding;
import io.micronaut.configuration.rabbitmq.annotation.RabbitClient;

@RabbitClient("${hc3.relais-exchange.name}")
public interface PumpMessageProducer {

	@Binding("hc-relais-hat")
	void send(byte[] data);

}
