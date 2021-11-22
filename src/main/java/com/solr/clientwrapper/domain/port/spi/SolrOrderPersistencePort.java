package com.solr.clientwrapper.domain.port.spi;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.solr.clientwrapper.infrastructure.entity.SolrOrderPojo;

public interface SolrOrderPersistencePort {
	Page<SolrOrderPojo> findByOrderDescription(String searchTerm, Pageable pageable);

	Page<SolrOrderPojo> findByCustomerQuery(String searchTerm, Pageable pageable);
	
	List<SolrOrderPojo> findByCustomerQuery2(String searchTerm, Pageable pageable);

}
