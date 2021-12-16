package com.solr.clientwrapper.domain.port.api;

public interface DataIngectionServicePort {
	String parseSolrSchemaArray(String jsonString);
	String parseSolrSchemaBtch(String jsonString);
}
