package com.solr.clientwrapper.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.solr.clientwrapper.domain.service.SolrSearchOperationService;
import com.solr.clientwrapper.infrastructure.solrbean.SolrSearchResult;

@RestController
@RequestMapping("/solr")
public class SolrSearchOperationResource {
	/*
	 * /////// ############## Solr Search Operation ################ /////////
	 */    
	
    private static final String SOLR_DATA_NAME_DEFAULT = "techproducts";
    
	@Autowired
	SolrSearchResult solrSearchResult;
	@Autowired
	SolrSearchOperationService solrSearchOperationService;

	@GetMapping(value = "/search")
	public ResponseEntity<SolrSearchResult> searchRecordsInGivenCollection(
											@RequestParam(defaultValue = SOLR_DATA_NAME_DEFAULT) String collection, 
											@RequestParam(defaultValue = "name") String queryField, 
											@RequestParam(defaultValue = "*") String searchTerm,
											@RequestParam(defaultValue = "0") String startRecord, 
											@RequestParam(defaultValue = "5") String pageSize, 
											@RequestParam(defaultValue = "id") String tag, 
											@RequestParam(defaultValue = "asc") String order) {

		solrSearchOperationService.setUpSelectQuery(collection, 
													queryField, 
													searchTerm, 
													startRecord, 
													pageSize, 
													tag, 
													order);
		return new ResponseEntity<>(solrSearchResult, HttpStatus.OK);
	}
	
	@GetMapping(value = "/search/paginated")
	public ResponseEntity<SolrSearchResult> paginatedSearch(
											@RequestParam(defaultValue = SOLR_DATA_NAME_DEFAULT) String collection, 
											@RequestParam(defaultValue = "name") String queryField, 
											@RequestParam(defaultValue = "*") String searchTerm,
											@RequestParam(defaultValue = "0") String startRecord, 
											@RequestParam(defaultValue = "5") String pageSize, 
											@RequestParam(defaultValue = "id") String tag, 
											@RequestParam(defaultValue = "asc") String order, 
											@RequestParam(defaultValue = "0") String startPage) {
		solrSearchOperationService.setUpSelectQueryWithPagination(collection, 
				queryField, 
				searchTerm, 
				startRecord, 
				pageSize, 
				tag, 
				order, 
				startPage);
		return new ResponseEntity<>(solrSearchResult, HttpStatus.OK);
	}
}
