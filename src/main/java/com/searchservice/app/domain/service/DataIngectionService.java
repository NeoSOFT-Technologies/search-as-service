package com.searchservice.app.domain.service;


import com.searchservice.app.domain.port.api.DataIngectionServicePort;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Iterator;

@Service
@Transactional
public class DataIngectionService implements DataIngectionServicePort {

	private final Logger log = LoggerFactory.getLogger(DataIngectionService.class);

	@Value("${base-solr-url}")
	private String baseSolrUrl;

	@Override
	public String parseSolrSchemaArray(String collectionName, String jsonString) {
		
		JSONObject jsonObject = new JSONObject(jsonString);
		log.debug("json Object :-" + jsonObject);
		JSONArray jArray = new JSONArray();

		for (@SuppressWarnings("rawtypes")
		Iterator iterator = jsonObject.keySet().iterator(); iterator.hasNext();) {
			String key = (String) iterator.next();
			jArray = (JSONArray) jsonObject.get(key);
		}
		log.debug("input array :-" + jArray);

		return jArray.toString();
	}

	@Override
	public String parseSolrSchemaBatch(String collectionName, String jsonString) {
		
		JSONObject jsonObject = new JSONObject(jsonString);

		JSONArray batchObj = (JSONArray) jsonObject.get("batch");
		JSONObject jsonObject2 = batchObj.getJSONObject(0);
		Iterator<String> keys = jsonObject2.keySet().iterator();
		ArrayList<String> al = new ArrayList<>();

		while (keys.hasNext()) {
			al.add(keys.next());
		}
		JSONArray jArray = new JSONArray();

		for (int i = 0; i < al.size(); i++) {
			String object = al.get(i);
			jArray.put(jsonObject2.getJSONArray(object));
		}
		log.debug("json Object :" + jArray,jArray.toString());

		return jArray.toString();
	}

}
