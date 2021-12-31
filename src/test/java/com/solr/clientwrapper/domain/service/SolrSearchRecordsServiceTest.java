/**
 * 
 */
package com.solr.clientwrapper.domain.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.io.IOException;

/*import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;*/

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.solr.clientwrapper.domain.dto.solr.SolrAPIAdapterResponseDTO;
import com.solr.clientwrapper.domain.dto.solrsearch.SolrSearchResponseDTO;
import com.solr.clientwrapper.domain.port.api.SolrSearchRecordsServicePort;
import com.solr.clientwrapper.infrastructure.adaptor.SolrAPIAdapter;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
//@ActiveProfiles("test")
@TestPropertySource(
        properties = {
                "base-solr-url-8985: http://localhost:8985/solr", 
                "base-solr-collection: techproducts"
        }
)
class SolrSearchRecordsServiceTest {
	private final Logger logger = LoggerFactory.getLogger(SolrSearchRecordsServiceTest.class);

	@Value("${base-solr-collection}")
	private String SOLR_COLLECTION;
	@Value("${base-solr-url-8985}")
	private String SOLR_URL;
	
	/* Mock the dependencies */
	@MockBean
	private SolrSearchRecordsServicePort solrSearchRecordsServicePort;
	//@Autowired
	@MockBean
	private SolrAPIAdapter solrAPIAdapterMock;
	@InjectMocks
	private SolrSearchRecordsService solrSearchRecordsService;
	
	private SolrAPIAdapter solrAPIAdapter = new SolrAPIAdapter();
	SolrClient solrClient = null;
	
	void setUpMockitoForInvalidCollection(String invalidCollection) {
		SolrAPIAdapterResponseDTO solrApiResponseDTO = solrAPIAdapter
				.getSolrClientAdapter(SOLR_URL, invalidCollection);
		
		int responseStatusCode = solrApiResponseDTO.getStatusCode();
		if(responseStatusCode == 200) {
			solrClient = solrApiResponseDTO.getSolrClient();
			when(solrAPIAdapterMock.getSolrClient(Mockito.any(), Mockito.any()))
				.thenReturn(solrClient);
		} else {
			when(solrAPIAdapterMock.getSolrClient(Mockito.any(), Mockito.any()))
				.thenReturn(null);
		}
	}
	
	@BeforeEach
	void setUp() {
		solrClient = solrAPIAdapter.getSolrClient(SOLR_URL, SOLR_COLLECTION);
		when(solrAPIAdapterMock.getSolrClient(Mockito.any(), Mockito.any()))
			.thenReturn(solrClient);
	}
	
	/**
	 * Test method for {@link com.solr.clientwrapper.domain.service.SolrSearchRecordsService#setUpSelectQueryUnfiltered(java.lang.String)}.
	 * @throws IOException 
	 * @throws SolrServerException 
	 */
	@Disabled
	@Test
	@DisplayName("Test Solr Search UNFILTERED Service for the given collection")
	void testSetUpSelectQueryUnfiltered() throws SolrServerException, IOException {
		logger.info("Solr Search UNFILTERED service test is started..");
	
		int expectedStatusResponse = 200;
		SolrSearchResponseDTO receivedResponse;
		receivedResponse = solrSearchRecordsService.setUpSelectQueryUnfiltered(SOLR_COLLECTION);
		assertEquals(
				expectedStatusResponse, 
				receivedResponse.getStatusCode());
		logger.info("Positive testing is completed.");
		
		/* Test service when invalid collection is provided as input */
		String invalidCollection = "invalidcollection";
		setUpMockitoForInvalidCollection(invalidCollection);
		expectedStatusResponse = 400;
		receivedResponse = solrSearchRecordsService.setUpSelectQueryUnfiltered(invalidCollection);
		assertEquals(
				expectedStatusResponse, 
				receivedResponse.getStatusCode());
		logger.info("Negative testing is completed for invalid Solr Collection.");
	}

