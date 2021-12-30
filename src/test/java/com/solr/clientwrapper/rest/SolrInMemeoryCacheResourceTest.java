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
import com.solr.clientwrapper.domain.dto.solr.SolrDocumentDTO;
import com.solr.clientwrapper.domain.dto.solr.SolrDocumentResponseDTO;
import com.solr.clientwrapper.domain.dto.solr.SolrFieldDTO;
import com.solr.clientwrapper.domain.service.SolrInMemeoryCacheService;
import com.solr.clientwrapper.infrastructure.Enum.SolrFieldType;
import com.solr.clientwrapper.solrwrapper.TestUtil;

@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class SolrInMemeoryCacheResourceTest {

	String solrendpoint = "/schema";
	String tableName = "gettingstarted1";
	String name = "default-config";
	SolrFieldDTO solr = new SolrFieldDTO("testField6", SolrFieldType._nest_path_, "mydefault", true, true, false, true, true);
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
	private SolrInMemeoryCacheService solrInMemeoryCacheService;

	public void setMockitoSucccessResponseForService() {
		
		SolrDocumentResponseDTO solrDocumentResponseDTO = new SolrDocumentResponseDTO(tableName,name,attributes);
		solrDocumentResponseDTO.setStatusCode(200);
		Mockito.when(solrInMemeoryCacheService.create(Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(solrDocumentResponseDTO);
		Mockito.when(solrInMemeoryCacheService.update(Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(solrDocumentResponseDTO);
		Mockito.when(solrInMemeoryCacheService.delete(Mockito.any(),Mockito.any())).thenReturn(solrDocumentResponseDTO);
		Mockito.when(solrInMemeoryCacheService.get(Mockito.any(),Mockito.any())).thenReturn(solrDocumentResponseDTO);
		
	}

	public void setMockitoBadResponseForService() {
		SolrDocumentResponseDTO solrDocumentResponseDTO = new SolrDocumentResponseDTO(tableName,name,attributes);
		solrDocumentResponseDTO.setStatusCode(400);
		Mockito.when(solrInMemeoryCacheService.create(Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(solrDocumentResponseDTO);
		Mockito.when(solrInMemeoryCacheService.update(Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(solrDocumentResponseDTO);
		Mockito.when(solrInMemeoryCacheService.delete(Mockito.any(),Mockito.any())).thenReturn(solrDocumentResponseDTO);
		Mockito.when(solrInMemeoryCacheService.get(Mockito.any(),Mockito.any())).thenReturn(solrDocumentResponseDTO);
		
	}

	@Test
	@Transactional
	void testCreateSchema() throws Exception {
		
		SolrDocumentDTO solrDocumentDTO = new SolrDocumentDTO(tableName, name, attributes);
		// CREATE SCHEMA
		setMockitoSucccessResponseForService();
		restAMockMvc.perform(
				MockMvcRequestBuilders
				.post(solrendpoint + "/create")
				.contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(solrDocumentDTO)))
		.andExpect(status().isOk());
		
		// CREATE SCHEMA WITH SAME NAME AND TEST
		setMockitoBadResponseForService();
		solrDocumentDTO = new SolrDocumentDTO(tableName, name, attributes);
		restAMockMvc.perform(
				MockMvcRequestBuilders
				.post(solrendpoint + "/create")
				.contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(solrDocumentDTO)));
		
		// DELETE THE CREATED SCHEMA
		setMockitoSucccessResponseForService();
		solrDocumentDTO = new SolrDocumentDTO(tableName, name, attributes);
		restAMockMvc.perform(
				MockMvcRequestBuilders
				.delete(solrendpoint + "/delete/" + tableName + "/" + name)
				.contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(solrDocumentDTO)))
		.andExpect(status().isOk());
	}
	
	@Test
	@Transactional
	void testUpdateSchema() throws IOException, Exception {

		// CREATE SCHEMA
		setMockitoSucccessResponseForService();
		SolrDocumentDTO solrDocumentDTO = new SolrDocumentDTO(tableName, name, attributes);
		restAMockMvc.perform(MockMvcRequestBuilders.post(solrendpoint + "/create")
				.contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(solrDocumentDTO)))
				.andExpect(status().isOk());
		
		// update Schema
		solrDocumentDTO = new SolrDocumentDTO(tableName, name, attributes);
		restAMockMvc.perform(MockMvcRequestBuilders.put(solrendpoint + "/update/" + tableName + "/" + name)
				.contentType(MediaType.APPLICATION_PROBLEM_JSON)
				.content(TestUtil.convertObjectToJsonBytes(solrDocumentDTO))).andExpect(status().isOk());

		// DELETE THE CREATED SCHEMA
		solrDocumentDTO = new SolrDocumentDTO(tableName, name, attributes);
		restAMockMvc.perform(MockMvcRequestBuilders.delete(solrendpoint + "/delete/" + tableName + "/" + name)
				.contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(solrDocumentDTO)))
				.andExpect(status().isOk());
	}


	@Test
	@Transactional
	void testDeleteSchema() throws Exception {
		
		// DELETE A NON EXISTING SCHEMA
		SolrDocumentDTO solrDocumentDTO = new SolrDocumentDTO(tableName, name, attributes);
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
	@Transactional
	void testGetSchema() throws Exception {		
		setMockitoSucccessResponseForService();
		restAMockMvc.perform(
				MockMvcRequestBuilders
				.get(solrendpoint + "/get/" + tableName + "/" + name)
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk());

		// CREATE SCHEMA
		setMockitoSucccessResponseForService();
		SolrDocumentDTO solrDocumentDTO = new SolrDocumentDTO(tableName, name, attributes);
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
		solrDocumentDTO = new SolrDocumentDTO(tableName, name, attributes);
		restAMockMvc.perform(
				MockMvcRequestBuilders
				.delete(solrendpoint + "/delete/" + tableName + "/" + name)
				.contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(solrDocumentDTO)))
		.andExpect(status().isOk());
	}

}