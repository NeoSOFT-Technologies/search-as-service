package com.solr.clientwrapper.domain.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import com.solr.clientwrapper.config.RabbitMQConfiguration;
import com.solr.clientwrapper.domain.port.api.RabbitMQServicePort;

@Service
public class RabbitMessageListener implements RabbitMQServicePort {

	private static final Logger LOGGER = LoggerFactory.getLogger(RabbitMessageListener.class);

	@Override
	  @RabbitListener(queues = RabbitMQConfiguration.QUEUE)
	public void listener(String payloads) {
		LOGGER.info("MessageListener"+payloads);
	}
}
