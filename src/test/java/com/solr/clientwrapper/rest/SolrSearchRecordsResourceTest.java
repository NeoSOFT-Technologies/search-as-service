package com.solr.clientwrapper.rest;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import com.solr.clientwrapper.IntegrationTest;
import com.solr.clientwrapper.domain.dto.solrsearch.SolrSearchResponseDTO;
import com.solr.clientwrapper.domain.service.SolrSearchRecordsService;
import com.solr.clientwrapper.infrastructure.solrbean.SolrSearchResult;

@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class SolrSearchRecordsResourceTest {
	private final Logger logger = LoggerFactory.getLogger(SolrSearchRecordsResourceTest.class);
	
	/* Define required CONSTANTS */
	private final String SOLR_ENDPOINT = "/solr-search-records";
	
	@Autowired
	private MockMvc restAMockMvc;
	@Autowired
	SolrSearchResponseDTO solrSearchResponseDTO;
	@MockBean
	private SolrSearchRecordsService solrSearchRecordsService;
	
	public void mockWithSuccessResponse() {
		solrSearchResponseDTO = mockSolrSearchService();
		solrSearchResponseDTO.setStatusCode(200);
		solrSearchResponseDTO.setResponseMessage("Success.");
	}
	public void mockWithBadRequestResponse() {
		solrSearchResponseDTO = mockSolrSearchService();
		solrSearchResponseDTO.setStatusCode(400);
		solrSearchResponseDTO.setResponseMessage("Failure.");
	}
	
	public SolrSearchResponseDTO mockSolrSearchService() {
		solrSearchResponseDTO = new SolrSearchResponseDTO("", new SolrSearchResult());
		Mockito.when(solrSearchRecordsService.setUpSelectQueryUnfiltered(Mockito.any()))
		.thenReturn(solrSearchResponseDTO);
		Mockito.when(solrSearchRecordsService.setUpSelectQueryBasicSearch(
				Mockito.any(), Mockito.any(), Mockito.any()))
		.thenReturn(solrSearchResponseDTO);
		Mockito.when(solrSearchRecordsService.setUpSelectQueryOrderedSearch(
				Mockito.any(), Mockito.any(), Mockito.any(), 
				Mockito.any(), Mockito.any()))
		.thenReturn(solrSearchResponseDTO);
		Mockito.when(solrSearchRecordsService.setUpSelectQueryAdvancedSearch(
				Mockito.any(), Mockito.any(), Mockito.any(), 
				Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()))
		.thenReturn(solrSearchResponseDTO);
		Mockito.when(solrSearchRecordsService.setUpSelectQueryAdvancedSearchWithPagination(
				Mockito.any(), Mockito.any(), Mockito.any(), 
				Mockito.any(), Mockito.any(), Mockito.any(), 
				Mockito.any(), Mockito.any()))
		.thenReturn(solrSearchResponseDTO);
		return solrSearchResponseDTO;
	}

	/**
	 * Parameterized Test for all the Solr Search REST calls
	 * Test method for {@link com.solr.clientwrapper.rest.SolrSearchRecordsResource}.
	 * @throws Exception 
	 */
	@ParameterizedTest
	@Transactional
	@DisplayName("Test all SOLR SEARCH REST calls for the given collection")
	@ValueSource(strings = {"unfiltered", "basic", "ordered", "advanced", "paginated"})
	void testSearchRecordsInGivenCollection(String searchType) throws Exception {
		logger.debug("\nSolr Search REST call testing for \"{} search\" is started..", searchType);
		mockWithSuccessResponse();
		restAMockMvc.perform(
				MockMvcRequestBuilders
				.get(SOLR_ENDPOINT + "/" + searchType)
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk());
		logger.info("Positive Test passed");
		
		mockWithBadRequestResponse();
		restAMockMvc.perform(
				MockMvcRequestBuilders
				.get(SOLR_ENDPOINT + "/" + searchType)
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isBadRequest());
		logger.info("Negative Test passed");
	}

}
