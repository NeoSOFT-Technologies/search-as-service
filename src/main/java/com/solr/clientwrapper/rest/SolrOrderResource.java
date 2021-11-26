package com.solr.clientwrapper.rest;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.solr.clientwrapper.domain.port.api.SolrOrderServicePort;
import com.solr.clientwrapper.infrastructure.entity.SolrOrderPojo;
import com.solr.clientwrapper.infrastructure.repository.SolrOrderRepository;


@RestController
@RequestMapping("/order")
public class SolrOrderResource {

	private final Logger log = LoggerFactory.getLogger(SolrOrderResource.class);
	
	@Autowired
	SolrOrderRepository solrOrderRepository;
	
	private final SolrOrderServicePort solrOrderServicePort;

	public SolrOrderResource(SolrOrderRepository solrOrderRepository, SolrOrderServicePort solrOrderServicePort) {
		this.solrOrderRepository = solrOrderRepository;
		this.solrOrderServicePort = solrOrderServicePort;
	}
	
	/*
	 * /////// ############## Solr Search Operation for Order Collection ################ /////////
	 */
	@GetMapping("/search")
	public List<SolrOrderPojo> findOrdersBySearchTerm(
			@RequestParam(defaultValue = "*") String searchTerm, 
			@RequestParam(defaultValue = "0") int startPage, 
			@RequestParam(defaultValue = "3") int pageSize) {
		log.debug("REST request to search orders in the collection : {}", new SolrOrderPojo());
		return solrOrderServicePort.findOrdersBySearchTerm(searchTerm, PageRequest.of(startPage, pageSize))
									.getContent();
	}
	
	@GetMapping("/search/{orderDescription}/{startPage}/{pageSize}")
	public List<SolrOrderPojo> findOrdersByDescription(	@PathVariable String orderDescription, 
														@PathVariable int startPage, 
														@PathVariable int pageSize) {
		log.debug("REST request to search orders based on order description in the collection : {}", new SolrOrderPojo());
		return solrOrderServicePort.findOrdersByOrderDescription(
				orderDescription, 
				PageRequest.of(startPage, pageSize)).getContent();
	}
	
	/*
	 * CRUD operations for Order Collection
	 */	
	@PostMapping("/placeorder")
	public String createOrder(@RequestBody SolrOrderPojo order) {
		log.debug("REST request to place the order : {}", order);
		String description = "Order Placed successfully.";
		solrOrderRepository.save(order);
		return description;
	}

	@GetMapping("/get/{orderid}")
	public SolrOrderPojo readOrder(@PathVariable Long orderid) {
		log.debug("REST request to fetch the order with order id : {}", orderid);
		return solrOrderRepository.findByOrderid(orderid);
	}

	@PutMapping("/update")
	public String updateOrder(@RequestBody SolrOrderPojo order) {
		log.debug("REST request to update the order : {}", order);
		String description = "Order Updated successfully!";
		solrOrderRepository.save(order);
		return description;
	}

	@DeleteMapping("/delete/{orderid}")
	public String deleteOrder(@PathVariable Long orderid) {
		log.debug("REST request to delete the order with order id : {}", orderid);
		String description = "Order Deleted successfully!!";
		solrOrderRepository.delete(solrOrderRepository.findByOrderid(orderid));
		return description;
	}


	
}
