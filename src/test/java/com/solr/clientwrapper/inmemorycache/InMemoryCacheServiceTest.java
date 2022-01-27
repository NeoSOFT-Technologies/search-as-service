package com.solr.clientwrapper.inmemorycache;


import com.solr.clientwrapper.IntegrationTest;
import com.solr.clientwrapper.domain.dto.document.DocumentDTO;
import com.solr.clientwrapper.domain.dto.document.DocumentResponseDTO;
import com.solr.clientwrapper.domain.dto.schema.FieldDTO;
import com.solr.clientwrapper.domain.service.InMemoryCacheService;
import com.solr.clientwrapper.infrastructure.solrenum.SchemaFieldType;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.io.IOException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

@IntegrationTest
@AutoConfigureMockMvc
public class InMemoryCacheServiceTest {
	
	String solrendpoint = "/cacheschema";
	String tableName = "gettingstarted3";
	String name = "default-config";
	FieldDTO solr = new FieldDTO("testField6", SchemaFieldType._nest_path_, "mydefault", true, true, false, true, true);
	FieldDTO[] attributes = { solr };
	
	@MockBean
	private InMemoryCacheService inMemoryCacheService;
	
	@Test
	void testCreateSchemaService() throws IOException, Exception {
		
		DocumentResponseDTO solrDocumentResponseDTO = new DocumentResponseDTO(tableName,name, attributes);
		
		DocumentDTO solrDocumentDTO = new DocumentDTO(tableName, name, attributes);
		when(inMemoryCacheService.create(tableName, name, solrDocumentDTO)).thenReturn(solrDocumentResponseDTO);
		DocumentResponseDTO createsolr = inMemoryCacheService.create(tableName, name, solrDocumentDTO);
		assertThat(createsolr).isNotNull();
		
	}
	
	@Test
	void testUpdateSchemaService() throws IOException, Exception {
		
		DocumentResponseDTO solrDocumentResponseDTO = new DocumentResponseDTO(tableName,name, attributes);
		
		DocumentDTO solrDocumentDTO = new DocumentDTO(tableName, name, attributes);
		when(inMemoryCacheService.update(tableName, name, solrDocumentDTO)).thenReturn(solrDocumentResponseDTO);
		DocumentResponseDTO updatesolr = inMemoryCacheService.update(tableName, name, solrDocumentDTO);
		assertThat(updatesolr).isNotNull();
		
	}
	
	@Test
	void testDeleteSchemaService() throws IOException, Exception {
		
		DocumentResponseDTO solrDocumentResponseDTO = new DocumentResponseDTO(tableName,name, attributes);
		when(inMemoryCacheService.delete(tableName, name)).thenReturn(solrDocumentResponseDTO);
		DocumentResponseDTO deletesolr = inMemoryCacheService.delete(tableName, name);
		assertThat(deletesolr).isNotNull();
		
	}
	
	@Test
	void testGetSchemaService() throws IOException, Exception {
		
		DocumentResponseDTO solrDocumentResponseDTO = new DocumentResponseDTO(tableName,name, attributes);
		when(inMemoryCacheService.get(tableName, name)).thenReturn(solrDocumentResponseDTO);
		DocumentResponseDTO getsolr = inMemoryCacheService.get(tableName, name);
		assertThat(getsolr).isNotNull();
		
	}
	
	
}
