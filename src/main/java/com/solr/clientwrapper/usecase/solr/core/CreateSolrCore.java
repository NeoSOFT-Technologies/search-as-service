package com.solr.clientwrapper.usecase.solr.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.solr.clientwrapper.domain.dto.solr.SolrResponseDTO;
import com.solr.clientwrapper.domain.port.api.SolrCoreServicePort;

@Service
@Transactional
public class CreateSolrCore {

	private final Logger log = LoggerFactory.getLogger(CreateSolrCore.class);

	private final SolrCoreServicePort solrCoreServicePort;

	public CreateSolrCore(SolrCoreServicePort solrCoreServicePort) {
		this.solrCoreServicePort = solrCoreServicePort;
	}

	public SolrResponseDTO create(String coreName) {
		log.debug("create");
		return solrCoreServicePort.create(coreName);
	}

}
