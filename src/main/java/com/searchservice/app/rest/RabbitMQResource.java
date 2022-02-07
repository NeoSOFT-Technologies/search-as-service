package com.searchservice.app.rest;

import com.searchservice.app.domain.dto.ResponseDTO;
import com.searchservice.app.domain.dto.ResponseMessages;
import com.searchservice.app.domain.service.RabbitMQSenderService;
import com.searchservice.app.rest.errors.BadRequestOccurredException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${base-url.api-endpoint.rabbit-mq}")
public class RabbitMQResource {
	private final Logger log = LoggerFactory.getLogger(VersionedRabbitMQResource.class);
	
	private final RabbitMQSenderService rabbitMQSenderService;
	public RabbitMQResource(RabbitMQSenderService rabbitMQSenderService) {
		this.rabbitMQSenderService = rabbitMQSenderService;
	}

	@PostMapping("/mq/{rabbit-mq}")
	@Operation(summary = "/Add-To-RabbitMQ", security = @SecurityRequirement(name = "bearerAuth"))
	public ResponseEntity<ResponseDTO> sendData(@RequestBody String payload) {

		log.debug("Add_To_RabbitMQ");

		rabbitMQSenderService.Sender(payload);
		if (!payload.isEmpty()) {
			
			return ResponseEntity.status(HttpStatus.OK).body(new ResponseDTO(
					200, "Payload sent successfully"));
		} else
			throw new BadRequestOccurredException(400, ResponseMessages.BAD_REQUEST_MSG);
	}
}