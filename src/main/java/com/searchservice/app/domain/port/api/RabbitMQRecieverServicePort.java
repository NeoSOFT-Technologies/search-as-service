package com.searchservice.app.domain.port.api;

public interface RabbitMQRecieverServicePort {
	public void listener(String  payloads);
	
}
