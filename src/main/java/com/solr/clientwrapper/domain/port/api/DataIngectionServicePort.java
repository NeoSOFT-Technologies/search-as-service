package com.solr.clientwrapper.domain.port.api;

public interface DataIngectionServicePort {	
	String parseSolrSchemaArray(String collectionName, String jsonString);
	String parseSolrSchemaBatch(String collectionName, String jsonString);
}
