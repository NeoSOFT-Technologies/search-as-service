package com.searchservice.app.rest.table;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.searchservice.app.IntegrationTest;
import com.searchservice.app.TestUtil;
import com.searchservice.app.domain.dto.Response;
import com.searchservice.app.domain.dto.table.CapacityPlanResponse;
import com.searchservice.app.domain.dto.table.CreateTable;
import com.searchservice.app.domain.dto.table.ManageTable;
import com.searchservice.app.domain.dto.table.SchemaField;
import com.searchservice.app.domain.dto.table.TableSchema;
import com.searchservice.app.domain.port.api.TableDeleteServicePort;
import com.searchservice.app.domain.service.ManageTableService;
import com.searchservice.app.domain.service.security.KeycloakUserPermission;
import com.searchservice.app.domain.utils.security.SecurityUtil;

@IntegrationTest
@AutoConfigureMockMvc	//(addFilters = false)
class ManageTableResourceTest {

	@Value("${custom-mock.jwt-token}")
	private String accessToken;
	
	@Value("${base-url.api-endpoint.home}")
	private String apiEndpoint;
	private String tableName = "automatedTestCollection";
	private int tenantId = 101;
	private String tenantName = "TestTenant";

	SchemaField search = new SchemaField("testField6", "string", true, true, false, true, true, false);
	// SchemaFieldDTO[] attributes = { search };
	List<SchemaField> attributes = new ArrayList<>(Arrays.asList(search));
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
	private ManageTableService manageTableService;

	@MockBean
	private TableDeleteServicePort tableDeleteService;

	@MockBean(name = "keycloakAuthService")
	private KeycloakUserPermission keycloakUserPermission;
	
	public void setMockitoSuccessResponseForService() {
		Response responseDTO = new Response();
		TableSchema tableInfoResponseDTO= new TableSchema();
		tableInfoResponseDTO.setStatusCode(200);
		
		responseDTO.setStatusCode(200);
		responseDTO.setMessage("Testing");

		List<String> mockGetTableList = new ArrayList<>();
		mockGetTableList.add("Testing1");
		mockGetTableList.add("Test2");
		mockGetTableList.add("Testing_101");
		
		List<String> mockGetAllTableList = new ArrayList<>();
		mockGetAllTableList.add("Testing1_102");
		mockGetAllTableList.add("Testing_101");

		Response responseDTOisCollectionExists = new Response();
		responseDTOisCollectionExists.setStatusCode(200);
		responseDTOisCollectionExists.setMessage("true");

		Response getTablesResponseDTO = new Response();
		getTablesResponseDTO.setStatusCode(200);
		getTablesResponseDTO.setMessage("Testing");
		getTablesResponseDTO.setData(mockGetAllTableList);
		
		Response getDeletedTablesResponseDTO = new Response();
		getDeletedTablesResponseDTO.setStatusCode(200);
		getDeletedTablesResponseDTO.setMessage("Testing");
		getDeletedTablesResponseDTO.setData(mockGetAllTableList);

		CapacityPlanResponse capacityPlanResponseDTO = new CapacityPlanResponse();

		Mockito.when(manageTableService.createTableIfNotPresent(Mockito.any())).thenReturn(responseDTO);
		Mockito.when(manageTableService.deleteTable(Mockito.any())).thenReturn(responseDTO);
		Mockito.when(manageTableService.updateTableSchema(Mockito.anyInt(), Mockito.any(), Mockito.any()))
				.thenReturn(responseDTO);
		Mockito.when(manageTableService.getTablesForTenant(Mockito.any())).thenReturn(getTablesResponseDTO);
		Mockito.when(manageTableService.capacityPlans()).thenReturn(capacityPlanResponseDTO);
		Mockito.when(manageTableService.getCurrentTableSchema(Mockito.anyInt(), Mockito.anyString())).thenReturn(tableInfoResponseDTO);
		Map<Object, Object> finalResponseMap = new HashMap<>();
		finalResponseMap.put("Random message", "Data is returned");
		Mockito.when(tableDeleteService.initializeTableDelete(Mockito.anyInt(), Mockito.anyString()))
				.thenReturn(responseDTO);
		Mockito.when(manageTableService.checkIfTableNameisValid(Mockito.anyString())).thenReturn(false);
		Mockito.when(tableDeleteService.isTableUnderDeletion(Mockito.anyString())).thenReturn(false);
		Mockito.when(manageTableService.isTableExists(Mockito.anyString())).thenReturn(true);
		Mockito.when(manageTableService.getAllTables(Mockito.anyInt(), Mockito.anyInt()))
				.thenReturn(getTablesResponseDTO);
		Mockito.when(tableDeleteService.getTablesUnderDeletion(Mockito.anyBoolean()))
				.thenReturn(getDeletedTablesResponseDTO);
		Mockito.when(tableDeleteService.getTablesUnderDeletionPagination(Mockito.anyBoolean(), Mockito.anyInt(),
				Mockito.anyInt())).thenReturn(getDeletedTablesResponseDTO);
		Mockito.when(tableDeleteService.getTablesUnderDeletionForTenant(Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt()))
				.thenReturn(getDeletedTablesResponseDTO);
	}

