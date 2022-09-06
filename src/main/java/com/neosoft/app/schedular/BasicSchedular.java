package com.neosoft.app.schedular;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.neosoft.app.domain.port.api.PublicKeyServicePort;

@Component
public class BasicSchedular {

	@Autowired
	PublicKeyServicePort publicKeyServicePort;

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Scheduled(fixedRateString = "${schedular-durations.public-key-update}")
	public void checkPublicKeyUpdation() {
		logger.debug("Check for Public Key Updation in Cache Started");
		publicKeyServicePort.checkIfPublicKeyExistsInCache();
	}

}
