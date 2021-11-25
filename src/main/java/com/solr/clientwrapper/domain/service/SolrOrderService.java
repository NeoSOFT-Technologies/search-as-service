package com.solr.clientwrapper.domain.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.solr.clientwrapper.domain.port.api.SolrOrderServicePort;
import com.solr.clientwrapper.domain.port.spi.SolrOrderPersistencePort;
import com.solr.clientwrapper.infrastructure.entity.SolrOrderPojo;

@Service
@Transactional
public class SolrOrderService implements SolrOrderServicePort {

	private final SolrOrderPersistencePort solrOrderPersistencePort;

	public SolrOrderService(SolrOrderPersistencePort solrOrderPersistencePort) {
		this.solrOrderPersistencePort = solrOrderPersistencePort;
	}

	@Override
	public Page<SolrOrderPojo> findByOrderDescription(String searchTerm, Pageable pageable) {
		return solrOrderPersistencePort.findByOrderDescription(searchTerm, pageable);
	}

	@Override
	public Page<SolrOrderPojo> findByCustomerQuery(String searchTerm, Pageable pageable) {
		return solrOrderPersistencePort.findByCustomerQuery(searchTerm, pageable);
	}

	@Override
	public List<SolrOrderPojo> findByCustomerQuery2(String searchTerm, Pageable pageable) {
		return solrOrderPersistencePort.findByCustomerQuery2(searchTerm, pageable);
	}
	
}
