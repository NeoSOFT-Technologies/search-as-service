package com.solr.clientwrapper.rest;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import com.solr.clientwrapper.IntegrationTest;
import com.solr.clientwrapper.domain.dto.solr.SolrFieldDTO;
import com.solr.clientwrapper.domain.dto.solr.SolrSchemaDTO;
import com.solr.clientwrapper.domain.dto.solr.SolrSchemaResponseDTO;
import com.solr.clientwrapper.domain.service.SolrSchemaService;
import com.solr.clientwrapper.solrwrapper.TestUtil;

@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class SchemaResourceTest {

	String solrendpoint = "/api/schema";
	String tableName = "gettingstarted1";
	String name = "default-config";
	SolrFieldDTO solr = new SolrFieldDTO("testField6", com.solr.clientwrapper.infrastructure.solrenum.SolrFieldType._nest_path_, "mydefault", true, true, false, true, true);
	SolrFieldDTO[] attributes = { solr };
	String expectedGetResponse = "{\n"
			  +"\"tableName\": \"gettingstarted1\",\n"
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
			  +"\"tableName\": \"gettingstarted1\",\n"
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
	private SolrSchemaService solrSchemaService;

	public void setMockitoSucccessResponseForService() {
		SolrSchemaResponseDTO solrResponseDTO = new SolrSchemaResponseDTO(tableName, name, attributes);
		solrResponseDTO.setStatusCode(200);
		Mockito.when(solrSchemaService.create(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(solrResponseDTO);
		Mockito.when(solrSchemaService.delete(Mockito.any())).thenReturn(solrResponseDTO);
		Mockito.when(solrSchemaService.update(Mockito.any(), Mockito.any())).thenReturn(solrResponseDTO);
		Mockito.when(solrSchemaService.get(Mockito.any())).thenReturn(solrResponseDTO);
	}

	public void setMockitoBadResponseForService() {
		SolrSchemaResponseDTO solrResponseDTO = new SolrSchemaResponseDTO(tableName, name, attributes);
		solrResponseDTO.setStatusCode(400);
		Mockito.when(solrSchemaService.create(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(solrResponseDTO);
		Mockito.when(solrSchemaService.delete(Mockito.any())).thenReturn(solrResponseDTO);
		Mockito.when(solrSchemaService.update(Mockito.any(), Mockito.any())).thenReturn(solrResponseDTO);
		Mockito.when(solrSchemaService.get(Mockito.any())).thenReturn(solrResponseDTO);
	}

	@Test
	@Transactional
	void testCreateSchema() throws Exception {
		SolrSchemaDTO solrSchemaDTO = new SolrSchemaDTO(tableName, name, attributes);
		// CREATE SCHEMA
		setMockitoSucccessResponseForService();
		restAMockMvc.perform(
				MockMvcRequestBuilders
				.post(solrendpoint )
				.contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(solrSchemaDTO)))
		.andExpect(status().isOk());

		// CREATE SCHEMA WITH SAME NAME AND TEST
		setMockitoBadResponseForService();
		solrSchemaDTO = new SolrSchemaDTO(tableName, name, attributes);
		restAMockMvc.perform(
				MockMvcRequestBuilders
				.post(solrendpoint )
				.contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(solrSchemaDTO)))
		//.andExpect(content().json(expectedCreateResponse400))
		.andExpect(status().isBadRequest());

		// DELETE THE CREATED SCHEMA
		setMockitoSucccessResponseForService();
		solrSchemaDTO = new SolrSchemaDTO(tableName, name, attributes);
		restAMockMvc.perform(
				MockMvcRequestBuilders
				.delete(solrendpoint + "/" + tableName)
				.contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(solrSchemaDTO)))
		.andExpect(status().isOk());
	}

	@Test
	@Transactional
	void testDeleteSchema() throws Exception {
		// DELETE A NON EXISTING SCHEMA
		SolrSchemaDTO solrSchemaDTO = new SolrSchemaDTO(tableName, name, attributes);
		setMockitoBadResponseForService();
		restAMockMvc.perform(MockMvcRequestBuilders.delete(solrendpoint + "/" + tableName)
				.contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(solrSchemaDTO)))
				.andExpect(status().isBadRequest());

		// CREATE SCHEMA
		setMockitoSucccessResponseForService();
		restAMockMvc.perform(MockMvcRequestBuilders.post(solrendpoint)
				.contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(solrSchemaDTO)))
				.andExpect(status().isOk());

		// DELETE THE CREATED SCHEMA
		restAMockMvc.perform(MockMvcRequestBuilders.delete(solrendpoint + "/" + tableName)
				.contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(solrSchemaDTO)))
				.andExpect(status().isOk());

	}

	@Test
	@Transactional
	void testUpdateSchema() throws IOException, Exception {

		// CREATE SCHEMA
		setMockitoSucccessResponseForService();
		SolrSchemaDTO solrSchemaDTO = new SolrSchemaDTO(tableName, name, attributes);
		restAMockMvc.perform(MockMvcRequestBuilders.post(solrendpoint )
				.contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(solrSchemaDTO)))
				.andExpect(status().isOk());

		// update Schema
		solrSchemaDTO = new SolrSchemaDTO(tableName, name, attributes);
		restAMockMvc.perform(MockMvcRequestBuilders.put(solrendpoint + "/" + tableName)
				.contentType(MediaType.APPLICATION_PROBLEM_JSON)
				.content(TestUtil.convertObjectToJsonBytes(solrSchemaDTO))).andExpect(status().isOk());

		// DELETE THE CREATED SCHEMA
		solrSchemaDTO = new SolrSchemaDTO(tableName, name, attributes);
		restAMockMvc.perform(MockMvcRequestBuilders.delete(solrendpoint + "/" + tableName)
				.contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(solrSchemaDTO)))
				.andExpect(status().isOk());
	}

	@Test
	@Transactional
	void testGetSchema() throws Exception {		
		setMockitoSucccessResponseForService();
		restAMockMvc.perform(
				MockMvcRequestBuilders
				.get(solrendpoint + "/" + tableName)
				.accept(MediaType.APPLICATION_JSON))
		//.andExpect(content().json(expectedGetResponse))
		.andExpect(status().isOk());
		
		setMockitoBadResponseForService();
		restAMockMvc.perform(
				MockMvcRequestBuilders
				.get(solrendpoint + "/" + tableName)
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isBadRequest());

		// CREATE SCHEMA
		setMockitoSucccessResponseForService();
		SolrSchemaDTO solrSchemaDTO = new SolrSchemaDTO(tableName, name, attributes);
		restAMockMvc.perform(
				MockMvcRequestBuilders
				.post(solrendpoint)
				.contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(solrSchemaDTO)))
		.andExpect(status().isOk());

		// GET CREATED SCHEMA
		restAMockMvc.perform(
				MockMvcRequestBuilders
				.get(solrendpoint + "/" + tableName)
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk());

		// DELETE THE CREATED SCHEMA
		solrSchemaDTO = new SolrSchemaDTO(tableName, name, attributes);
		restAMockMvc.perform(
				MockMvcRequestBuilders
				.delete(solrendpoint + "/" + tableName )
				.contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(solrSchemaDTO)))
		.andExpect(status().isOk());
	}

}