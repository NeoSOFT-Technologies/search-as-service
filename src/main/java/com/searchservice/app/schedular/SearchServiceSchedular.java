package com.searchservice.app.schedular;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.searchservice.app.domain.service.ManageTableService;
import com.searchservice.app.domain.service.TableDeleteService;

@EnableScheduling
public class SearchServiceSchedular {

	@Autowired
	TableDeleteService tableDeleteService;

	@Autowired
	ManageTableService manageTableService;

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Scheduled(cron = "0 15 8 * * ?")
	public void checkCollectionSoftDelete() {
		logger.debug("Check For Table Deletion Operation Started");
		tableDeleteService.checkDeletionofTable();
		manageTableService.checkForSchemaDeletion();
	}

	@Scheduled(cron = "0 15 8 * * ?")
	public void checkScheamaoftDelete() {
		logger.debug("Check For Table Schema Deletion Operation Started");
		manageTableService.checkForSchemaDeletion();
	}
}
