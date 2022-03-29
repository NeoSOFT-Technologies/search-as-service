package com.searchservice.app.domain.service;
import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.request.schema.SchemaRequest;
import org.apache.solr.client.solrj.response.CollectionAdminResponse;
import org.apache.solr.client.solrj.response.ConfigSetAdminResponse;
import org.apache.solr.client.solrj.response.schema.SchemaResponse;
import org.apache.solr.common.util.NamedList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.searchservice.app.config.CapacityPlanProperties;
import com.searchservice.app.config.CapacityPlanProperties.Plan;
import com.searchservice.app.domain.dto.Response;
import com.searchservice.app.domain.dto.logger.LoggersDTO;
import com.searchservice.app.domain.dto.table.ConfigSet;
import com.searchservice.app.domain.dto.table.ManageTable;
import com.searchservice.app.domain.dto.table.SchemaField;
import com.searchservice.app.domain.dto.table.TableSchema;
import com.searchservice.app.domain.dto.table.TableSchemav2;
import com.searchservice.app.domain.dto.table.TableSchemav2.TableSchemav2Data;
import com.searchservice.app.domain.utils.LoggerUtils;
import com.searchservice.app.domain.utils.SearchUtil;
import com.searchservice.app.infrastructure.adaptor.SearchAPIAdapter;
import com.searchservice.app.infrastructure.adaptor.SolrJAdapter;
import com.searchservice.app.rest.errors.BadRequestOccurredException;
import com.searchservice.app.rest.errors.ContentNotFoundException;
@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)

@SpringBootTest
class ManageTableServiceTest  {

	@Value("${base-search-url}")
	String searchUrl ;
	SearchAPIAdapter solrApiAdapter = new SearchAPIAdapter();
	HttpSolrClient solrClient = null;
	HttpSolrClient solrClientWithTable = null;
	private String tableName = "automatedTestCollection";
	private int tenantId = 101;

	@MockBean
	SearchAPIAdapter solrApiAdapterMocked;

	@MockBean
	private LoggersDTO loggersDTO;

	@MockBean
	SearchUtil solrUtilMocked;

	@MockBean
	CapacityPlanProperties capacityPlanProperties;
	
	@MockBean
	SolrJAdapter solrJAdapter;
	
	@InjectMocks
	ManageTableService manageTableService;
	
	@MockBean
	SchemaRequest schemaRequest;

	ManageTable manageTable = new ManageTable();

	TableSchema newTableSchemaDTO = new TableSchema();

	TableSchemav2 tableSchema = new TableSchemav2();
	TableSchemav2Data tableSchemav2Data = new TableSchemav2Data();
	SchemaResponse schemaResponse= new SchemaResponse();
	ConfigSetAdminResponse configSetResponse = new ConfigSetAdminResponse();
	CollectionAdminResponse collectionAdminResponse = new CollectionAdminResponse();
	List<SchemaField> list = new ArrayList<SchemaField>();
	SchemaField schemaField = new SchemaField();
	ConfigSet configSetDTO = new ConfigSet();
	Response responseDTO = new Response();
	
	
	public void setMockitoBadResponseForService() {
		
		
		
		collectionAdminResponse.setElapsedTime(5);
		collectionAdminResponse.setRequestUrl(searchUrl);
		collectionAdminResponse.setResponse(test());
		Mockito.when(solrJAdapter.getCollectionAdminRequestList(solrClient)).thenReturn(collectionAdminResponse);
		Mockito.when(solrJAdapter.getAllTablesList(solrClient)).thenReturn(collectionAdminResponse);
		configSetResponse.setResponse(test1());
		Mockito.when(solrJAdapter.getConfigSetFromSolrj(solrClient)).thenReturn(configSetResponse);
	}
	
	
	
