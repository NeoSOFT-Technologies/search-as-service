/**
 * 
 */
package com.solr.clientwrapper.domain.dto.solrsearch;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.solr.clientwrapper.infrastructure.solrbean.SolrSearchResult;

class SolrSearchResponseDTOTest {
	private final Logger logger = LoggerFactory.getLogger(SolrSearchResponseDTOTest.class);

	private int statusCode;
	private String responseMessage;
	private SolrSearchResult solrSearchResultResponse;
	private SolrSearchResponseDTO solrSearchResponseDTO;
	
	String expectedToStringResponse;
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeEach
	void setUp() throws Exception {
		solrSearchResponseDTO = new SolrSearchResponseDTO();
		statusCode = 200;
		responseMessage = "Search Response tested successfully";
		solrSearchResultResponse = null;
		solrSearchResponseDTO.setStatusCode(statusCode);
		solrSearchResponseDTO.setResponseMessage(responseMessage);
		solrSearchResponseDTO.setSolrSearchResultResponse(solrSearchResultResponse);
		expectedToStringResponse = "SolrSearchResponseDTO [statusCode=" + statusCode + ", "
				+ "responseMessage=" + responseMessage + ", "
				+ "solrSearchResultResponse=" + solrSearchResultResponse + "]";
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterEach
	void tearDown() throws Exception {
		solrSearchResponseDTO = null;
	}

	/**
	 * Test method for {@link com.solr.clientwrapper.domain.dto.solrsearch.SolrSearchResponseDTO#toString()}.
	 */
	@Test
	void testToString() {
		logger.info("Testing toString method of SolrSearchResponseDTO");
		String receivedToStringResponse = solrSearchResponseDTO.toString();
		assertEquals(expectedToStringResponse, receivedToStringResponse);
	}
	
	@Test
	void testRequiredArgsConstructor() {
		logger.info("testing ReqArgsConstructor of SolrSearchResponseDTO");
		SolrSearchResponseDTO solrSearchResponseDTOTest = new SolrSearchResponseDTO(
				responseMessage, 
				solrSearchResultResponse);
		assertNotNull(solrSearchResponseDTOTest);
	}
	
	@Test
	void testGetStatusCode() {
		String verifyStatusCode = ""+solrSearchResponseDTO.getStatusCode();
		// check if all the values are digits
		boolean isDigit = true;
		for(int i=0; i<verifyStatusCode.length(); i++) {
			if(!Character.isDigit(verifyStatusCode.charAt(i))) {
				isDigit = false;
				break;
			}
		}
		assertNotNull(verifyStatusCode);
		assertTrue(isDigit);
	}
	
	@Test
	void testSetStatusCode() {
		solrSearchResponseDTO = new SolrSearchResponseDTO();
		logger.info("Test Positive case with status code");
		solrSearchResponseDTO.setStatusCode(200);
		String verifyStatusCode = ""+solrSearchResponseDTO.getStatusCode();
		// check if all the values are digits
		boolean isDigit = true;
		for(int i=0; i<verifyStatusCode.length(); i++) {
			if(!Character.isDigit(verifyStatusCode.charAt(i))) {
				isDigit = false;
				break;
			}
		}
		assertNotNull(verifyStatusCode);
		assertEquals("200", verifyStatusCode);
		assertTrue(isDigit);
		
		logger.info("Test negative case(value: non-numeric) with status code");
		assertThrows(NumberFormatException.class, () -> {
			solrSearchResponseDTO.setStatusCode((Integer.parseInt("f20")));
		});
	}
	
	@Test
	void testSetResponseMessage() {
		solrSearchResponseDTO = new SolrSearchResponseDTO();
		solrSearchResponseDTO.setResponseMessage("Tested");
		String verifyResponseMessage = ""+solrSearchResponseDTO.getResponseMessage();
		assertNotNull(verifyResponseMessage);
		assertEquals("Tested", verifyResponseMessage);
	}
	
	@Test
	void testSetSolrSearchResult() {
		solrSearchResponseDTO = new SolrSearchResponseDTO();
		solrSearchResponseDTO.setSolrSearchResultResponse(new SolrSearchResult());
		SolrSearchResult verifySolrSearchResult = solrSearchResponseDTO.getSolrSearchResultResponse();
		assertNotNull(verifySolrSearchResult);
		assertEquals(SolrSearchResult.class, verifySolrSearchResult.getClass());;
	}

}
