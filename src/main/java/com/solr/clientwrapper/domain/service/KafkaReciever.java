package com.solr.clientwrapper.domain.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.solr.clientwrapper.domain.port.api.KafkaRecievers;



@Service
public class KafkaReciever implements KafkaRecievers {

	private String message = null;
	private static final Logger LOGGER = LoggerFactory.getLogger(KafkaReciever.class);

	@KafkaListener(topics = "Topic_Name", groupId = "group_id")
	@Override
	public void recieveData(String payload) {
		LOGGER.info("Data - " + payload.toString() + " recieved");
		this.message = payload;

	}

	public String message() {
		return this.message;
	}

}