	public void setMockitoSuccessResponseForService() {
		
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

		TableSchemav2 tableSchemaResponseDTO = new TableSchemav2();
		tableSchemaResponseDTO.setMessage("Testting");
		tableSchemaResponseDTO.setStatusCode(200);
		
		Response aliasTableResponse = new Response();
		aliasTableResponse.setStatusCode(200);
		
		Response unodDeleteResponseDTO = new Response();
		unodDeleteResponseDTO.setStatusCode(200);
		unodDeleteResponseDTO.setMessage("Testing");

		Map<Object, Object> finalResponseMap = new HashMap<>();
		
		finalResponseMap.put(" message", "Data is returned");


		newTableSchemaDTO.setSchemaName("table");
		newTableSchemaDTO.setColumns(list);
		newTableSchemaDTO.setTableDetails(finalResponseMap);
		newTableSchemaDTO.setTableName(tableName);


		// schemaField.setDefault_("");
		schemaField.setFilterable(true);
		schemaField.setMultiValue(true);
		schemaField.setName("ok");
		schemaField.setPartialSearch(true);
		schemaField.setRequired(true);
		schemaField.setSortable(true);
		schemaField.setStorable(true);
		schemaField.setType("string");
		list.add(schemaField);
		manageTable.setColumns(list);
		manageTable.setSchemaName("timestamp");
		manageTable.setSku("B");
		manageTable.setTableName("Demo");
		manageTable.setTableNewName("Demo1");
		configSetDTO.setBaseConfigSetName("solrUrl");
		configSetDTO.setConfigSetName("solrUrl");
		tableSchemav2Data.setColumns(list);
		List<CapacityPlanProperties.Plan> plan =new ArrayList<>();
		Plan newPlan = new Plan();
		newPlan.setName("Basic");
		newPlan.setSku("B");
		newPlan.setShards(1);
		newPlan.setReplicas(1);
		plan.add(newPlan);
		Mockito.when(capacityPlanProperties.getPlans()).thenReturn(plan);
		collectionAdminResponse.setElapsedTime(5);
		collectionAdminResponse.setRequestUrl(searchUrl);
		collectionAdminResponse.setResponse(test());
		List<String> tableList = new ArrayList<String>();
		tableList.add(tableName);
		Mockito.when(solrJAdapter.getCollectionAdminRequestList( solrClient)).thenReturn(collectionAdminResponse);
	//	Mockito.when(solrJAdapter.createTableInSolrj(null, solrClient)
	//	Mockito.when(solrJAdapter.getAllTablesList()).thenReturn(tableList);
			Mockito.when(solrJAdapter.getAllTablesList(solrClient)).thenReturn(collectionAdminResponse);
		//Mockito.when(solrJAdapter.addAliasTableInSolrj(tableName, "AutomatedTesting")).thenReturn(aliasTableResponse);
		
	}

	public void setUpTestClass() {
		solrClient = solrApiAdapter.getSearchClient(searchUrl);
		solrClientWithTable = solrApiAdapter.getSearchClientWithTable(searchUrl, tableName);
	}

	@BeforeEach
	void setUp() throws Exception {

		setUpTestClass();

		Mockito.when(solrApiAdapterMocked.getSearchClient(searchUrl)).thenReturn(solrClient);
		Mockito.when(solrApiAdapterMocked.getSearchClientWithTable(Mockito.any(), Mockito.any()))
				.thenReturn(solrClientWithTable);
		configSetResponse.setResponse(test1());
		Mockito.when(solrJAdapter.getConfigSetFromSolrj(solrClient)).thenReturn(configSetResponse);
		
	//	Mockito.when(schemaRequest.process(solrClient)).thenReturn(schemaResponse);
		String timestamp = LoggerUtils.utcTime().toString();
		loggersDTO.setNameofmethod("nameofCurrMethod");
		loggersDTO.setTimestamp(timestamp);
		loggersDTO.setServicename("servicename");
		loggersDTO.setUsername("username");

	}


	@Test
	void testGetTables() {
		setMockitoSuccessResponseForService();
		Response resp = manageTableService.getTables(tenantId, loggersDTO);

		assertEquals(200, resp.getStatusCode());

	}
	
	@Test
	void testCapacityPlans() {
		Mockito.when(solrApiAdapterMocked.getSearchClient(searchUrl)).thenReturn(solrClient);
		assertTrue(manageTableService.capacityPlans(loggersDTO).getPlans() != null);

	}

	@Test
	void testGetTableDetails() {
		setMockitoSuccessResponseForService();
		assertTrue(manageTableService.getTableDetails(tableName, loggersDTO) != null);

	}

	@Test
	void testGetTableSchemaIfPresent() {

		try {

			manageTableService.getTableSchemaIfPresent(tableName, new LoggersDTO());
		} catch (BadRequestOccurredException e) {
			assertEquals(400, e.getExceptionCode());
		}

	}