	public void setMockitoBadResponseForService() {
		Response responseDTO = new Response();
		responseDTO.setStatusCode(400);
		responseDTO.setMessage("Testing");
		TableSchema tableInfoResponseDTO= new TableSchema();
		tableInfoResponseDTO.setStatusCode(400);
		Response unodDeleteResponseDTO = new Response();
		unodDeleteResponseDTO.setStatusCode(400);
		unodDeleteResponseDTO.setMessage("Error!");

		Response responseDTOisCollectionExists = new Response();
		responseDTOisCollectionExists.setStatusCode(400);
		responseDTOisCollectionExists.setMessage("Error!");

		Response getTablesResponseDTO = new Response();
		getTablesResponseDTO.setStatusCode(400);
		getTablesResponseDTO.setMessage("Testing");
		
		Response getDeletedTablesResponseDTO = new Response();
		getDeletedTablesResponseDTO.setStatusCode(400);
		getDeletedTablesResponseDTO.setMessage("Testing");

		CapacityPlanResponse capacityPlanResponseDTO = new CapacityPlanResponse();
		Mockito.when(manageTableService.getCurrentTableSchema(Mockito.anyInt(), Mockito.anyString())).thenReturn(tableInfoResponseDTO);
		Mockito.when(manageTableService.createTableIfNotPresent(Mockito.any())).thenReturn(responseDTO);
		Mockito.when(manageTableService.deleteTable(Mockito.any())).thenReturn(responseDTO);
		Mockito.when(manageTableService.updateTableSchema(Mockito.anyInt(), Mockito.any(), Mockito.any()))
				.thenReturn(responseDTO);
		Mockito.when(manageTableService.getTablesForTenant(Mockito.any())).thenReturn(getTablesResponseDTO);
		Mockito.when(manageTableService.capacityPlans()).thenReturn(capacityPlanResponseDTO);

		Map<Object, Object> finalResponseMap = new HashMap<>();
		finalResponseMap.put("Error", "Error connecting to cluster.");
		Mockito.when(tableDeleteService.undoTableDeleteRecord(Mockito.anyString())).thenReturn(unodDeleteResponseDTO);
		Mockito.when(tableDeleteService.initializeTableDelete(Mockito.anyInt(), Mockito.anyString()))
				.thenReturn(responseDTO);
		Mockito.when(manageTableService.isColumnNameValid(Mockito.anyList())).thenReturn(false);
		Mockito.when(manageTableService.getAllTables(Mockito.anyInt(), Mockito.anyInt()))
				.thenReturn(getTablesResponseDTO);
		Mockito.when(tableDeleteService.getTablesUnderDeletion(Mockito.anyBoolean()))
				.thenReturn(getDeletedTablesResponseDTO);
		Mockito.when(tableDeleteService.getTablesUnderDeletionPagination(Mockito.anyBoolean(), Mockito.anyInt(),
				Mockito.anyInt())).thenReturn(getDeletedTablesResponseDTO);
		Mockito.when(tableDeleteService.getTablesUnderDeletionForTenant(Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt()))
				.thenReturn(getDeletedTablesResponseDTO);
	}


