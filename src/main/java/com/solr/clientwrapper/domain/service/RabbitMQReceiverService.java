package com.solr.clientwrapper.domain.service;


import com.solr.clientwrapper.config.RabbitMQConfiguration;
import com.solr.clientwrapper.domain.port.api.RabbitMQRecieverServicePort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQReceiverService implements RabbitMQRecieverServicePort {

	private String message = null;

	private static final Logger LOGGER = LoggerFactory.getLogger(RabbitMQReceiverService.class);

	@Autowired
	RabbitTemplate rabbitTemplate;

	@Override
	@RabbitListener(queues = RabbitMQConfiguration.QUEUES)
	public void listener(String payload) {

		LOGGER.info("Start Recive the Message ");

		this.message = payload;

	}

	// For Test
	public String message() {

		return this.message;
	}
}
