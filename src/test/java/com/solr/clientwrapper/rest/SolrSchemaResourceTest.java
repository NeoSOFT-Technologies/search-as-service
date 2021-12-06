package com.solr.clientwrapper.rest;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import com.solr.clientwrapper.IntegrationTest;
import com.solr.clientwrapper.domain.dto.solr.SolrFieldDTO;
import com.solr.clientwrapper.domain.dto.solr.SolrSchemaDTO;
import com.solr.clientwrapper.infrastructure.Enum.SolrFieldType;

@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class SolrSchemaResourceTest {

	String solrendpoint = "/schema";
	String tableName = "Mytable";
	String name = "Employee";
	SolrFieldDTO solr = new SolrFieldDTO("mangesh", SolrFieldType.binary, "date", true, true, true, true, true);
	SolrFieldDTO[] attributes = { solr };

	@Autowired
	private MockMvc restAMockMvc;

	@Test
	@Transactional
	void testCreateSchema() throws Exception {
		SolrSchemaDTO solrSchemaDTO = new SolrSchemaDTO(tableName, name, attributes);

		// CREATE SCHEMA
		restAMockMvc.perform(MockMvcRequestBuilders.post(solrendpoint + "/create/")
				.contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(solrSchemaDTO)))
				.andExpect(status().isOk());

		// CREATE SCHEMA WITH SAME NAME AND TEST
		restAMockMvc.perform(MockMvcRequestBuilders.post(solrendpoint + "/create/")
				.contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(solrSchemaDTO)))
				.andExpect(status().isBadRequest());

		// DELETE THE CREATED SCHEMA

		solrSchemaDTO = new SolrSchemaDTO(tableName, name, attributes);
		restAMockMvc.perform(MockMvcRequestBuilders.delete(solrendpoint + "/delete/" + tableName + "/" + name)
				.contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(solrSchemaDTO)))
				.andExpect(status().isOk());
	}

	@Test
	@Transactional
	void testDeleteSchema() throws Exception {
		// DELETE A NON EXISTING SCHEMA
		SolrSchemaDTO solrSchemaDTO = new SolrSchemaDTO(tableName, name, attributes);
		restAMockMvc.perform(MockMvcRequestBuilders.delete(solrendpoint + "/delete/" + tableName + "/" + name)
				.contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(solrSchemaDTO)))
				.andExpect(status().isBadRequest());

		// CREATE SCHEMA
		restAMockMvc.perform(MockMvcRequestBuilders.post(solrendpoint + "/create/")
				.contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(solrSchemaDTO)))
				.andExpect(status().isOk());

		// DELETE THE CREATED SCHEMA
		restAMockMvc.perform(MockMvcRequestBuilders.delete(solrendpoint + "/delete/" + tableName + "/" + name)
				.contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(solrSchemaDTO)))
				.andExpect(status().isOk());

	}

	@Test
	@Transactional
	void testUpdateSchema() throws IOException, Exception {

		// CREATE SCHEMA
		SolrSchemaDTO solrSchemaDTO = new SolrSchemaDTO(tableName, name, attributes);
		restAMockMvc.perform(MockMvcRequestBuilders.post(solrendpoint + "/create/")
				.contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(solrSchemaDTO)))
				.andExpect(status().isOk());

		// update Schema
		solrSchemaDTO = new SolrSchemaDTO(tableName, name, attributes);
		restAMockMvc.perform(MockMvcRequestBuilders.put(solrendpoint + "/update/" + tableName + "/" + name)
				.contentType(MediaType.APPLICATION_PROBLEM_JSON)
				.content(TestUtil.convertObjectToJsonBytes(solrSchemaDTO))).andExpect(status().isOk());

		// DELETE THE CREATED SCHEMA
		solrSchemaDTO = new SolrSchemaDTO(tableName, name, attributes);
		restAMockMvc.perform(MockMvcRequestBuilders.delete(solrendpoint + "/delete/" + tableName + "/" + name)
				.contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(solrSchemaDTO)))
				.andExpect(status().isOk());
	}

	@Test
	@Transactional
	void testGetSchema() throws Exception {
		restAMockMvc.perform(MockMvcRequestBuilders.get(solrendpoint + "/getSchema/" + "nonExistingSchema")
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());

		// CREATE SCHEMA
		SolrSchemaDTO solrSchemaDTO = new SolrSchemaDTO(tableName, name, attributes);
		restAMockMvc.perform(MockMvcRequestBuilders.post(solrendpoint + "/create/")
				.contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(solrSchemaDTO)))
				.andExpect(status().isOk());

		// GET CREATED SCHEMA
		restAMockMvc.perform(MockMvcRequestBuilders.get(solrendpoint + "/getSchema/" + tableName + "/" + name)
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());

		// DELETE THE CREATED SCHEMA
		solrSchemaDTO = new SolrSchemaDTO(tableName, name, attributes);
		restAMockMvc.perform(MockMvcRequestBuilders.delete(solrendpoint + "/delete/" + tableName + "/" + name)
				.contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(solrSchemaDTO)))
				.andExpect(status().isOk());
	}

}
