package com.solr.clientwrapper.domain.port.api;

public interface KafkaRecievers {
	
	public void recieveData(String payload);
}
