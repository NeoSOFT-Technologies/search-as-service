package com.solr.clientwrapper.rest;

import java.io.IOException;
import java.util.List;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.SolrQuery.SortClause;
import org.apache.solr.client.solrj.beans.DocumentObjectBinder;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.solr.clientwrapper.infrastructure.solrbean.SolrCollectionIndex;
import com.solr.clientwrapper.infrastructure.solrbean.SolrSearchResult;

@RestController
@RequestMapping("/solr")
public class SolrSearchOperationResource {
	/*
	 * /////// ############## Solr Search Operation ################ /////////
	 * Author: Piyush Ojha; NeoSOFT Tech.
	 */    
	
    @Autowired
    private SolrClient solrClient;
    private final static String solrDataNameDefault = "techproducts";
    private final static String solrDataName = "techproducts";
    
	@Autowired
	SolrSearchResult solrSearchResult;
	
	@Autowired
	SolrCollectionIndex solrCollectionIndex;


	@GetMapping(value = "/search")
	public ResponseEntity<SolrSearchResult> searchRecordsInGivenCollection(
											@RequestParam(defaultValue = solrDataNameDefault) String collection, 
											@RequestParam(defaultValue = "name") String queryField, 
											@RequestParam(defaultValue = "*") String searchTerm,
											@RequestParam(defaultValue = "0") String startRecord, 
											@RequestParam(defaultValue = "5") String pageSize, 
											@RequestParam(defaultValue = "id") String tag, 
											@RequestParam(defaultValue = "asc") String order)
													throws SolrServerException, IOException {
		SolrClient client = new HttpSolrClient.Builder("http://localhost:8983/solr/"+collection).build();
		SolrQuery query = new SolrQuery();
		query.set("q", queryField + ":" + searchTerm);
		query.set("start", startRecord);
		query.set("rows", pageSize);

		SortClause sortClause = new SortClause(tag, order);
		query.setSort(sortClause);
		
		QueryResponse response = client.query(query);
		SolrDocumentList numdocs = response.getResults();
		
		// testing... : start /////////
		DocumentObjectBinder binder = new DocumentObjectBinder();
		
		List<SolrCollectionIndex> docs = binder.getBeans(SolrCollectionIndex.class, numdocs);
		
		// testing: end  ////////////
		
		response.getDebugMap();
		long numDocs = numdocs.getNumFound();
		solrSearchResult.setNumDocs(numDocs);
		solrSearchResult.setSolrCollectionIndex(docs);
		return new ResponseEntity<>(solrSearchResult, HttpStatus.OK);

	}
	
	@GetMapping(value = "/search/paginated")
	public ResponseEntity<SolrSearchResult> paginatedSearch(
											@RequestParam(defaultValue = solrDataNameDefault) String collection, 
											@RequestParam(defaultValue = "name") String queryField, 
											@RequestParam(defaultValue = "*") String searchTerm,
											@RequestParam(defaultValue = "0") String startRecord, 
											@RequestParam(defaultValue = "5") String pageSize, 
											@RequestParam(defaultValue = "id") String tag, 
											@RequestParam(defaultValue = "asc") String order, 
											@RequestParam(defaultValue = "0") int startPage)
													throws SolrServerException, IOException {
		SolrClient client = new HttpSolrClient.Builder("http://localhost:8983/solr/"+collection).build();
		SolrQuery query = new SolrQuery();
		query.set("q", queryField + ":" + searchTerm);
		query.set("start", startRecord);
		query.set("rows", pageSize);

		// set sorting strategy
		SortClause sortClause = new SortClause(tag, order);
		query.setSort(sortClause);
		
		QueryResponse response = client.query(query);
		SolrDocumentList numdocs = response.getResults();
		
		// testing... : start /////////
		DocumentObjectBinder binder = new DocumentObjectBinder();
		List<SolrCollectionIndex> docs = binder.getBeans(SolrCollectionIndex.class, numdocs);	
		// testing: end  ////////////
		
		response.getDebugMap();
		long numDocs = numdocs.getNumFound();
		solrSearchResult.setNumDocs(numDocs);
		solrSearchResult.setSolrCollectionIndex(docs);
		return new ResponseEntity<>(solrSearchResult, HttpStatus.OK);

	}
}
