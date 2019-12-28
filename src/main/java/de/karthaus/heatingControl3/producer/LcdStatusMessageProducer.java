package de.karthaus.heatingControl3.producer;

import io.micronaut.configuration.rabbitmq.annotation.Binding;
import io.micronaut.configuration.rabbitmq.annotation.RabbitClient;

@RabbitClient("hc-lcd-display")
public interface LcdStatusMessageProducer {

	@Binding("hc-lcd-display")
	void send(byte[] data);

}