	/**
	 * Test method for {@link com.solr.clientwrapper.domain.service.SolrSearchRecordsService#setUpSelectQueryBasicSearch(java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Disabled
	@Test
	@DisplayName("Test Solr BASIC Search Service for the given collection")
	void testSetUpSelectQueryBasicSearch() {
		logger.info("Solr Search BASIC service test is started..");

		int expectedStatusResponse = 200;
		SolrSearchResponseDTO receivedResponse;
		receivedResponse = solrSearchRecordsService.setUpSelectQueryBasicSearch(
				SOLR_COLLECTION, 
				"name", 
				"*");
		assertEquals(
				expectedStatusResponse, 
				receivedResponse.getStatusCode());
		logger.info("Positive testing is completed.");
		
		/* Test service when invalid collection is provided as input */
		String invalidCollection = "invalidcollection";
		setUpMockitoForInvalidCollection(invalidCollection);
		expectedStatusResponse = 400;
		receivedResponse = solrSearchRecordsService.setUpSelectQueryBasicSearch(
				SOLR_COLLECTION, 
				"name", 
				"*");
		assertEquals(
				expectedStatusResponse, 
				receivedResponse.getStatusCode());
		logger.info("Negative testing is completed for invalid Solr Collection.");
	}

	/**
	 * Test method for {@link com.solr.clientwrapper.domain.service.SolrSearchRecordsService#setUpSelectQueryOrderedSearch(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Disabled
	@Test
	@DisplayName("Test Solr Search ORDERED Service for the given collection")
	void testSetUpSelectQueryOrderedSearch() {
		logger.info("Solr Search ORDERED service test is started..");
		
		int expectedStatusResponse = 200;
		SolrSearchResponseDTO receivedResponse;
		receivedResponse = solrSearchRecordsService.setUpSelectQueryOrderedSearch(
				SOLR_COLLECTION, 
				"name", 
				"*", 
				"id", 
				"asc");
		assertEquals(
				expectedStatusResponse, 
				receivedResponse.getStatusCode());
		logger.info("Positive testing is completed.");
		
		/* Test service when invalid collection is provided as input */
		String invalidCollection = "invalidcollection";
		setUpMockitoForInvalidCollection(invalidCollection);
		expectedStatusResponse = 400;
		receivedResponse = solrSearchRecordsService.setUpSelectQueryOrderedSearch(
				SOLR_COLLECTION, 
				"name", 
				"*", 
				"id", 
				"asc");
		assertEquals(
				expectedStatusResponse, 
				receivedResponse.getStatusCode());
		logger.info("Negative testing is completed for invalid Solr Collection.");
	}

	/**
	 * Test method for {@link com.solr.clientwrapper.domain.service.SolrSearchRecordsService#setUpSelectQueryAdvancedSearch(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Disabled
	@Test
	@DisplayName("Test Solr Search ADVANCED Service for the given collection")
	void testSetUpSelectQueryAdvancedSearch() {
		logger.info("Solr Search ADVANCED service test is started..");
	
		int expectedStatusResponse = 200;
		SolrSearchResponseDTO receivedResponse;
		receivedResponse = solrSearchRecordsService.setUpSelectQueryAdvancedSearch(
				SOLR_COLLECTION, 
				"name", 
				"*", 
				"0", 
				"5", 
				"id", 
				"asc");
		assertEquals(
				expectedStatusResponse, 
				receivedResponse.getStatusCode());
		logger.info("Positive testing is completed.");
		
		/* Test service when invalid collection is provided as input */
		String invalidCollection = "invalidcollection";
		setUpMockitoForInvalidCollection(invalidCollection);
		expectedStatusResponse = 400;
		receivedResponse = solrSearchRecordsService.setUpSelectQueryAdvancedSearch(
				SOLR_COLLECTION, 
				"name", 
				"*", 
				"0", 
				"5", 
				"id", 
				"asc");
		assertEquals(
				expectedStatusResponse, 
				receivedResponse.getStatusCode());
		logger.info("Negative testing is completed for invalid Solr Collection.");
	}

	/**
	 * Test method for {@link com.solr.clientwrapper.domain.service.SolrSearchRecordsService#setUpSelectQueryAdvancedSearchWithPagination(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Disabled
	@Test
	@DisplayName("Test Solr Search PAGINATED Service for the given collection")
	void testSetUpSelectQueryAdvancedSearchWithPagination() {
		logger.info("Solr Search PAGINATED service test is started..");

		int expectedStatusResponse = 200;
		SolrSearchResponseDTO receivedResponse;
		receivedResponse = solrSearchRecordsService.setUpSelectQueryAdvancedSearchWithPagination(
				SOLR_COLLECTION, 
				"name", 
				"*", 
				"0", 
				"5", 
				"id", 
				"asc", 
				"0");
		assertEquals(
				expectedStatusResponse, 
				receivedResponse.getStatusCode());
		logger.info("Positive testing is completed.");
		
		/* Test service when invalid collection is provided as input */
		String invalidCollection = "invalidcollection";
		setUpMockitoForInvalidCollection(invalidCollection);
		expectedStatusResponse = 400;
		receivedResponse = solrSearchRecordsService.setUpSelectQueryAdvancedSearchWithPagination(
				SOLR_COLLECTION, 
				"name", 
				"*", 
				"0", 
				"5", 
				"id", 
				"asc", 
				"0");
		assertEquals(
				expectedStatusResponse, 
				receivedResponse.getStatusCode());
		logger.info("Negative testing is completed for invalid Solr Collection.");
	}
}



