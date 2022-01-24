package com.searchservice.app.domain.port.api;

public interface RabbitMQSenderServicePort {
	
	public void Sender(String payload);

}
