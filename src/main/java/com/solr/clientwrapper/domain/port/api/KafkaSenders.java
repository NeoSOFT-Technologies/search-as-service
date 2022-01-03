package com.solr.clientwrapper.domain.port.api;

public interface KafkaSenders {
	
	public String addToQueue(String payload);

}
