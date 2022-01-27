package com.searchservice.app.schema;


import com.searchservice.app.IntegrationTest;
import com.searchservice.app.TestUtil;
import com.searchservice.app.domain.dto.schema.FieldDTO;
import com.searchservice.app.domain.dto.schema.SchemaDTO;
import com.searchservice.app.domain.dto.schema.SchemaResponseDTO;
import com.searchservice.app.domain.service.SchemaService;
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
public class SchemaResourceTest {
	
	String solrendpoint = "/api/schema";
	String tableName = "gettingstarted1";

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
	private SchemaService schemaService;

	public void setMockitoSucccessResponseForService() {
		SchemaResponseDTO schemaResponseDTO = new SchemaResponseDTO(tableName, name, attributes);
		schemaResponseDTO.setStatusCode(200);
		Mockito.when(schemaService.create(Mockito.any(), Mockito.any())).thenReturn(schemaResponseDTO);
		Mockito.when(schemaService.delete(Mockito.any())).thenReturn(schemaResponseDTO);
		Mockito.when(schemaService.update(Mockito.any(), Mockito.any())).thenReturn(schemaResponseDTO);
		Mockito.when(schemaService.get(Mockito.any())).thenReturn(schemaResponseDTO);
	}

	public void setMockitoBadResponseForService() {
		SchemaResponseDTO schemaResponseDTO = new SchemaResponseDTO(tableName, name, attributes);
		schemaResponseDTO.setStatusCode(400);
		Mockito.when(schemaService.create(Mockito.any(), Mockito.any())).thenReturn(schemaResponseDTO);
		Mockito.when(schemaService.delete(Mockito.any())).thenReturn(schemaResponseDTO);
		Mockito.when(schemaService.update(Mockito.any(), Mockito.any())).thenReturn(schemaResponseDTO);
		Mockito.when(schemaService.get(Mockito.any())).thenReturn(schemaResponseDTO);
	}

	@Test
	void testCreateSchema() throws Exception {
		SchemaDTO schemaDTO = new SchemaDTO(tableName, name, attributes);
		// CREATE SCHEMA
		setMockitoSucccessResponseForService();
		restAMockMvc.perform(
				MockMvcRequestBuilders
				.post(solrendpoint + "")
				.contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(schemaDTO)))
		.andExpect(status().isOk());

		// CREATE SCHEMA WITH SAME NAME AND TEST
		setMockitoBadResponseForService();
		schemaDTO = new SchemaDTO(tableName, name, attributes);
		restAMockMvc.perform(
				MockMvcRequestBuilders

		//.andExpect(content().json(expectedCreateResponse400));
				.post(solrendpoint )
				.contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(schemaDTO)))
		//.andExpect(content().json(expectedCreateResponse400))
		.andExpect(status().isBadRequest());

		// DELETE THE CREATED SCHEMA
		setMockitoSucccessResponseForService();
		schemaDTO = new SchemaDTO(tableName, name, attributes);
		restAMockMvc.perform(
				MockMvcRequestBuilders
				.delete(solrendpoint + "/" + tableName)
				.contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(schemaDTO)))
		.andExpect(status().isOk());
	}

	@Test
	void testDeleteSchema() throws Exception {
		// DELETE A NON EXISTING SCHEMA
		SchemaDTO schemaDTO = new SchemaDTO(tableName, name, attributes);
		setMockitoBadResponseForService();
		restAMockMvc.perform(MockMvcRequestBuilders.delete(solrendpoint + "/delete/" + tableName + "/" + name)
				.contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(schemaDTO)));

		restAMockMvc.perform(MockMvcRequestBuilders.delete(solrendpoint + "/" + tableName)
				.contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(schemaDTO)))
				.andExpect(status().isBadRequest());


		// CREATE SCHEMA
		setMockitoSucccessResponseForService();
		restAMockMvc.perform(MockMvcRequestBuilders.post(solrendpoint + "")
				.contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(schemaDTO)))
				.andExpect(status().isOk());

		// DELETE THE CREATED SCHEMA
		restAMockMvc.perform(MockMvcRequestBuilders.delete(solrendpoint + "/" + tableName)
				.contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(schemaDTO)))
				.andExpect(status().isOk());

	}

	@Test
	void testUpdateSchema() throws IOException, Exception {

		// CREATE SCHEMA
		setMockitoSucccessResponseForService();
		SchemaDTO schemaDTO = new SchemaDTO(tableName, name, attributes);
		restAMockMvc.perform(MockMvcRequestBuilders.post(solrendpoint + "")
				.contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(schemaDTO)))
				.andExpect(status().isOk());

		// update Schema
		schemaDTO = new SchemaDTO(tableName, name, attributes);
		restAMockMvc.perform(MockMvcRequestBuilders.put(solrendpoint + "/" + tableName)
				.contentType(MediaType.APPLICATION_PROBLEM_JSON)
				.content(TestUtil.convertObjectToJsonBytes(schemaDTO))).andExpect(status().isOk());

		// DELETE THE CREATED SCHEMA
		schemaDTO = new SchemaDTO(tableName, name, attributes);
		restAMockMvc.perform(MockMvcRequestBuilders.delete(solrendpoint + "/" + tableName)
				.contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(schemaDTO)))
				.andExpect(status().isOk());
	}

	@Test
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
		SchemaDTO schemaDTO = new SchemaDTO(tableName, name, attributes);
		restAMockMvc.perform(
				MockMvcRequestBuilders
				.post(solrendpoint + "")
				.contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(schemaDTO)))
		.andExpect(status().isOk());

		// GET CREATED SCHEMA
		restAMockMvc.perform(
				MockMvcRequestBuilders
				.get(solrendpoint + "/" + tableName)
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk());

		// DELETE THE CREATED SCHEMA
		schemaDTO = new SchemaDTO(tableName, name, attributes);
		restAMockMvc.perform(
				MockMvcRequestBuilders
				.delete(solrendpoint + "/" + tableName)
				.contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(schemaDTO)))
		.andExpect(status().isOk());
	}

}
