package com.solr.clientwrapper.domain.service;


import com.solr.clientwrapper.config.RabbitMQConfiguration;
import com.solr.clientwrapper.domain.port.api.RabbitMQSenderServicePort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQSenderService implements RabbitMQSenderServicePort {

	  @Autowired
     RabbitTemplate rabbitTemplate;
	
		private String message = null;

		private static final Logger LOGGER = LoggerFactory.getLogger(RabbitMQSenderService.class);
		
	@Override
	public void Sender(String payload) { 
		
		LOGGER.info("Start Sending the Message ");
		
	rabbitTemplate.convertAndSend(RabbitMQConfiguration.EXCHANGES, RabbitMQConfiguration.ROUTING_KEY,payload);
		 this.message=payload;
	
		
	}
	//For testing
	public String message() {
		return this.message;
	}


}
