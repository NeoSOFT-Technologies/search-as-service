package com.solr.clientwrapper.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.solr.clientwrapper.IntegrationTest;
import com.solr.clientwrapper.domain.dto.solr.SolrDocumentDTO;
import com.solr.clientwrapper.domain.dto.solr.SolrDocumentResponseDTO;
import com.solr.clientwrapper.domain.dto.solr.SolrFieldDTO;
import com.solr.clientwrapper.domain.service.SolrInMemeoryCacheService;
import com.solr.clientwrapper.infrastructure.solrenum.SolrFieldType;

@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
public class SolrInMemoryCacheServiceTest {
	
	String solrendpoint = "/cacheschema";
	String tableName = "gettingstarted3";
	String name = "default-config";
	SolrFieldDTO solr = new SolrFieldDTO("testField6", SolrFieldType._nest_path_, "mydefault", true, true, false, true, true);
	SolrFieldDTO[] attributes = { solr };
	
	@MockBean
	private SolrInMemeoryCacheService solrInMemeoryCacheService;
	
	@Test
	@Transactional
	void testCreateSchemaSevice() throws IOException, Exception {
		
		SolrDocumentResponseDTO solrDocumentResponseDTO = new SolrDocumentResponseDTO(tableName,name, attributes);
		
		SolrDocumentDTO solrDocumentDTO = new SolrDocumentDTO(tableName, name, attributes);
		when(solrInMemeoryCacheService.create(tableName, name, solrDocumentDTO)).thenReturn(solrDocumentResponseDTO);
		SolrDocumentResponseDTO createsolr = solrInMemeoryCacheService.create(tableName, name, solrDocumentDTO);
		assertThat(createsolr).isNotNull();
		
	}
	
	@Test
	@Transactional
	void testUpdateSchemaSevice() throws IOException, Exception {
		
		SolrDocumentResponseDTO solrDocumentResponseDTO = new SolrDocumentResponseDTO(tableName,name, attributes);
		
		SolrDocumentDTO solrDocumentDTO = new SolrDocumentDTO(tableName, name, attributes);
		when(solrInMemeoryCacheService.update(tableName, name, solrDocumentDTO)).thenReturn(solrDocumentResponseDTO);
		SolrDocumentResponseDTO updatesolr = solrInMemeoryCacheService.update(tableName, name, solrDocumentDTO);
		assertThat(updatesolr).isNotNull();
		
	}
	
	@Test
	@Transactional
	void testDeleteSchemaSevice() throws IOException, Exception {
		
		SolrDocumentResponseDTO solrDocumentResponseDTO = new SolrDocumentResponseDTO(tableName,name, attributes);
		when(solrInMemeoryCacheService.delete(tableName, name)).thenReturn(solrDocumentResponseDTO);
		SolrDocumentResponseDTO deletesolr = solrInMemeoryCacheService.delete(tableName, name);
		assertThat(deletesolr).isNotNull();
		
	}
	
	@Test
	@Transactional
	void testGetSchemaSevice() throws IOException, Exception {
		
		SolrDocumentResponseDTO solrDocumentResponseDTO = new SolrDocumentResponseDTO(tableName,name, attributes);
		when(solrInMemeoryCacheService.get(tableName, name)).thenReturn(solrDocumentResponseDTO);
		SolrDocumentResponseDTO getsolr = solrInMemeoryCacheService.get(tableName, name);
		assertThat(getsolr).isNotNull();
		
	}
	
	
}
