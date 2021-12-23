package com.solr.clientwrapper.domain.port.api;

public interface DataInjectionServicePort {
	String parseSolrSchemaArray(String jsonString);
	String parseSolrSchemaBtch(String jsonString);
}
