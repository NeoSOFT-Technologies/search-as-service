package com.searchservice.app.domain.port.api;

import org.springframework.stereotype.Component;

@Component
public interface CollectionDeleteServicePort {
	public  boolean insertCollectionDeleteRecord(int clientId,String tableName);
	public int checkDeletionofCollection();
	public boolean undoCollectionDeleteRecord(int clientId);
}