	public void mockPreAuthorizedService() {
		when(keycloakUserPermission.isViewPermissionEnabled()).thenReturn(true);
		when(keycloakUserPermission.isCreatePermissionEnabled()).thenReturn(true);
		when(keycloakUserPermission.isEditPermissionEnabled()).thenReturn(true);
		when(keycloakUserPermission.isDeletePermissionEnabled()).thenReturn(true);
	}

	
	@Test
	void testGetCapacityPlans() throws Exception {

		try (MockedStatic<SecurityUtil> mockedUtility = Mockito.mockStatic(SecurityUtil.class)) {
			mockedUtility.when(
					() -> SecurityUtil.validate(Mockito.anyString(), Mockito.anyString()))
			.thenReturn(true);
			mockedUtility.when(
					() -> SecurityUtil.getTokenFromRequestHeader(
							Mockito.any(), Mockito.any(), Mockito.any()))
			.thenReturn(accessToken);

			mockPreAuthorizedService();
			restAMockMvc.perform(MockMvcRequestBuilders.get("/api/v1" + "/manage/table" + "/capacity-plans")
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken).accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk());
		}
	}

	@Test
	void testGetTablesWithTenant() throws Exception {
		try (MockedStatic<SecurityUtil> mockedUtility = Mockito.mockStatic(SecurityUtil.class)) {
			mockedUtility.when(
					() -> SecurityUtil.validate(Mockito.anyString(), Mockito.anyString()))
			.thenReturn(true);
			mockedUtility.when(
					() -> SecurityUtil.getTokenFromRequestHeader(
							Mockito.any(), Mockito.any(), Mockito.any()))
			.thenReturn(accessToken);

			mockPreAuthorizedService();
			setMockitoSuccessResponseForService();
			restAMockMvc.perform(MockMvcRequestBuilders.get(apiEndpoint + "/manage/table/" + "/?tenantName=" + tenantName)
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken).accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk());

			setMockitoBadResponseForService();
			restAMockMvc.perform(MockMvcRequestBuilders.get(apiEndpoint + "/manage/table/" + "/?tenantId=" + tenantId)
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken).accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isBadRequest());

			Mockito.when(manageTableService.getTablesForTenant(Mockito.any())).thenReturn(null);
			restAMockMvc.perform(MockMvcRequestBuilders.get(apiEndpoint + "/manage/table/" + "/?tenantId=" + tenantId)
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken).accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isBadRequest());
		}
	}
	
	@Test
	void testGetTablesWithTenantWithPagination() throws Exception {
		try (MockedStatic<SecurityUtil> mockedUtility = Mockito.mockStatic(SecurityUtil.class)) {
			mockedUtility.when(
					() -> SecurityUtil.validate(Mockito.anyString(), Mockito.anyString()))
			.thenReturn(true);
			mockedUtility.when(
					() -> SecurityUtil.getTokenFromRequestHeader(
							Mockito.any(), Mockito.any(), Mockito.any()))
			.thenReturn(accessToken);

			mockPreAuthorizedService();
			setMockitoSuccessResponseForService();
			restAMockMvc.perform(MockMvcRequestBuilders.get(apiEndpoint + "/manage/table/tablesList" + "/?tenantId=" + tenantId
					+"&pageNumber=1&pageSize=5")
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken).accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk());

			setMockitoBadResponseForService();
			restAMockMvc.perform(MockMvcRequestBuilders.get(apiEndpoint + "/manage/table/tablesList" + "/?tenantId=" + tenantId
					+"&pageNumber=1&pageSize=5")
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken).accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isBadRequest());

			Mockito.when(manageTableService.getTablesForTenant(Mockito.any())).thenReturn(null);
			restAMockMvc.perform(MockMvcRequestBuilders.get(apiEndpoint + "/manage/table/tablesList" + "/?tenantId=" + tenantId
					+"&pageNumber=1&pageSize=5")
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken).accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isBadRequest());
		}
	}
	
	@Test
	void testGetAllTables() throws Exception {
		try (MockedStatic<SecurityUtil> mockedUtility = Mockito.mockStatic(SecurityUtil.class)) {
			mockedUtility.when(
					() -> SecurityUtil.validate(Mockito.anyString(), Mockito.anyString()))
			.thenReturn(true);
			mockedUtility.when(
					() -> SecurityUtil.getTokenFromRequestHeader(
							Mockito.any(), Mockito.any(), Mockito.any()))
			.thenReturn(accessToken);

			mockPreAuthorizedService();
			setMockitoSuccessResponseForService();
			restAMockMvc.perform(MockMvcRequestBuilders
					.get(apiEndpoint + "/manage/table" + "/all-tables" + "?pageNumber=1&pageSize=5")
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken).accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk());

			setMockitoBadResponseForService();
			restAMockMvc.perform(MockMvcRequestBuilders
					.get(apiEndpoint + "/manage/table" + "/all-tables" + "?pageNumber=1&pageSize=5")
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken).accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isBadRequest());

			Mockito.when(manageTableService.getAllTables(Mockito.anyInt(), Mockito.anyInt())).thenReturn(null);
			restAMockMvc.perform(MockMvcRequestBuilders
					.get(apiEndpoint + "/manage/table" + "/all-tables" + "?pageNumber=1&pageSize=5")
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken).accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isBadRequest());

		}
	}

	@Test
	void testGetAllTablesUnderDeletion() throws Exception {
		try (MockedStatic<SecurityUtil> mockedUtility = Mockito.mockStatic(SecurityUtil.class)) {
			mockedUtility.when(
					() -> SecurityUtil.validate(Mockito.anyString(), Mockito.anyString()))
			.thenReturn(true);
			mockedUtility.when(
					() -> SecurityUtil.getTokenFromRequestHeader(
							Mockito.any(), Mockito.any(), Mockito.any()))
			.thenReturn(accessToken);

			mockPreAuthorizedService();
			setMockitoSuccessResponseForService();
			restAMockMvc.perform(MockMvcRequestBuilders
					.get(apiEndpoint + "/manage/table" + "/deletion/all-tables" + "?pageNumber=1&pageSize=5")
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken).accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk());

			setMockitoBadResponseForService();
			restAMockMvc.perform(MockMvcRequestBuilders
					.get(apiEndpoint + "/manage/table/" + "/deletion/all-tables" + "?pageNumber=1&pageSize=5")
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
					.accept(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());

			Mockito.when(tableDeleteService.getTablesUnderDeletion(Mockito.anyBoolean())).thenReturn(null);
			restAMockMvc.perform(MockMvcRequestBuilders
					.get(apiEndpoint + "/manage/table/" + "/deletion/all-tables" + "?pageNumber=1&pageSize=5")
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken).accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isBadRequest());
		}
	}
	
	@Test
	void testGetAllTablesUnderDeletionByTenant() throws Exception {
		try (MockedStatic<SecurityUtil> mockedUtility = Mockito.mockStatic(SecurityUtil.class)) {
			mockedUtility.when(
					() -> SecurityUtil.validate(Mockito.anyString(), Mockito.anyString()))
			.thenReturn(true);
			mockedUtility.when(
					() -> SecurityUtil.getTokenFromRequestHeader(
							Mockito.any(), Mockito.any(), Mockito.any()))
			.thenReturn(accessToken);

			mockPreAuthorizedService();
			setMockitoSuccessResponseForService();
			restAMockMvc.perform(MockMvcRequestBuilders
					.get(apiEndpoint + "/manage/table/" + "/deletion" + "?tenantName=" + tenantName +"&pageNumber=1&pageSize=5")
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken).accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk());

			setMockitoBadResponseForService();
			restAMockMvc.perform(MockMvcRequestBuilders
					.get(apiEndpoint + "/manage/table/" + "/deletion" + "?tenantName=" + tenantName
							+ "&pageNumber=1&pageSize=5")
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken).accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isBadRequest());

			Mockito.when(tableDeleteService.getTablesUnderDeletionForTenant(Mockito.anyString(), Mockito.anyInt(),
					Mockito.anyInt())).thenReturn(null);
			restAMockMvc.perform(MockMvcRequestBuilders
					.get(apiEndpoint + "/manage/table/" + "/deletion" + "?tenantId=" + tenantId +"&pageNumber=1&pageSize=5")
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken).accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isBadRequest());
		}
	}

	@Test
	void testGetTableInfo() throws Exception {
		try (MockedStatic<SecurityUtil> mockedUtility = Mockito.mockStatic(SecurityUtil.class)) {
			mockedUtility.when(
					() -> SecurityUtil.validate(Mockito.anyString(), Mockito.anyString()))
			.thenReturn(true);
			mockedUtility.when(
					() -> SecurityUtil.getTokenFromRequestHeader(
							Mockito.any(), Mockito.any(), Mockito.any()))
			.thenReturn(accessToken);

			mockPreAuthorizedService();
			setMockitoSuccessResponseForService();
			restAMockMvc.perform(MockMvcRequestBuilders
					.get(apiEndpoint + "/manage/table" + "/" + tableName + "/?tenantId=" + tenantId)
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken).accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk());

			setMockitoBadResponseForService();
			restAMockMvc.perform(MockMvcRequestBuilders
					.get(apiEndpoint + "/manage/table" + "/" + tableName + "/?tenantId=" + tenantId)
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken).accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isBadRequest());

			Mockito.when(manageTableService.getCurrentTableSchema(Mockito.anyInt(), Mockito.anyString()))
					.thenReturn(null);
			restAMockMvc.perform(MockMvcRequestBuilders
					.get(apiEndpoint + "/manage/table" + "/" + tableName + "/?tenantId=" + tenantId)
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken).accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isBadRequest());

			// Accessing Table Under Deletion
			setMockitoForTableUnderDeletion();
			restAMockMvc.perform(MockMvcRequestBuilders
					.get(apiEndpoint + "/manage/table" + "/" + tableName + "/?tenantId=" + tenantId)
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken).accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isBadRequest());
		}

	}

	@Test
	void testCreateTable() throws Exception {
		try (MockedStatic<SecurityUtil> mockedUtility = Mockito.mockStatic(SecurityUtil.class)) {
			mockedUtility.when(
					() -> SecurityUtil.validate(Mockito.anyString(), Mockito.anyString()))
			.thenReturn(true);
			mockedUtility.when(
					() -> SecurityUtil.getTokenFromRequestHeader(
							Mockito.any(), Mockito.any(), Mockito.any()))
			.thenReturn(accessToken);

			mockPreAuthorizedService();
			CreateTable createTableDTO = new CreateTable(tableName, "B", attributes);

			// CREATE COLLECTION
			setMockitoSuccessResponseForService();
			Mockito.when(tableDeleteService.isTableUnderDeletion(Mockito.anyString())).thenReturn(false);
			restAMockMvc.perform(MockMvcRequestBuilders
					.post(apiEndpoint + "/manage/table" + "/" + "/?tenantId=" + tenantId)
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken).contentType(MediaType.APPLICATION_JSON)
					.content(TestUtil.convertObjectToJsonBytes(createTableDTO))).andExpect(status().isOk());

			// CREATE COLLECTION WITH SAME NAME AND TEST
			setMockitoBadResponseForService();
			restAMockMvc
					.perform(MockMvcRequestBuilders.post(apiEndpoint + "/manage/table" + "/" + "/?tenantId=" + tenantId)
							.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
							.contentType(MediaType.APPLICATION_JSON)
							.content(TestUtil.convertObjectToJsonBytes(createTableDTO)))
					.andExpect(status().isBadRequest());

			// DELETE THE CREATED COLLECTION
			Response deleteTableDTO = new Response();
			setMockitoSuccessResponseForService();
			restAMockMvc.perform(MockMvcRequestBuilders
					.delete(apiEndpoint + "/manage/table" + "/" + "/" + tableName + "/?tenantId=" + tenantId)
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken).contentType(MediaType.APPLICATION_JSON)
					.content(TestUtil.convertObjectToJsonBytes(deleteTableDTO))).andExpect(status().isOk());
			
		}
	}

	@Test
	void testDeleteTable() throws Exception {
		try (MockedStatic<SecurityUtil> mockedUtility = Mockito.mockStatic(SecurityUtil.class)) {
			mockedUtility.when(
					() -> SecurityUtil.validate(Mockito.anyString(), Mockito.anyString()))
			.thenReturn(true);
			mockedUtility.when(
					() -> SecurityUtil.getTokenFromRequestHeader(
							Mockito.any(), Mockito.any(), Mockito.any()))
			.thenReturn(accessToken);

			mockPreAuthorizedService();
			CreateTable createTableForDeletion = new CreateTable(tableName, "B", attributes);

			// DELETE A NON EXISTING COLLECTION
			Response deleteTableResponseDTO = new Response();

			setMockitoBadResponseForService();
			restAMockMvc.perform(MockMvcRequestBuilders
					.delete(apiEndpoint + "/manage/table" + "/" + tableName + "/?tenantId=" + tenantId)
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken).contentType(MediaType.APPLICATION_JSON)
					.content(TestUtil.convertObjectToJsonBytes(deleteTableResponseDTO)))
					.andExpect(status().isBadRequest());

			// CREATE COLLECTION
			setMockitoSuccessResponseForService();
			restAMockMvc.perform(MockMvcRequestBuilders.post(apiEndpoint + "/manage/table" + "/?tenantId=" + tenantId)
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken).contentType(MediaType.APPLICATION_JSON)
					.content(TestUtil.convertObjectToJsonBytes(createTableForDeletion))).andExpect(status().isOk());

			// DELETE THE CREATED COLLECTION
			setMockitoSuccessResponseForService();
			restAMockMvc.perform(MockMvcRequestBuilders
					.delete(apiEndpoint + "/manage/table" + "/" + tableName + "/?tenantId=" + tenantId)
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken).contentType(MediaType.APPLICATION_JSON)
					.content(TestUtil.convertObjectToJsonBytes(deleteTableResponseDTO))).andExpect(status().isOk());

		}
	}

	@Test
	void testUpdateTableSchema() throws Exception {
		try (MockedStatic<SecurityUtil> mockedUtility = Mockito.mockStatic(SecurityUtil.class)) {
			mockedUtility.when(
					() -> SecurityUtil.validate(Mockito.anyString(), Mockito.anyString()))
			.thenReturn(true);
			mockedUtility.when(
					() -> SecurityUtil.getTokenFromRequestHeader(
							Mockito.any(), Mockito.any(), Mockito.any()))
			.thenReturn(accessToken);

			mockPreAuthorizedService();
			// Update Schema
			setMockitoSuccessResponseForService();
			ManageTable schemaDTO = new ManageTable(tableName, attributes);
			restAMockMvc.perform(MockMvcRequestBuilders
					.put(apiEndpoint + "/manage/table" + "/" + tableName + "/?tenantId=" + tenantId)
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_PROBLEM_JSON)
					.content(TestUtil.convertObjectToJsonBytes(schemaDTO))).andExpect(status().isOk());

			setMockitoBadResponseForService();
			restAMockMvc.perform(MockMvcRequestBuilders
					.put(apiEndpoint + "/manage/table" + "/" + tableName + "/?tenantId=" + tenantId)
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_PROBLEM_JSON)
					.content(TestUtil.convertObjectToJsonBytes(schemaDTO))).andExpect(status().isBadRequest());

			// Update Schema for non-existing table
			setMockitoForTableNotExist();
			schemaDTO = new ManageTable(tableName, attributes);
			restAMockMvc.perform(MockMvcRequestBuilders
					.put(apiEndpoint + "/manage/table" + "/" + tableName + "/?tenantId=" + tenantId)
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_PROBLEM_JSON)
					.content(TestUtil.convertObjectToJsonBytes(schemaDTO))).andExpect(status().isBadRequest());

			// Update Schema for Table Under Deletion
			Mockito.when(manageTableService.isTableExists(Mockito.anyString())).thenReturn(true);
			Mockito.when(tableDeleteService.isTableUnderDeletion(Mockito.anyString())).thenReturn(true);
			schemaDTO = new ManageTable(tableName, attributes);
			restAMockMvc.perform(MockMvcRequestBuilders
					.put(apiEndpoint + "/manage/table" + "/" + tableName + "/?tenantId=" + tenantId)
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_PROBLEM_JSON)
					.content(TestUtil.convertObjectToJsonBytes(schemaDTO))).andExpect(status().isBadRequest());
		}
	}

	public void setMockitoForTableUnderDeletion() {
		Mockito.when(tableDeleteService.isTableUnderDeletion(Mockito.anyString())).thenReturn(true);
		Response unodDeleteResponseDTO = new Response();
		unodDeleteResponseDTO.setStatusCode(200);
		unodDeleteResponseDTO.setMessage("Testing");
		Mockito.when(tableDeleteService.undoTableDeleteRecord(Mockito.anyString())).thenReturn(unodDeleteResponseDTO);
	}

	public void setMockitoForTableNotUnderDeletion() {
		Mockito.when(tableDeleteService.isTableUnderDeletion(Mockito.anyString())).thenReturn(false);
	}

	public void setMockitoForTableNotExist() {
		Mockito.when(manageTableService.isTableExists(Mockito.anyString())).thenReturn(false);
	}

	@Test
	void testUndoDeleteTable() throws Exception {
		try (MockedStatic<SecurityUtil> mockedUtility = Mockito.mockStatic(SecurityUtil.class)) {
			mockedUtility.when(
					() -> SecurityUtil.validate(Mockito.anyString(), Mockito.anyString()))
			.thenReturn(true);
			mockedUtility.when(
					() -> SecurityUtil.getTokenFromRequestHeader(
							Mockito.any(), Mockito.any(), Mockito.any()))
			.thenReturn(accessToken);

			mockPreAuthorizedService();
			Response undoDeleteTableDTO = new Response();

			// Testing Undo Table Delete For Valid Table
			setMockitoForTableUnderDeletion();
			restAMockMvc.perform(MockMvcRequestBuilders
					.put(apiEndpoint + "/manage/table" + "/restore/" + "/" + tableName + "?tenantId=" + tenantId)
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_PROBLEM_JSON)
					.content(TestUtil.convertObjectToJsonBytes(undoDeleteTableDTO))).andExpect(status().isOk());

			// Testing Undo Table Delete For Invalid Table
			setMockitoBadResponseForService();
			restAMockMvc
					.perform(MockMvcRequestBuilders
							.put(apiEndpoint + "/manage/table" + "/restore" + "/" + tableName + "?tenantId=" + tenantId)
							.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
							.contentType(MediaType.APPLICATION_PROBLEM_JSON)
							.content(TestUtil.convertObjectToJsonBytes(undoDeleteTableDTO)))
					.andExpect(status().isBadRequest());

			setMockitoForTableNotUnderDeletion();
			restAMockMvc
					.perform(MockMvcRequestBuilders
							.put(apiEndpoint + "/manage/table" + "/restore" + "/" + tableName + "?tenantId=" + tenantId)
							.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
							.contentType(MediaType.APPLICATION_PROBLEM_JSON)
							.content(TestUtil.convertObjectToJsonBytes(undoDeleteTableDTO)))
					.andExpect(status().isBadRequest());
		}
	}

}
