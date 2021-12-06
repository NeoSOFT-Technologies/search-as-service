package com.solr.clientwrapper.domain.service;

import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.solr.clientwrapper.domain.dto.solr.SolrFieldDTO;
import com.solr.clientwrapper.domain.dto.solr.SolrSchemaDTO;
import com.solr.clientwrapper.domain.port.api.SolrSchemaServicePort;

@Service
@Transactional
public class SolrSchemaService implements SolrSchemaServicePort {

	private final Logger log = LoggerFactory.getLogger(SolrSchemaService.class);

	HttpSolrClient solrClient = new HttpSolrClient.Builder("http://localhost:8983/solr").build();

	@Override
	public SolrSchemaDTO create(String tableName, String name, SolrFieldDTO[] attributes) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SolrSchemaDTO update(String tableName, String name, SolrSchemaDTO solrSchemaDTO) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SolrSchemaDTO delete(String tableName, String name) {
		// TODO Auto-generated method stub
		return null;
		}

	@Override
	public SolrSchemaDTO get(String tableName, String name) {
		// TODO Auto-generated method stub
		return null;
	}

}
