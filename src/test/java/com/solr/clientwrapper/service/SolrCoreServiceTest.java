package com.solr.clientwrapper.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.skyscreamer.jsonassert.JSONAssert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.solr.clientwrapper.domain.dto.solr.SolrResponseDTO;
import com.solr.clientwrapper.domain.port.api.SolrCoreServicePort;
import com.solr.clientwrapper.domain.service.SolrCoreService;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
class SolrCoreServiceTest {

	Logger logger = LoggerFactory.getLogger(SolrCoreServiceTest.class);
	
	String coreName = "DemoCore";
	String newCoreName = "newDemoCore";
	
	@MockBean
	SolrCoreService solrcoreservice;
	
	@Autowired
	SolrCoreServicePort solrcoreserviceport;
	
	 public void setMockitoSuccessResponseForService() {
	        SolrResponseDTO solrResponseDTO = new SolrResponseDTO(coreName);
	        solrResponseDTO.setStatusCode(200);
	        solrResponseDTO.setMessage("Testing");


	        Mockito.when(solrcoreservice.create(Mockito.any())).thenReturn(solrResponseDTO);
	        Mockito.when(solrcoreservice.delete(Mockito.any())).thenReturn(solrResponseDTO);
	        Mockito.when(solrcoreservice.rename(Mockito.any(),Mockito.any())).thenReturn(solrResponseDTO);
	        Mockito.when(solrcoreservice.swap(Mockito.any(), Mockito.any())).thenReturn(solrResponseDTO);
	        Mockito.when(solrcoreservice.reload(Mockito.any())).thenReturn(solrResponseDTO);
	        Mockito.when(solrcoreservice.status(Mockito.any())).thenReturn("Success");
	 }
	
	 public void setMockitoBadResponseForService() {
	        SolrResponseDTO solrResponseDTO = new SolrResponseDTO(coreName);
	        solrResponseDTO.setStatusCode(400);
	        solrResponseDTO.setMessage("Testing");
	        
	        
	        
	        Mockito.when(solrcoreservice.create(Mockito.any())).thenReturn(solrResponseDTO);
	        Mockito.when(solrcoreservice.delete(Mockito.any())).thenReturn(solrResponseDTO);
	        Mockito.when(solrcoreservice.rename(Mockito.any(),Mockito.any())).thenReturn(solrResponseDTO);
	        Mockito.when(solrcoreservice.swap(Mockito.any(), Mockito.any())).thenReturn(solrResponseDTO);
	        Mockito.when(solrcoreservice.reload(Mockito.any())).thenReturn(solrResponseDTO);
	        Mockito.when(solrcoreservice.status(Mockito.any())).thenReturn("Failure");
	 }
	 
	 @Test
	 void testCreateSolrCore() {
		int statusCode = 200;
		
		// Create Core
		setMockitoSuccessResponseForService();
		SolrResponseDTO solrresponseDto = solrcoreserviceport.create(coreName);
		assertEquals(statusCode, solrresponseDto.getStatusCode());
		
		// Create core with Existing CoreName
		
		setMockitoBadResponseForService();
		SolrResponseDTO solrResponseDTO = solrcoreserviceport.create(coreName);
		assertNotEquals(statusCode, solrResponseDTO.getStatusCode());
	 }
	 
	 @Test
	 void testDeleteSolrCore() {
		 int statusCode = 200;
			
			// Delete Core
			setMockitoSuccessResponseForService();
			SolrResponseDTO solrresponseDto = solrcoreserviceport.delete(coreName);
			assertEquals(statusCode, solrresponseDto.getStatusCode());
			
			// delete core with NonExisting CoreName
			
			setMockitoBadResponseForService();
			SolrResponseDTO solrResponseDTO = solrcoreserviceport.delete(coreName);
			assertNotEquals(statusCode, solrResponseDTO.getStatusCode()); 
	 }
	 
	 @Test
	 void testRenameSolrCore() {
		 int statusCode = 200;
			
			// Rename Core
			setMockitoSuccessResponseForService();
			SolrResponseDTO solrresponseDto = solrcoreserviceport.rename(coreName, newCoreName);
			assertEquals(statusCode, solrresponseDto.getStatusCode());
			
			// rename core with NonExisting CoreName
			setMockitoBadResponseForService();
			SolrResponseDTO solrResponseDTO = solrcoreserviceport.rename("oldCore", newCoreName);
			assertNotEquals(statusCode, solrResponseDTO.getStatusCode()); 
	 }
	 @Test
	 void testSwapSolrCore() {
		 int statusCode = 200;
			
			// Swap Two Core with Existing Core
			setMockitoSuccessResponseForService();
			SolrResponseDTO solrresponseDto = solrcoreserviceport.swap(coreName, newCoreName);
			assertEquals(statusCode, solrresponseDto.getStatusCode());
			
			// Swap core with NonExisting Core
			setMockitoBadResponseForService();
			SolrResponseDTO solrResponseDTO = solrcoreserviceport.swap("oldCore", newCoreName);
			assertNotEquals(statusCode, solrResponseDTO.getStatusCode()); 
	 }
	 @Test
	 void testReloadSolrCore() {
		 int statusCode = 200;
			
			// Reload Existing Core
			setMockitoSuccessResponseForService();
			SolrResponseDTO solrresponseDto = solrcoreserviceport.reload(coreName);
			assertEquals(statusCode, solrresponseDto.getStatusCode());
			
			// Reload core with NonExisting Core
			setMockitoBadResponseForService();
			SolrResponseDTO solrResponseDTO = solrcoreserviceport.reload("oldCore");
			assertNotEquals(statusCode, solrResponseDTO.getStatusCode()); 
	 }
	 @Test
	 void testSolrCoreStatus() {
		 String expectedResponse = "Success";
			
			// Get Status of Existing Core
			setMockitoSuccessResponseForService();
			String response= solrcoreserviceport.status(coreName);
			JSONAssert.assertEquals(expectedResponse, response, true);		
			
			
			// Get Status of NonExisting Core
			setMockitoBadResponseForService();
			String expectedResponse2 = "Failure";
			String response2= solrcoreserviceport.status(coreName);
			JSONAssert.assertEquals(expectedResponse2, response2, true);	
	 }
}
