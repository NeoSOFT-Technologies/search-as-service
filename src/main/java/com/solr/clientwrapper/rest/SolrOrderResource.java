package com.solr.clientwrapper.rest;

import java.util.List;

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

	@Autowired
	SolrOrderRepository solrOrderRepository;
	
	private final SolrOrderServicePort solrOrderServicePort;

	public SolrOrderResource(SolrOrderRepository solrOrderRepository, SolrOrderServicePort solrOrderServicePort) {
		this.solrOrderRepository = solrOrderRepository;
		this.solrOrderServicePort = solrOrderServicePort;
	}
	

	/*
	 * /////// ############## Solr Search Operation for Order Collection ################ /////////
	 * Author: Piyush Ojha; NeoSOFT Tech.
	 */
	@GetMapping("/search")
	public List<SolrOrderPojo> findDesiredOrders(
			@RequestParam(defaultValue = "*") String searchTerm, 
			@RequestParam(defaultValue = "0") int page, 
			@RequestParam(defaultValue = "3") int pagesize) {
		return solrOrderServicePort.findByCustomerQuery(searchTerm, PageRequest.of(page, pagesize)).getContent();
	}
	
	@GetMapping("/search/{searchTerm}/{page}/{pagesize}")
	public List<SolrOrderPojo> findOrdersBySearchTerm(@PathVariable String searchTerm, @PathVariable int page, @PathVariable int pagesize) {
		return solrOrderServicePort.findByCustomerQuery(searchTerm, PageRequest.of(page, pagesize)).getContent();
	}
	
	@GetMapping("/search/findByDesc/{orderDesc}/{page}/{pagesize}")
	public List<SolrOrderPojo> findOrdersByDescription(@PathVariable String orderDesc, @PathVariable int page, @PathVariable int pagesize) {
		return solrOrderServicePort.findByOrderDescription(orderDesc, PageRequest.of(page, pagesize)).getContent();
	}

	
	/*
	 * // CRUD operations for Order Collection
	 */	
	@PostMapping("/placeorder")
	public String createOrder(@RequestBody SolrOrderPojo order) {
		String description = "Order Placed successfully.";
		solrOrderRepository.save(order);
		return description;
	}

	@GetMapping("/get/{orderid}")
	public SolrOrderPojo readOrder(@PathVariable Long orderid) {
		return solrOrderRepository.findByOrderid(orderid);
	}

	@PutMapping("/update")
	public String updateOrder(@RequestBody SolrOrderPojo order) {
		String description = "Order Updated successfully!";
		solrOrderRepository.save(order);
		return description;
	}

	@DeleteMapping("/delete/{orderid}")
	public String deleteOrder(@PathVariable Long orderid) {
		String description = "Order Deleted successfully!!";
		solrOrderRepository.delete(solrOrderRepository.findByOrderid(orderid));
		return description;
	}


	
}
