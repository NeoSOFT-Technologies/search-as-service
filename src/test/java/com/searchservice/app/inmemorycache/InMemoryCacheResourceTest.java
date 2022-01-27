package com.searchservice.app.inmemorycache;


import com.searchservice.app.IntegrationTest;
import com.searchservice.app.TestUtil;
import com.searchservice.app.domain.dto.document.DocumentDTO;
import com.searchservice.app.domain.dto.document.DocumentResponseDTO;
import com.searchservice.app.domain.dto.schema.FieldDTO;
import com.searchservice.app.domain.service.InMemoryCacheService;
import com.searchservice.app.infrastructure.solrenum.SchemaFieldType;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@IntegrationTest
@AutoConfigureMockMvc
class InMemoryCacheResourceTest {

	String solrendpoint = "/cacheschema";
	String tableName = "gettingstarted3";
	String name = "default-config";
	FieldDTO solr = new FieldDTO("testField6", SchemaFieldType._nest_path_, "mydefault", true, true, false, true, true);
	FieldDTO[] attributes = { solr };
	String expectedGetResponse = "{\n"
			  +"\"tableName\": \"gettingstarted3\",\n"
			  +"\"name\": \"default-config\",\n"
			  +"\"attributes\": [{\n"
		      +"\"name\": \"testField6\",\n"
		      +"\"type\": \"_nest_path_\",\n"
		      +"\"default_\": \"mydefault\",\n"
		      +"\"storable\": false,\n"
		      +"\"filterable\": true,"
		      +"\"required\": true,"
		      +"\"sortable\": true,\n"
		      +"\"multiValue\": true,\n"
		      +"}],\n"
		      +"\"statusCode\": 200\n"
		      +"}";
	String expectedCreateResponse400 = "{\n"
			  +"\"tableName\": \"gettingstarted3\",\n"
			  +"\"name\": \"default-config\",\n"
			  +"\"attributes\": [{\n"
		      +"\"name\": \"testField6\",\n"
		      +"\"type\": \"_nest_path_\",\n"
		      +"\"default_\": \"mydefault\",\n"
		      +"\"storable\": false,\n"
		      +"\"filterable\": true,"
		      +"\"required\": true,"
		      +"\"sortable\": true,\n"
		      +"\"multiValue\": true,\n"
		      +"}],\n"
		      +"\"statusCode\": 400\n"
		      +"}";

	@Autowired
	private MockMvc restAMockMvc;

	@MockBean
	private InMemoryCacheService inMemoryCacheService;

