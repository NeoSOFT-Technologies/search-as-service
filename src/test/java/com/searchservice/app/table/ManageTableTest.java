package com.searchservice.app.table;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.searchservice.app.IntegrationTest;
import com.searchservice.app.TestUtil;
import com.searchservice.app.domain.dto.Response;
import com.searchservice.app.domain.dto.table.GetCapacityPlan;
import com.searchservice.app.domain.dto.table.ManageTable;
import com.searchservice.app.domain.dto.table.SchemaField;
import com.searchservice.app.domain.dto.table.TableSchema;
import com.searchservice.app.domain.service.ManageTableService;
import com.searchservice.app.domain.service.TableDeleteService;

@IntegrationTest
@AutoConfigureMockMvc(addFilters = false)
@Disabled
class ManageTableTest {

	@Value("${base-url.api-endpoint.home}")
	private String apiEndpoint;
	private String tableName = "automatedTestCollection";
	private int tenantId = 101;

	String schemaName = "default-config";
	SchemaField search = new SchemaField("testField6", "string", true, true, false, true, true, false);
	// SchemaFieldDTO[] attributes = { search };
	List<SchemaField> attributes = new ArrayList<>(Arrays.asList(search));
	String expectedGetResponse = "{\n" + "\"tableName\": \"gettingstarted3\",\n" + "\"name\": \"default-config\",\n"
			+ "\"attributes\": [{\n" + "\"name\": \"testField6\",\n" + "\"type\": \"_nest_path_\",\n"
			+ "\"default_\": \"mydefault\",\n" + "\"storable\": false,\n" + "\"filterable\": true,"
			+ "\"required\": true," + "\"sortable\": true,\n" + "\"multiValue\": true,\n" + "}],\n"
			+ "\"statusCode\": 200\n" + "}";
	String expectedCreateResponse400 = "{\n" + "\"tableName\": \"gettingstarted3\",\n"
			+ "\"name\": \"default-config\",\n" + "\"attributes\": [{\n" + "\"name\": \"testField6\",\n"
			+ "\"type\": \"_nest_path_\",\n" + "\"default_\": \"mydefault\",\n" + "\"storable\": false,\n"
			+ "\"filterable\": true," + "\"required\": true," + "\"sortable\": true,\n" + "\"multiValue\": true,\n"
			+ "}],\n" + "\"statusCode\": 400\n" + "}";

	@Autowired
	private MockMvc restAMockMvc;

	@MockBean
	private ManageTableService manageTableService;

	@MockBean
	private TableDeleteService tableDeleteService;

	public void setMockitoSuccessResponseForService() {
		Response responseDTO = new Response();

		responseDTO.setStatusCode(200);
		responseDTO.setMessage("Testing");

		List<String> mockGetTableList = new ArrayList<>();
		mockGetTableList.add("Testing1");
		mockGetTableList.add("Test2");

		Response responseDTOisCollectionExists = new Response();
		responseDTOisCollectionExists.setStatusCode(200);
		responseDTOisCollectionExists.setMessage("true");

		Response getTablesResponseDTO = new Response();
		getTablesResponseDTO.setStatusCode(200);
		getTablesResponseDTO.setMessage("Testing");
		getTablesResponseDTO.setData(mockGetTableList);

		GetCapacityPlan capacityPlanResponseDTO = new GetCapacityPlan();

		Response unodDeleteResponseDTO = new Response();
		unodDeleteResponseDTO.setStatusCode(200);
		unodDeleteResponseDTO.setMessage("Testing");

		Mockito.when(manageTableService.createTableIfNotPresent(Mockito.any())).thenReturn(responseDTO);
		Mockito.when(manageTableService.deleteTable(Mockito.any())).thenReturn(responseDTO);
		Mockito.when(manageTableService.updateTableSchema(Mockito.anyInt(), Mockito.any(), Mockito.any()))
				.thenReturn(responseDTO);
		// Mockito.when(tableService.rename(Mockito.any(),Mockito.any())).thenReturn(responseDTO);

		Mockito.when(manageTableService.getTables(Mockito.anyInt())).thenReturn(getTablesResponseDTO);
//        Mockito.when(manageTableService.getTableSchemaIfPresent(Mockito.any())).thenReturn(tableSchemaResponseDTO);
		Mockito.when(manageTableService.capacityPlans()).thenReturn(capacityPlanResponseDTO);

		Map<Object, Object> finalResponseMap = new HashMap<>();
		finalResponseMap.put("Random message", "Data is returned");
		Mockito.when(manageTableService.getTableDetails(Mockito.any())).thenReturn(finalResponseMap);
		Mockito.when(tableDeleteService.undoTableDeleteRecord(Mockito.anyString())).thenReturn(unodDeleteResponseDTO);
		Mockito.when(tableDeleteService.initializeTableDelete(Mockito.anyInt(), Mockito.anyString()))
				.thenReturn(responseDTO);
		Mockito.when(tableDeleteService.checkTableExistensce(Mockito.anyString())).thenReturn(true);
		Mockito.when(manageTableService.checkIfTableNameisValid(Mockito.anyString())).thenReturn(false);
	}

