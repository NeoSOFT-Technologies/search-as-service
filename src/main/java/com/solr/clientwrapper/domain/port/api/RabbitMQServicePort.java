package com.solr.clientwrapper.domain.port.api;

public interface RabbitMQServicePort {
	public void listener(String  payloads);
	
}