	public void setMockitoSucccessResponseForService() {
		
		DocumentResponseDTO solrDocumentResponseDTO = new DocumentResponseDTO(tableName,name,attributes);
		solrDocumentResponseDTO.setStatusCode(200);
		Mockito.when(inMemoryCacheService.create(Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(solrDocumentResponseDTO);
		Mockito.when(inMemoryCacheService.update(Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(solrDocumentResponseDTO);
		Mockito.when(inMemoryCacheService.delete(Mockito.any(),Mockito.any())).thenReturn(solrDocumentResponseDTO);
		Mockito.when(inMemoryCacheService.get(Mockito.any(),Mockito.any())).thenReturn(solrDocumentResponseDTO);
		
	}

	public void setMockitoBadResponseForService() {
		DocumentResponseDTO solrDocumentResponseDTO = new DocumentResponseDTO(tableName,name,attributes);
		solrDocumentResponseDTO.setStatusCode(400);
		Mockito.when(inMemoryCacheService.create(Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(solrDocumentResponseDTO);
		Mockito.when(inMemoryCacheService.update(Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(solrDocumentResponseDTO);
		Mockito.when(inMemoryCacheService.delete(Mockito.any(),Mockito.any())).thenReturn(solrDocumentResponseDTO);
		Mockito.when(inMemoryCacheService.get(Mockito.any(),Mockito.any())).thenReturn(solrDocumentResponseDTO);	
	}

	@Test
	void testCreateSchema() throws Exception {
		
		DocumentDTO solrDocumentDTO = new DocumentDTO(tableName, name, attributes);
		// CREATE SCHEMA
		setMockitoSucccessResponseForService();
		restAMockMvc.perform(
				MockMvcRequestBuilders
				.post(solrendpoint + "/create")
				.contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(solrDocumentDTO)))
		.andExpect(status().isOk());
		
		// CREATE SCHEMA WITH SAME NAME AND TEST
		setMockitoBadResponseForService();
		solrDocumentDTO = new DocumentDTO(tableName, name, attributes);
		restAMockMvc.perform(
				MockMvcRequestBuilders
				.post(solrendpoint + "/create")
				.contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(solrDocumentDTO)));
		
		// DELETE THE CREATED SCHEMA
		setMockitoSucccessResponseForService();
		solrDocumentDTO = new DocumentDTO(tableName, name, attributes);
		restAMockMvc.perform(
				MockMvcRequestBuilders
				.delete(solrendpoint + "/delete/" + tableName + "/" + name)
				.contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(solrDocumentDTO)))
		.andExpect(status().isOk());
	}
	
	@Test
	void testUpdateSchema() throws IOException, Exception {

		// CREATE SCHEMA
		setMockitoSucccessResponseForService();
		DocumentDTO solrDocumentDTO = new DocumentDTO(tableName, name, attributes);
		restAMockMvc.perform(MockMvcRequestBuilders.post(solrendpoint + "/create")
				.contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(solrDocumentDTO)))
				.andExpect(status().isOk());
		
		// update Schema
		solrDocumentDTO = new DocumentDTO(tableName, name, attributes);
		restAMockMvc.perform(MockMvcRequestBuilders.put(solrendpoint + "/update/" + tableName + "/" + name)
				.contentType(MediaType.APPLICATION_PROBLEM_JSON)
				.content(TestUtil.convertObjectToJsonBytes(solrDocumentDTO))).andExpect(status().isOk());

		// DELETE THE CREATED SCHEMA
		solrDocumentDTO = new DocumentDTO(tableName, name, attributes);
		restAMockMvc.perform(MockMvcRequestBuilders.delete(solrendpoint + "/delete/" + tableName + "/" + name)
				.contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(solrDocumentDTO)))
				.andExpect(status().isOk());
	}


	@Test
	void testDeleteSchema() throws Exception {
		
		// DELETE A NON EXISTING SCHEMA
		DocumentDTO solrDocumentDTO = new DocumentDTO(tableName, name, attributes);
		setMockitoBadResponseForService();
		restAMockMvc.perform(MockMvcRequestBuilders.delete(solrendpoint + "/delete/" + tableName + "/" + name)
				.contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(solrDocumentDTO)))
				.andExpect(status().isBadRequest());

		// CREATE SCHEMA
		setMockitoSucccessResponseForService();
		restAMockMvc.perform(MockMvcRequestBuilders.post(solrendpoint + "/create")
				.contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(solrDocumentDTO)))
				.andExpect(status().isOk());

		// DELETE THE CREATED SCHEMA
		restAMockMvc.perform(MockMvcRequestBuilders.delete(solrendpoint + "/delete/" + tableName + "/" + name)
				.contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(solrDocumentDTO)))
				.andExpect(status().isOk());

	}
	@Test
	void testGetSchema() throws Exception {		
		setMockitoSucccessResponseForService();
		restAMockMvc.perform(
				MockMvcRequestBuilders
				.get(solrendpoint + "/get/" + tableName + "/" + name)
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk());

		// CREATE SCHEMA
		setMockitoSucccessResponseForService();
		DocumentDTO solrDocumentDTO = new DocumentDTO(tableName, name, attributes);
		restAMockMvc.perform(
				MockMvcRequestBuilders
				.post(solrendpoint + "/create")
				.contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(solrDocumentDTO)))
		.andExpect(status().isOk());

		// GET CREATED SCHEMA
		restAMockMvc.perform(
				MockMvcRequestBuilders
				.get(solrendpoint + "/get/" + tableName + "/" + name)
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk());

		// DELETE THE CREATED SCHEMA
		solrDocumentDTO = new DocumentDTO(tableName, name, attributes);
		restAMockMvc.perform(
				MockMvcRequestBuilders
				.delete(solrendpoint + "/delete/" + tableName + "/" + name)
				.contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(solrDocumentDTO)))
		.andExpect(status().isOk());
	}
}