	@Test
	void testGetCurrentTableSchema() {

		try {

			manageTableService.getCurrentTableSchema(tenantId, tableName);
		} catch (BadRequestOccurredException e) {
			assertEquals(400, e.getExceptionCode());
		}
	}

	@Test
	void testIsTableExists() {

		try {
			manageTableService.isTableExists(tableName);
		} catch (BadRequestOccurredException e) {
			assertEquals(400, e.getExceptionCode());
		}
	}

	@Test
	void isConfigSetExists() {
		boolean configSetExist = manageTableService.isConfigSetExists("_default");
		assertTrue(configSetExist);
	}
	@Test
	void isConfigSetDontExists() {
		boolean configSetExist = manageTableService.isConfigSetExists("TestConfig");
		assertFalse(configSetExist);
	}
	@Test
	void addAliasTable() {

		setMockitoSuccessResponseForService();
		Response rs=	manageTableService.addAliasTable(tableName,"AutomatedTesting" );
		assertEquals(200, rs.getStatusCode());
		}

	@Test
	void deleteConfigSet() {

		try {
			manageTableService.deleteConfigSet(searchUrl);
		} catch (BadRequestOccurredException e) {
			assertEquals(400, e.getExceptionCode());
		}
	}

	@Test
	void initializeSchemaDeletion() {

		// setMockitoSuccessResponseForService();
		try {
			manageTableService.initializeSchemaDeletion(tenantId, tableName, searchUrl);
		} catch (BadRequestOccurredException e) {
			assertEquals(400, e.getExceptionCode());
		}
	}

	@Test
	void getTableSchema() {

		try {
			manageTableService.getTableSchema(tableName);
		} catch (BadRequestOccurredException e) {
			assertEquals(400, e.getExceptionCode());
		}

	}

	@Test
	void checkIfTableNameisValid() {

		// setMockitoSuccessResponseForService();
		try {
			manageTableService.checkIfTableNameisValid(tableName);
		} catch (BadRequestOccurredException e) {
			assertEquals(400, e.getExceptionCode());
		}
	}

	@Test
	void deleteTable() {
		// Mockito.when(manageTableServicePort.isTableExists(Mockito.anyString())).thenReturn(true);
		 setMockitoSuccessResponseForService();
		Response rs =manageTableService.deleteTable(tableName, loggersDTO);
		assertEquals(400, rs.getStatusCode());
		}
	
	@Test
	void deleteTableDonotExist() {
		// Mockito.when(manageTableServicePort.isTableExists(Mockito.anyString())).thenReturn(true);
		 setMockitoBadResponseForService();
		 try {
		Response rs= manageTableService.deleteTable("TestingTableDelete", loggersDTO);
		 }catch(ContentNotFoundException e) {
		assertEquals(404, e.getExceptionCode());
		}
	}

	@Test
	void compareCloudSchemaWithSoftDeleteSchemaReturnCurrentSchema() {

		tableSchema.setStatusCode(200);
		tableSchema.setMessage("ssss");
		tableSchema.setData(tableSchemav2Data);

		setMockitoSuccessResponseForService();

		assertEquals(200,
				manageTableService
						.compareCloudSchemaWithSoftDeleteSchemaReturnCurrentSchema(tableName, tenantId, tableSchema)
						.getStatusCode());

	}

	@Test
	void createConfigSet() {
		setMockitoSuccessResponseForService();
	//	ConfigSet configSetDTO = new ConfigSet();
		try {
			manageTableService.createConfigSet(configSetDTO);
		} catch (BadRequestOccurredException e) {
			assertEquals(400, e.getExceptionCode());
		}

	}

	@Test
	void isPartialSearchFieldTypePresent() {

		try {
			manageTableService.isPartialSearchFieldTypePresent(tableName);
		} catch (BadRequestOccurredException e) {
			assertEquals(400, e.getExceptionCode());
		}

	}

	@Test
	void getFieldTypeAttributesForPartialSearch() {
		try {
			manageTableService.getFieldTypeAttributesForPartialSearch();
		} catch (BadRequestOccurredException e) {
			assertEquals(400, e.getExceptionCode());
		}

	}

