package com.searchservice.app.schedular;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.searchservice.app.domain.port.api.ManageTableServicePort;
import com.searchservice.app.domain.port.api.PublicKeyServicePort;
import com.searchservice.app.domain.port.api.TableDeleteServicePort;

@Component
public class SearchServiceSchedular {

	@Autowired
	TableDeleteServicePort tableDeleteService;
	
	@Autowired
    ManageTableServicePort manageTableService;
    
    @Autowired
	PublicKeyServicePort publicKeyServicePort;
    
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Scheduled(cron = "${schedular-durations.table-deletion}")
	public void checkForTableDeletion() {
		logger.debug("Check For Table Deletion Operation Started");
		tableDeleteService.checkDeletionofTable();
	}

	@Scheduled(cron = "${schedular-durations.column-deletion}")
	public void checkForSchemaDeletion() {
		logger.debug("Check For Table Schema Deletion Operation Started");
		manageTableService.checkForSchemaDeletion();
	}
	
	@Scheduled(fixedRateString = "${schedular-durations.public-key-update}")
	public void checkPublicKeyUpdation() {
		logger.debug("Check for Public Key Updation in Cache Started");
		publicKeyServicePort.checkIfPublicKeyExistsInCache();
	}

}