	public void setMockitoBadResponseForService() {
		Response responseDTO = new Response();
		responseDTO.setStatusCode(400);
		responseDTO.setMessage("Testing");

		Response unodDeleteResponseDTO = new Response();
		unodDeleteResponseDTO.setStatusCode(400);
		unodDeleteResponseDTO.setMessage("Error!");

		Response responseDTOisCollectionExists = new Response();
		responseDTOisCollectionExists.setStatusCode(400);
		responseDTOisCollectionExists.setMessage("Error!");

		Response getTablesResponseDTO = new Response();
		getTablesResponseDTO.setStatusCode(400);
		getTablesResponseDTO.setMessage("Testing");

		GetCapacityPlan capacityPlanResponseDTO = new GetCapacityPlan();

		Mockito.when(manageTableService.createTableIfNotPresent(Mockito.any())).thenReturn(responseDTO);
		Mockito.when(manageTableService.deleteTable(Mockito.any())).thenReturn(responseDTO);
		Mockito.when(manageTableService.updateTableSchema(Mockito.anyInt(), Mockito.any(), Mockito.any()))
				.thenReturn(responseDTO);
		// Mockito.when(tableService.rename(Mockito.any(),Mockito.any())).thenReturn(responseDTO);
		Mockito.when(manageTableService.getTables(Mockito.anyInt())).thenReturn(getTablesResponseDTO);
//        Mockito.when(manageTableService.getTableSchemaIfPresent(Mockito.any())).thenReturn(tableSchemaResponseDTO);
		Mockito.when(manageTableService.capacityPlans()).thenReturn(capacityPlanResponseDTO);

		Map<Object, Object> finalResponseMap = new HashMap<>();
		finalResponseMap.put("Error", "Error connecting to cluster.");
		Mockito.when(manageTableService.getTableDetails(Mockito.any())).thenReturn(finalResponseMap);
		Mockito.when(tableDeleteService.undoTableDeleteRecord(Mockito.anyString())).thenReturn(unodDeleteResponseDTO);
		Mockito.when(tableDeleteService.initializeTableDelete(Mockito.anyInt(), Mockito.anyString()))
				.thenReturn(responseDTO);
		Mockito.when(tableDeleteService.checkTableExistensce(Mockito.anyString())).thenReturn(true);

	}

	public void setMockitoForTableUnderDeletion() {
		Mockito.when(tableDeleteService.isTableUnderDeletion(Mockito.anyString())).thenReturn(true);
	}

