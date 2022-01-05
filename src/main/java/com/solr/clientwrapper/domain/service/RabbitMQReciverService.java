package com.solr.clientwrapper.domain.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import com.solr.clientwrapper.config.RabbitMQConfiguration;
import com.solr.clientwrapper.domain.port.api.RabbitMQRecieverServicePort;

@Service
public class RabbitMQReciverService implements RabbitMQRecieverServicePort {

	private static final Logger LOGGER = LoggerFactory.getLogger(RabbitMQReciverService.class);

	private String listener = null;
	
	@Override
	  @RabbitListener(queues = RabbitMQConfiguration.QUEUES)
	public void listener(String payload) {
		
		LOGGER.info("Start Recive the Message "+ payload);
		this.listener = payload;
		
	}
	
	public String listener() {
		return this.listener;
	}
}
