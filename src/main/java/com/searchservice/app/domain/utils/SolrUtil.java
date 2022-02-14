package com.searchservice.app.domain.utils;

import java.io.IOException;

import org.apache.solr.client.solrj.impl.HttpSolrClient;

import lombok.extern.slf4j.Slf4j;


@Slf4j
public class SolrUtil {
	
	private SolrUtil() {}
	
	public static void closeSolrClientConnection(HttpSolrClient solrClient) {
		try {
			solrClient.close();
		} catch (IOException e) {
			log.debug(e.getMessage());
		}
	}
}
