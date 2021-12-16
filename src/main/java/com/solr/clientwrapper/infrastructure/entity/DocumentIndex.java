package com.solr.clientwrapper.infrastructure.entity;



import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.annotation.Id;
import org.springframework.data.solr.core.mapping.SolrDocument;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SolrDocument(collection = "user")
@JsonIgnoreProperties(ignoreUnknown = true)
public class DocumentIndex {
	@Id
	@Field
	private Integer id;
	
	@Field
	private String name;

	@Field
	private String author;


	
}
