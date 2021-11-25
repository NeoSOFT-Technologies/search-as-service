package com.solr.clientwrapper.domain.port.api;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.solr.clientwrapper.infrastructure.entity.SolrOrderPojo;

public interface SolrOrderServicePort {

	Page<SolrOrderPojo> findOrdersByOrderDescription(String searchTerm, Pageable pageable);

	Page<SolrOrderPojo> findOrdersBySearchTerm(String searchTerm, Pageable pageable);
	
	List<SolrOrderPojo> findListOfOrdersBySearchTerm(String searchTerm, Pageable pageable);
	
}
