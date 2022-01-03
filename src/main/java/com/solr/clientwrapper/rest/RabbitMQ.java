package com.solr.clientwrapper.rest;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.solr.clientwrapper.config.RabbitMQConfiguration;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/RabbitMQ")
public class RabbitMQ {

	private final Logger log = LoggerFactory.getLogger(RabbitMQ.class);

	@Autowired
	private RabbitTemplate rabbitTemplate;


	@PostMapping("/MQ/{RabbitMQ}")
	@Operation(summary = "/Add-To-RabbitMQ", security = @SecurityRequirement(name = "bearerAuth"))
	public ResponseEntity<String> sendData(@RequestBody String payloads) {

		log.debug("Add To RabbitMQ");
	
		rabbitTemplate.convertAndSend(RabbitMQConfiguration.EXCHANGE, RabbitMQConfiguration.ROUTING_KEY, payloads);
		 
		return null;
	

//		if (!responseString.isEmpty()) {
//			return ResponseEntity.status(HttpStatus.OK).body(responseString);
//		} else {
//			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseString);
		}

	}