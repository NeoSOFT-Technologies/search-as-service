package com.solr.clientwrapper.domain.service;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.solr.clientwrapper.domain.port.api.DataIngectionServicePort;

@Service
@Transactional
public class DataIngectionService implements DataIngectionServicePort {

	private final Logger log = LoggerFactory.getLogger(DataIngectionService.class);

	@Value("${base-solr-url}")
	private String baseSolrUrl;

	@Override
	public String parseSolrSchemaArray(String collectionName, String jsonString) {
		SolrClient solrClient = new HttpSolrClient.Builder(baseSolrUrl + "/" + collectionName).build();

		JSONObject jsonObject = new JSONObject(jsonString);
		log.debug("json Object :-" + jsonObject);
		JSONArray jArray = new JSONArray();

		for (@SuppressWarnings("rawtypes")
		Iterator iterator = jsonObject.keySet().iterator(); iterator.hasNext();) {
			String key = (String) iterator.next();
			jArray = (JSONArray) jsonObject.get(key);
		}
		log.debug("input array :-" + jArray.toString());

		return jArray.toString();
	}

	@Override
	public String parseSolrSchemaBatch(String collectionName, String jsonString) {
		SolrClient solrClient = new HttpSolrClient.Builder(baseSolrUrl + "/" + collectionName).build();
		JSONObject jsonObject = new JSONObject(jsonString);

		JSONArray batchObj = (JSONArray) jsonObject.get("batch");
		JSONObject jsonObject2 = batchObj.getJSONObject(0);
		Iterator keys = (Iterator) jsonObject2.keySet().iterator();
		ArrayList al = new ArrayList();

		while (keys.hasNext()) {
			al.add(keys.next());
		}
		JSONArray jArray = new JSONArray();

		for (int i = 0; i < al.size(); i++) {
			String object = (String) al.get(i);
			jArray.put((JSONArray) jsonObject2.getJSONArray(object));
		}
		log.debug("json Object :" + jArray.toString());

		return jArray.toString();
	}

}
