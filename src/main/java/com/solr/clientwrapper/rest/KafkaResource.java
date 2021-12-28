package com.solr.clientwrapper.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.solr.clientwrapper.domain.service.KafkaSender;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/kafka")
public class KafkaResource {

	private final Logger log = LoggerFactory.getLogger(KafkaResource.class);

	@Autowired
	private KafkaSender sender;

	@PostMapping("/add/{addtoQueue}")
	@Operation(summary = "/Add-To-Queue", security = @SecurityRequirement(name = "bearerAuth"))
	public ResponseEntity<String> sendData(@RequestBody String payload) {

		log.debug("Add To Queue");

		String responseString = sender.addToQueue(payload);

		if (!responseString.isEmpty()) {
			return ResponseEntity.status(HttpStatus.OK).body(responseString);
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseString);
		}

	}
	



}