	@Test
	void testCreateTable() throws Exception {

		ManageTable createTableDTO = new ManageTable(tableName, "B", "default-schema", attributes);

		// CREATE COLLECTION
		setMockitoSuccessResponseForService();
		Mockito.when(tableDeleteService.isTableUnderDeletion(Mockito.anyString())).thenReturn(false);
		restAMockMvc.perform(MockMvcRequestBuilders.post(apiEndpoint + "/manage/table" + "/" + tenantId)
				.contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(createTableDTO)))
				.andExpect(status().isOk());

		// CREATE COLLECTION WITH SAME NAME AND TEST
		setMockitoBadResponseForService();
		restAMockMvc.perform(MockMvcRequestBuilders.post(apiEndpoint + "/manage/table" + "/" + tenantId)
				.contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(createTableDTO)))
				.andExpect(status().isBadRequest());

		// DELETE THE CREATED COLLECTION
		Response deleteTableDTO = new Response();

		setMockitoSuccessResponseForService();
		restAMockMvc.perform(MockMvcRequestBuilders
				.delete(apiEndpoint + "/manage/table" + "/" + tenantId + "/" + tableName)
				.contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(deleteTableDTO)))
				.andExpect(status().isOk());

		// CREATING COLLECTION WITH INVALID TABLE NAME
		Mockito.when(manageTableService.checkIfTableNameisValid(Mockito.anyString())).thenReturn(true);
		createTableDTO.setTableName("Testing_123");
		restAMockMvc.perform(MockMvcRequestBuilders.post(apiEndpoint + "/manage/table" + "/" + tenantId)
				.contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(createTableDTO)))
				.andExpect(status().isBadRequest());

		// CREATING COLLECTION WITH NAME OF TABLE UNDER DELETION
		Mockito.when(manageTableService.checkIfTableNameisValid(Mockito.anyString())).thenReturn(false);
		Mockito.when(tableDeleteService.isTableUnderDeletion(Mockito.anyString())).thenReturn(true);
		createTableDTO.setTableName("TableTesting");
		restAMockMvc.perform(MockMvcRequestBuilders.post(apiEndpoint + "/manage/table" + "/" + tenantId)
				.contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(createTableDTO)))
				.andExpect(status().isBadRequest());

	}

	@Test
	void testDeleteTable() throws Exception {

		ManageTable createTableForDeletion = new ManageTable(tableName, "B", "default-schema", attributes);

		// DELETE A NON EXISTING COLLECTION
		Response deleteTableResponseDTO = new Response();

		setMockitoBadResponseForService();
		restAMockMvc
				.perform(MockMvcRequestBuilders.delete(apiEndpoint + "/manage/table" + "/" + tenantId + "/" + tableName)
						.contentType(MediaType.APPLICATION_JSON)
						.content(TestUtil.convertObjectToJsonBytes(deleteTableResponseDTO)))
				.andExpect(status().isBadRequest());

		// CREATE COLLECTION
		setMockitoSuccessResponseForService();
		restAMockMvc.perform(MockMvcRequestBuilders.post(apiEndpoint + "/manage/table" + "/" + tenantId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(TestUtil.convertObjectToJsonBytes(createTableForDeletion))).andExpect(status().isOk());

		// DELETE THE CREATED COLLECTION
		setMockitoSuccessResponseForService();
		restAMockMvc
				.perform(MockMvcRequestBuilders.delete(apiEndpoint + "/manage/table" + "/" + tenantId + "/" + tableName)
						.contentType(MediaType.APPLICATION_JSON)
						.content(TestUtil.convertObjectToJsonBytes(deleteTableResponseDTO)))
				.andExpect(status().isOk());

		// TRY TO DELETE TABLE UNDER DELETION
		setMockitoForTableUnderDeletion();
		restAMockMvc
				.perform(MockMvcRequestBuilders.delete(apiEndpoint + "/manage/table" + "/" + tenantId + "/" + tableName)
						.contentType(MediaType.APPLICATION_JSON)
						.content(TestUtil.convertObjectToJsonBytes(deleteTableResponseDTO)))
				.andExpect(status().isBadRequest());
	}

	@Test
	void testUpdateTableSchema() throws Exception {

		// Update Schema
		setMockitoSuccessResponseForService();
		TableSchema schemaDTO = new TableSchema(tableName, schemaName, attributes);
		restAMockMvc.perform(MockMvcRequestBuilders
				.put(apiEndpoint + "/manage/table" + "/" + tenantId + "/" + tableName)
				.contentType(MediaType.APPLICATION_PROBLEM_JSON).content(TestUtil.convertObjectToJsonBytes(schemaDTO)))
				.andExpect(status().isOk());

		// Update Schema for non-existing table
		setMockitoBadResponseForService();
		schemaDTO = new TableSchema(tableName, schemaName, attributes);
		restAMockMvc.perform(MockMvcRequestBuilders
				.put(apiEndpoint + "/manage/table" + "/" + tenantId + "/" + tableName)
				.contentType(MediaType.APPLICATION_PROBLEM_JSON).content(TestUtil.convertObjectToJsonBytes(schemaDTO)))
				.andExpect(status().isBadRequest());

		// Update Schema for Table Under Deletion
		setMockitoForTableUnderDeletion();
		schemaDTO = new TableSchema(tableName, schemaName, attributes);
		restAMockMvc.perform(MockMvcRequestBuilders
				.put(apiEndpoint + "/manage/table" + "/" + tenantId + "/" + tableName)
				.contentType(MediaType.APPLICATION_PROBLEM_JSON).content(TestUtil.convertObjectToJsonBytes(schemaDTO)))
				.andExpect(status().isBadRequest());

	}

	@Test
	void testGetTables() throws Exception {

		setMockitoSuccessResponseForService();
		restAMockMvc.perform(MockMvcRequestBuilders.get(apiEndpoint + "/manage/table/" + tenantId)
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());

	}

	@Test
	void testGetCapacityPlans() throws Exception {

		restAMockMvc.perform(MockMvcRequestBuilders.get(apiEndpoint + "/manage/table" + "/capacity-plans")
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());

	}

	@Test
	void testUndoDeleteTable() throws Exception {

		Response undoDeleteTableDTO = new Response();

		// Testing Undo Table Delete For Valid Table
		setMockitoSuccessResponseForService();
		restAMockMvc.perform(
				MockMvcRequestBuilders.put(apiEndpoint + "/manage/table" + "/restore/" + tenantId + "/" + tableName)
						.contentType(MediaType.APPLICATION_PROBLEM_JSON)
						.content(TestUtil.convertObjectToJsonBytes(undoDeleteTableDTO)))
				.andExpect(status().isOk());

		// Testing Undo Table Delete For Invalid Table
		setMockitoBadResponseForService();
		restAMockMvc
				.perform(MockMvcRequestBuilders
						.put(apiEndpoint + "/manage/table" + "/restore/" + tenantId + "/" + tableName + "0901")
						.contentType(MediaType.APPLICATION_PROBLEM_JSON)
						.content(TestUtil.convertObjectToJsonBytes(undoDeleteTableDTO)))
				.andExpect(status().isBadRequest());
	}

//	@Test
	void testGetTableInfo() throws Exception {
		setMockitoSuccessResponseForService();
		;
		restAMockMvc
				.perform(MockMvcRequestBuilders.get(apiEndpoint + "/manage/table" + "/" + tenantId + "/" + tableName)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

		setMockitoBadResponseForService();
		restAMockMvc
				.perform(MockMvcRequestBuilders.get(apiEndpoint + "/manage/table" + "/" + tenantId + "/" + tableName)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());

		// Accessing Table Under Deletion
		setMockitoForTableUnderDeletion();
		restAMockMvc
				.perform(MockMvcRequestBuilders.get(apiEndpoint + "/manage/table" + "/" + tenantId + "/" + tableName)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}

}
