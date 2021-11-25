package com.solr.clientwrapper.infrastructure.adaptor;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.solr.clientwrapper.domain.port.spi.SolrOrderPersistencePort;
import com.solr.clientwrapper.infrastructure.entity.SolrOrderPojo;
import com.solr.clientwrapper.infrastructure.repository.SolrOrderRepository;

@Service
@Transactional
public class SolrOrderJPAAdapter implements SolrOrderPersistencePort {
	@Autowired
	private final SolrOrderRepository solrOrderRepository;

	public SolrOrderJPAAdapter(SolrOrderRepository solrOrderRepository) {
		this.solrOrderRepository = solrOrderRepository;
	}

	@Override
	public Page<SolrOrderPojo> findOrdersByOrderDescription(String searchTerm, Pageable pageable) {
		return solrOrderRepository.findByOrderDescription(searchTerm, pageable);
	}

	@Override
	public Page<SolrOrderPojo> findOrdersBySearchTerm(String searchTerm, Pageable pageable) {
		return solrOrderRepository.findByCustomerQuery(searchTerm, pageable);
	}

	@Override
	public List<SolrOrderPojo> findListOfOrdersBySearchTerm(String searchTerm, Pageable pageable) {
		return solrOrderRepository.findByCustomerQuery2(searchTerm, pageable);
	}

}
