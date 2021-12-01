package com.solr.clientwrapper.domain.port.api;

public interface SolrSchemaStorageServicePort {
	String validateSchema();
	String storeSchema();
}
