package com.searchservice.app.domain.utils;

import java.io.IOException;

import org.apache.solr.client.solrj.impl.HttpSolrClient;

import lombok.extern.slf4j.Slf4j;


@Slf4j
public class SearchUtil {
	
	private SearchUtil() {}
	
	public static void closeSearchClientConnection(HttpSolrClient searchClient) {
		try {
			searchClient.close();
		} catch (IOException e) {
			log.debug(e.getMessage());
		}
	}
}
