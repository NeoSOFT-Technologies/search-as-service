package com.searchservice.app.domain.utils;

import java.io.IOException;

import org.apache.solr.client.solrj.impl.HttpSolrClient;


public class SolrUtil {
	public static void closeSolrClientConnection(HttpSolrClient solrClient) {
		try {
			solrClient.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
