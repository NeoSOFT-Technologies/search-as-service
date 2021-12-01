package com.solr.clientwrapper.domain.service;

public class SolrSchemaRunner1 {
	public static void main(String[] args) {
		SolrSchemaService solrSchemaStorageService = new SolrSchemaService();
		
		String valResult = solrSchemaStorageService.validateSchema();
//		String storeResult = solrSchemaStorageService.storeSchema();
		
		System.out.println("validation @@@@@ : "+valResult);
//		System.out.println("storage %%%%% : "+storeResult);
	}
}
