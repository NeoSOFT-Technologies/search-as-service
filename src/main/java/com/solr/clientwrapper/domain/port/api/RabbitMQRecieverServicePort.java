package com.solr.clientwrapper.domain.port.api;

public interface RabbitMQRecieverServicePort {
	public void listener(String  payloads);
	
}
