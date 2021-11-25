package com.solr.clientwrapper.infrastructure.solrbean;

import java.util.List;

import org.apache.solr.client.solrj.beans.Field;
import org.springframework.stereotype.Component;

@Component
public class SolrCollectionIndex {
	
	private String id;
	private String name;
	private List<String> features;
	private List<String> category;

	public String getId() {
		return id;
	}

	@Field
	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	@Field
	public void setName(String name) {
		this.name = name;
	}

	public List<String> getFeatures() {
		return features;
	}

	@Field
	public void setFeatures(List<String> features) {
		this.features = features;
	}

	public List<String> getCat() {
		return category;
	}

	@Field
	public void setCat(List<String> category) {
		this.category = category;
	}

}
