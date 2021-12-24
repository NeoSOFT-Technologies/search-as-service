package com.solr.clientwrapper.domain.port.api;

public interface kafkaSender {
	
	public String AddToQueue(String payload);

}
