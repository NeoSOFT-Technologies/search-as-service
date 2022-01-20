package com.solr.clientwrapper.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.solr.clientwrapper.domain.dto.solr.SolrResponseDTO;
import com.solr.clientwrapper.domain.dto.solr.collection.SolrGetCollectionsResponseDTO;
import com.solr.clientwrapper.domain.service.SolrCollectionService;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
class SolrCollectionServiceIT {
	
	Logger logger = LoggerFactory.getLogger(SolrCollectionService.class);

	
	String tableName = "automatedTestCollection";
	
	@MockBean
	SolrCollectionService solrCollectionService;
	
	 public void setMockitoSuccessResponseForService() {
	        SolrResponseDTO solrResponseDTO = new SolrResponseDTO(tableName);
	        solrResponseDTO.setStatusCode(200);
	        solrResponseDTO.setMessage("Testing");

	        SolrGetCollectionsResponseDTO solrGetCollectionsResponseDTO=new SolrGetCollectionsResponseDTO();
	        solrGetCollectionsResponseDTO.setStatusCode(200);
	        solrGetCollectionsResponseDTO.setMessage("Testing");

	        Mockito.when(solrCollectionService.create(Mockito.any(),Mockito.any())).thenReturn(solrResponseDTO);
	        Mockito.when(solrCollectionService.delete(Mockito.any())).thenReturn(solrResponseDTO);
	        //Mockito.when(solrCollectionService.rename(Mockito.any(),Mockito.any())).thenReturn(solrResponseDTO);
	        Mockito.when(solrCollectionService.getCollections()).thenReturn(solrGetCollectionsResponseDTO);
	        Mockito.when(solrCollectionService.isCollectionExists(Mockito.any())).thenReturn(solrResponseDTO);
	 }
	 
	 public void setMockitoBadResponseForService() {
	        SolrResponseDTO solrResponseDTO = new SolrResponseDTO(tableName);
	        solrResponseDTO.setStatusCode(400);
	        solrResponseDTO.setMessage("Testing");

	        SolrGetCollectionsResponseDTO solrGetCollectionsResponseDTO=new SolrGetCollectionsResponseDTO();
	        solrGetCollectionsResponseDTO.setStatusCode(400);
	        solrGetCollectionsResponseDTO.setMessage("Testing");

	        Mockito.when(solrCollectionService.create(Mockito.any(),Mockito.any())).thenReturn(solrResponseDTO);
	        Mockito.when(solrCollectionService.delete(Mockito.any())).thenReturn(solrResponseDTO);
	        //Mockito.when(solrCollectionService.rename(Mockito.any(),Mockito.any())).thenReturn(solrResponseDTO);
	        Mockito.when(solrCollectionService.getCollections()).thenReturn(solrGetCollectionsResponseDTO);
	        Mockito.when(solrCollectionService.isCollectionExists(Mockito.any())).thenReturn(solrResponseDTO);
	    }
	 
	 
	 
	 @Test
	 void testCreateSolrCollection() {
		  int statusCode = 200;
		 //CREATE COLLECTION
		 setMockitoSuccessResponseForService();
		 SolrResponseDTO solrresponseDto  = solrCollectionService.create(tableName, "B");
		 assertEquals(statusCode, solrresponseDto.getStatusCode());
		 
		 //CREATE COLLECTION WITH SAME NAME
		 setMockitoBadResponseForService();
		 SolrResponseDTO solrresponseDto2 = solrCollectionService.create(tableName, "B");
		 assertNotEquals(statusCode, solrresponseDto2.getStatusCode());
	 }
	 
	 @Test
	 void testDeleteSolrCollection() {
		 int statusCode = 200;
		 //DELETE COLLECTION
		 setMockitoSuccessResponseForService();
		 SolrResponseDTO solrresponseDto  = solrCollectionService.delete(tableName);
		 assertEquals(statusCode, solrresponseDto.getStatusCode());
		 
		 //DELETE NOT EXISTING COLLECTION
		 setMockitoBadResponseForService();
		 SolrResponseDTO solrresponseDto2 = solrCollectionService.delete(tableName);
		 assertNotEquals(statusCode, solrresponseDto2.getStatusCode());
	 }
	 
//	 @Test
//	 void testRenameSolrCollection() {
//		 
//		 int statusCode = 200;
//		 
//		 //RNAME COLLECTION
//		 setMockitoSuccessResponseForService();
//		 //SolrResponseDTO solrresponseDto  = solrCollectionService.rename(tableName, "ABC");
//		 assertEquals(statusCode, solrresponseDto.getStatusCode());
//		 
//		 //RENAME NON EXISTING COLLECTION
//		 setMockitoBadResponseForService();
//		 //SolrResponseDTO solrresponseDto2 = solrCollectionService.rename(tableName, "ACBD");
//		 assertNotEquals(statusCode, solrresponseDto2.getStatusCode());
//	 }
	 @Test
	 void testGetSolrCollections() {
		 int statusCode = 200;
		 setMockitoSuccessResponseForService();
		 SolrGetCollectionsResponseDTO solrresponseDto  = solrCollectionService.getCollections();
		 assertEquals(statusCode, solrresponseDto.getStatusCode());
		 
		 
		 
		 setMockitoBadResponseForService();
		 SolrGetCollectionsResponseDTO solrresponseDto2 = solrCollectionService.getCollections();
		 assertNotEquals(statusCode, solrresponseDto2.getStatusCode());
	 }
	 @Test
	 void testIsCollectionExists() {
		 int statusCode = 200;
		 
		 setMockitoSuccessResponseForService();
		 SolrResponseDTO solrresponseDto  = solrCollectionService.isCollectionExists(tableName);
		 assertEquals(statusCode, solrresponseDto.getStatusCode());
		 
		 
		 setMockitoBadResponseForService();
		 SolrResponseDTO solrresponseDto2 = solrCollectionService.isCollectionExists(tableName);
		 assertNotEquals(statusCode, solrresponseDto2.getStatusCode());}
}
