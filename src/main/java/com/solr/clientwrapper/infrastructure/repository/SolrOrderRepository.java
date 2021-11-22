package com.solr.clientwrapper.infrastructure.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.solr.repository.Query;
import org.springframework.data.solr.repository.SolrCrudRepository;

import com.solr.clientwrapper.infrastructure.entity.SolrOrderPojo;

public interface SolrOrderRepository extends SolrCrudRepository<SolrOrderPojo, Long> {
	SolrOrderPojo findByOrderid(Long orderid);

	@Query("odesc:*?0*")
	Page<SolrOrderPojo> findByOrderDescription(String searchTerm, Pageable pageable);

	@Query("oname:*?0* OR odesc:*?0* OR pname:*?0* OR cname:*?0*")
	Page<SolrOrderPojo> findByCustomerQuery(String searchTerm, Pageable pageable);
	
	@Query("oname:*?0* OR odesc:*?0* OR pname:*?0* OR cname:*?0*")
	List<SolrOrderPojo> findByCustomerQuery2(String searchTerm, Pageable pageable);
	
}
