package com.searchservice.app.schedular;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.searchservice.app.domain.service.CollectionDeleteService;

@EnableScheduling
public class SearchServiceSchedular {

	@Autowired CollectionDeleteService collectionDeleteService; 
	
	@Scheduled(cron="0 15 8 * * ?")
	public void checkCollectionSoftDelete()
	{
		 collectionDeleteService.checkDeletionofCollection();
	}
}
