package com.solr.clientwrapper.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.solr.clientwrapper.domain.dto.solrsearch.SolrSearchResponseDTO;
import com.solr.clientwrapper.infrastructure.solrbean.SolrSearchResult;
import com.solr.clientwrapper.usecase.solrsearch.SolrSearchAdvanced;
import com.solr.clientwrapper.usecase.solrsearch.SolrSearchBasic;
import com.solr.clientwrapper.usecase.solrsearch.SolrSearchOrdered;
import com.solr.clientwrapper.usecase.solrsearch.SolrSearchPaginated;
import com.solr.clientwrapper.usecase.solrsearch.SolrSearchUnfiltered;

@RestController
@RequestMapping("/solr-search-records")
public class SolrSearchRecordsResource {
	/* Solr Search Records for given collection- Egress Service Resource */  
	private final Logger logger = LoggerFactory.getLogger(SolrSearchRecordsResource.class);  
    private static final String SOLR_DATA_NAME_DEFAULT = "techproducts";
    
    private SolrSearchBasic solrSearchBasic;
    private SolrSearchOrdered solrSearchOrdered;
    private SolrSearchAdvanced solrSearchAdvanced;
    private SolrSearchPaginated solrSearchPaginated;
    private SolrSearchUnfiltered solrSearchUnfiltered;

	public SolrSearchRecordsResource(
			SolrSearchBasic solrSearchBasic, SolrSearchOrdered solrSearchOrdered,
			SolrSearchAdvanced solrSearchAdvanced, SolrSearchPaginated solrSearchPaginated,
			SolrSearchUnfiltered solrSearchUnfiltered) {
		this.solrSearchBasic = solrSearchBasic;
		this.solrSearchOrdered = solrSearchOrdered;
		this.solrSearchAdvanced = solrSearchAdvanced;
		this.solrSearchPaginated = solrSearchPaginated;
		this.solrSearchUnfiltered = solrSearchUnfiltered;
	}

	@Autowired
	SolrSearchResult solrSearchResult;

	
	@GetMapping(value = "/unfiltered")
	public ResponseEntity<SolrSearchResponseDTO> searchRecordsInGivenCollectionUnfiltered(
					@RequestParam(defaultValue = SOLR_DATA_NAME_DEFAULT) String collection) {
		logger.debug("REST call for UNFILTERED SEARCH search in the given collection");
		SolrSearchResponseDTO solrSearchResponseDTO = solrSearchUnfiltered.unfilteredSearch(collection);
		if(solrSearchResponseDTO.getStatusCode() == 200) {
			return ResponseEntity.status(HttpStatus.OK)
								.body(solrSearchResponseDTO);
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
								.body(solrSearchResponseDTO);
		}
	}
	
	@GetMapping(value = "/basic")
	public ResponseEntity<SolrSearchResponseDTO> searchRecordsInGivenCollectionByQueryField(
											@RequestParam(defaultValue = SOLR_DATA_NAME_DEFAULT) String collection, 
											@RequestParam(defaultValue = "name") String queryField, 
											@RequestParam(defaultValue = "*") String queryFieldSearchTerm) {
		logger.debug("REST call for BASIC SEARCH search in the given collection");
		SolrSearchResponseDTO solrSearchResponseDTO = solrSearchBasic.basicSearch(collection, 
									queryField, 
									queryFieldSearchTerm);
		if(solrSearchResponseDTO.getStatusCode() == 200) {
			return ResponseEntity.status(HttpStatus.OK)
								.body(solrSearchResponseDTO);
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
								.body(solrSearchResponseDTO);
		}
	}
	
	@GetMapping(value = "/ordered")
	public ResponseEntity<SolrSearchResponseDTO> searchRecordsInGivenCollectionByQueryFieldOrderedByTag(
											@RequestParam(defaultValue = SOLR_DATA_NAME_DEFAULT) String collection, 
											@RequestParam(defaultValue = "name") String queryField, 
											@RequestParam(defaultValue = "*") String searchTerm, 
											@RequestParam(defaultValue = "id") String tag, 
											@RequestParam(defaultValue = "asc") String order) {
		logger.debug("REST call for ORDERED SEARCH search in the given collection");
		SolrSearchResponseDTO solrSearchResponseDTO = solrSearchOrdered.orderedSearch(collection, 
													queryField, 
													searchTerm, 
													tag, 
													order);
		if(solrSearchResponseDTO.getStatusCode() == 200) {
			return ResponseEntity.status(HttpStatus.OK)
								.body(solrSearchResponseDTO);
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
								.body(solrSearchResponseDTO);
		}
	}
	
	@GetMapping(value = "/advanced")
	public ResponseEntity<SolrSearchResponseDTO> searchRecordsInGivenCollectionAdvanced(
											@RequestParam(defaultValue = SOLR_DATA_NAME_DEFAULT) String collection, 
											@RequestParam(defaultValue = "name") String queryField, 
											@RequestParam(defaultValue = "*") String searchTerm,
											@RequestParam(defaultValue = "0") String startRecord, 
											@RequestParam(defaultValue = "5") String pageSize, 
											@RequestParam(defaultValue = "id") String tag, 
											@RequestParam(defaultValue = "asc") String order) {
		logger.debug("REST call for ADVANCED SEARCH search in the given collection");
		SolrSearchResponseDTO solrSearchResponseDTO = solrSearchAdvanced.advancedSearch(collection, 
													queryField, 
													searchTerm, 
													startRecord, 
													pageSize, 
													tag, 
													order);
		if(solrSearchResponseDTO.getStatusCode() == 200) {
			return ResponseEntity.status(HttpStatus.OK)
								.body(solrSearchResponseDTO);
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
								.body(solrSearchResponseDTO);
		}
	}

	@GetMapping(value = "/paginated")
	public ResponseEntity<SolrSearchResponseDTO> searchRecordsInGivenCollectionPaginated(
											@RequestParam(defaultValue = SOLR_DATA_NAME_DEFAULT) String collection, 
											@RequestParam(defaultValue = "name") String queryField, 
											@RequestParam(defaultValue = "*") String searchTerm,
											@RequestParam(defaultValue = "0") String startRecord, 
											@RequestParam(defaultValue = "5") String pageSize, 
											@RequestParam(defaultValue = "id") String tag, 
											@RequestParam(defaultValue = "asc") String order, 
											@RequestParam(defaultValue = "0") String startPage) {
		logger.debug("REST call for PAGINATED SEARCH search in the given collection");
		SolrSearchResponseDTO solrSearchResponseDTO = solrSearchPaginated.paginatedSearch(collection, 
				queryField, 
				searchTerm, 
				startRecord, 
				pageSize, 
				tag, 
				order, 
				startPage);
		if(solrSearchResponseDTO.getStatusCode() == 200) {
			return ResponseEntity.status(HttpStatus.OK)
								.body(solrSearchResponseDTO);
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
								.body(solrSearchResponseDTO);
		}
	}
}