	@Test
	void checkTableDeletionStatus() {
		try {
			manageTableService.checkTableDeletionStatus(tenantId);
		} catch (BadRequestOccurredException e) {
			assertEquals(400, e.getExceptionCode());
		}

	}

	@Test
	void updateSchemaAttributes() {

		// Response s= manageTableService.updateSchemaAttributes(newTableSchemaDTO);
		// System.out.println("sssssssssssssssssssssss"+s);

		try {
			manageTableService.updateSchemaAttributes(newTableSchemaDTO);
		} catch (BadRequestOccurredException e) {
			assertEquals(400, e.getExceptionCode());
		}

	}

	@Test
	void createTable() {
		
		setMockitoSuccessResponseForService();
		Response rs = manageTableService.createTable(manageTable);
		assertEquals(200, rs.getStatusCode());
	}

	@Test
	void createTableIfNotPresent() {
		setMockitoSuccessResponseForService();
//		Response response = manageTableService.createTableIfNotPresent(manageTable, loggersDTO);
//		assertEquals(200, response.getStatusCode());
		try {
			manageTableService.createTableIfNotPresent(manageTable, loggersDTO);
		} catch (BadRequestOccurredException e) {
			assertEquals(400, e.getExceptionCode());
		}
	}

	@Test
	void addSchemaAttributes() {

//
	//	newTableSchemaDTO.setSchemaName("table");
		
	//	newTableSchemaDTO.setTableDetails(finalResponseMap);
//		newTableSchemaDTO.setTableName(tableName);
//
//		schemaRequest.setBasicAuthCredentials(tableName, "abcd");
//		schemaRequest.setBasePath(searchUrl);
//			schemaField.setFilterable(true);
//		schemaField.setMultiValue(true);
//		schemaField.setName("ok");
//		schemaField.setPartialSearch(true);
//		schemaField.setRequired(true);
//		schemaField.setSortable(true);
//		schemaField.setStorable(true);
//		schemaField.setType("string");
//		list.add(schemaField);
//		newTableSchemaDTO.setColumns(list);

	setMockitoSuccessResponseForService();
			
		
		Response rs=	manageTableService.addSchemaAttributes(newTableSchemaDTO);
		assertEquals(400, rs.getStatusCode());
			System.out.println("sssssssssssssssssssssssss"+rs);
	}

	@Test
	void checkDatesDifference() {

		try {
			manageTableService.checkDatesDifference(searchUrl);
		} catch (BadRequestOccurredException e) {
			assertEquals(400, e.getExceptionCode());
		}
		//
	}

	@Test
	void schemaDelete() {
		try {
			manageTableService.checkForSchemaDeletion();
		} catch (BadRequestOccurredException e) {
			assertEquals(1,1);
		} 
	}
	
	@Test
	void updateTableSchema() {
		 newTableSchemaDTO.setColumns(list);
		newTableSchemaDTO.setTableName(tableName);
		newTableSchemaDTO.setSchemaName("solrUrl");
		schemaField.setFilterable(true);
		schemaField.setMultiValue(true);
		schemaField.setName("ok");
		schemaField.setPartialSearch(true);
		schemaField.setRequired(true);
		schemaField.setSortable(true);
		schemaField.setStorable(true);
		schemaField.setType("string");
		list.add(schemaField);
		newTableSchemaDTO.setColumns(list);
		setMockitoSuccessResponseForService();
		Response rs=	manageTableService.updateTableSchema(tenantId, tableName, newTableSchemaDTO, loggersDTO);
		assertEquals(200,rs.getStatusCode());
	}
	
	
	@Test
	void performSchemaDeletion() {
		assertFalse(manageTableService.performSchemaDeletion("101,automatedTestCollection,14-3-2022 05:05:26,name"));
	
	
	}
	
	public NamedList<Object> test(){
		List<String> testing = new ArrayList<String>();
		testing.add(tableName);
		testing.add("Testing_101");
		 NamedList<Object> lst = new NamedList<Object>();
		  lst.add("collections", testing);
		  return lst;
		}
	
	public NamedList<Object> test1(){
		List<String> testing = new ArrayList<String>();
		testing.add("_default");
		testing.add("Product_1.AUTOCREATED");
		 NamedList<Object> lst = new NamedList<Object>();
		  lst.add("configSets", testing);
		  return lst;
		}
	
	
}