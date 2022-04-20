package com.searchservice.app.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.request.schema.SchemaRequest;
import org.apache.solr.client.solrj.response.CollectionAdminResponse;
import org.apache.solr.client.solrj.response.ConfigSetAdminResponse;
import org.apache.solr.client.solrj.response.schema.SchemaResponse;
import org.apache.solr.client.solrj.response.schema.SchemaResponse.UpdateResponse;
import org.apache.solr.common.util.NamedList;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.searchservice.app.config.CapacityPlanProperties;
import com.searchservice.app.config.CapacityPlanProperties.Plan;
import com.searchservice.app.domain.dto.Response;
import com.searchservice.app.domain.dto.table.ConfigSet;
import com.searchservice.app.domain.dto.table.ManageTable;
import com.searchservice.app.domain.dto.table.SchemaField;
import com.searchservice.app.domain.dto.table.TableSchema;
import com.searchservice.app.domain.dto.table.TableSchemav2;
import com.searchservice.app.domain.dto.table.TableSchemav2.TableSchemav2Data;
import com.searchservice.app.domain.utils.SearchUtil;
import com.searchservice.app.infrastructure.adaptor.SearchAPIAdapter;
import com.searchservice.app.infrastructure.adaptor.SearchJAdapter;
import com.searchservice.app.rest.errors.BadRequestOccurredException;
import com.searchservice.app.rest.errors.HttpStatusCode;
import com.searchservice.app.rest.errors.InvalidInputOccurredException;
import com.searchservice.app.rest.errors.NullPointerOccurredException;
import com.searchservice.app.rest.errors.TableAlreadyExistsException;
import com.searchservice.app.rest.errors.TableNotFoundException;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)

@SpringBootTest
@TestInstance(Lifecycle.PER_CLASS)
class ManageTableServiceTest {

	@Value("${base-search-url}")
	String searchUrl;

	private String deleteSchemaAttributesFileTest = "src/test/resources/SchemaDeleteRecordTest.csv";

	private static final String MULTIVALUED = "multiValued";
	private static final String STORED = "stored";
	private static final String REQUIRED = "required";

	private static final String DOCVALUES = "docValues";
	private static final String INDEXED = "indexed";
	private static final String PARTIAL_SEARCH = "partial_search";
	
	SearchAPIAdapter solrApiAdapter = new SearchAPIAdapter();
	HttpSolrClient solrClient = null;
	HttpSolrClient solrClientWithTable = null;
	
	private String tableName = "automatedTestCollection";
	private int tenantId = 101;

	@MockBean
	SearchAPIAdapter solrApiAdapterMocked;

	@MockBean
	SearchUtil solrUtilMocked;

	@MockBean
	CapacityPlanProperties capacityPlanProperties;

	@MockBean
	SearchJAdapter searchJAdapter;

	@InjectMocks
	ManageTableService manageTableService;

	@MockBean
	SchemaRequest schemaRequest;

	
	ManageTable manageTable = new ManageTable();

	TableSchema newTableSchemaDTO = new TableSchema();

	TableSchemav2 tableSchema = new TableSchemav2();
	TableSchemav2Data tableSchemav2Data = new TableSchemav2Data();
	SchemaResponse schemaResponse = new SchemaResponse();
	ConfigSetAdminResponse configSetResponse = new ConfigSetAdminResponse();
	CollectionAdminResponse collectionAdminResponse = new CollectionAdminResponse();
	List<SchemaField> list = new ArrayList<SchemaField>();
	SchemaField schemaField = new SchemaField();
	ConfigSet configSetDTO = new ConfigSet();
	UpdateResponse updatedResponse = new UpdateResponse();
	Response responseDTO = new Response();

	public void setMockitoBadResponseForService() {

		collectionAdminResponse.setElapsedTime(5);
		collectionAdminResponse.setRequestUrl(searchUrl);
		collectionAdminResponse.setResponse(emptyData());
		Mockito.when(searchJAdapter.getCollectionAdminRequestList(solrClient)).thenReturn(collectionAdminResponse);
		configSetResponse.setResponse(test1());
		Mockito.when(searchJAdapter.getConfigSetFromSolrj(solrClient)).thenReturn(configSetResponse);
		manageTable.setColumns(null);
		manageTable.setSku("B");
		manageTable.setTableName("Testing_101");
		List<CapacityPlanProperties.Plan> plan = new ArrayList<>();
		Plan newPlan = new Plan();
		newPlan.setName("Basic");
		newPlan.setSku("B");
		newPlan.setShards(1);
		newPlan.setReplicas(1);
		plan.add(newPlan);
		Mockito.when(capacityPlanProperties.getPlans()).thenReturn(plan);
		Mockito.when(searchJAdapter.updateSchemaLogic(Mockito.any(),Mockito.any())).thenReturn(null);
		schemaResponse.setElapsedTime(3);
		schemaResponse.setRequestUrl(searchUrl);
		schemaResponse.setResponse(test2());
		setTableSchemaDTO();
		Mockito.when(searchJAdapter.getSchemaFields(Mockito.any())).thenReturn(schemaResponse);
		Mockito.when(searchJAdapter.processSchemaRequest(Mockito.any(), Mockito.any())).thenReturn(schemaResponse);
		Mockito.when(searchJAdapter.parseSchemaFieldDtosToListOfMaps(newTableSchemaDTO)).thenReturn(testing(schemaField));

	}

	public void setMockitoTableNotExist() {
		collectionAdminResponse.setElapsedTime(5);
		collectionAdminResponse.setRequestUrl(searchUrl);
		collectionAdminResponse.setResponse(test());
		Mockito.when(searchJAdapter.getCollectionAdminRequestList(solrClient)).thenReturn(collectionAdminResponse);
		manageTable.setColumns(null);
		manageTable.setSku("B");
		manageTable.setTableName("Testing_101");
		List<CapacityPlanProperties.Plan> plan = new ArrayList<>();
		Plan newPlan = new Plan();
		newPlan.setName("Basic");
		newPlan.setSku("B");
		newPlan.setShards(1);
		newPlan.setReplicas(1);
		plan.add(newPlan);
		Mockito.when(capacityPlanProperties.getPlans()).thenReturn(plan);

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

		newTableSchemaDTO.setTableName(tableName);
		schemaField.setFilterable(true);
		schemaField.setMultiValue(true);
		schemaField.setName("test");
		schemaField.setPartialSearch(true);
		schemaField.setRequired(true);
		schemaField.setSortable(true);
		schemaField.setStorable(true);
		schemaField.setType("string");
		list.add(schemaField);
		newTableSchemaDTO.setColumns(list);

		manageTable.setColumns(list);
		manageTable.setSchemaName("timestamp");
		manageTable.setSku("B");
		manageTable.setTableName("Demo");
		manageTable.setTableNewName("Demo1");
		configSetDTO.setBaseConfigSetName("solrUrl");
		configSetDTO.setConfigSetName("solrUrl");
		tableSchemav2Data.setColumns(list);
		List<CapacityPlanProperties.Plan> plan = new ArrayList<>();
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
		schemaResponse.setElapsedTime(3);
		schemaResponse.setRequestUrl(searchUrl);
		schemaResponse.setResponse(test2());
		updatedResponse.setResponse(test3());
		List<String> tableList = new ArrayList<String>();
		tableList.add(tableName);
		tableSchema.setStatusCode(200);
		tableSchema.setMessage("Testing");
		tableSchema.setData(tableSchemav2Data);
		Mockito.when(searchJAdapter.getCollectionAdminRequestList(solrClient)).thenReturn(collectionAdminResponse);
		Mockito.when(searchJAdapter.processSchemaRequest(Mockito.any(), Mockito.any())).thenReturn(schemaResponse);
		Mockito.when(searchJAdapter.addFieldRequestInSolrj(Mockito.any(), Mockito.any())).thenReturn(updatedResponse);
		Mockito.when(searchJAdapter.getSchemaFields(Mockito.any())).thenReturn(schemaResponse);
		Mockito.when(searchJAdapter.deleteTableFromSolrj(Mockito.any())).thenReturn(true);
		Mockito.when(searchJAdapter.parseSchemaFieldDtosToListOfMaps(Mockito.any())).thenReturn(testing(schemaField));
		Mockito.when(searchJAdapter.updateSchemaLogic(Mockito.any(),Mockito.any())).thenReturn(updatedResponse);

	}

	public void setUpTestClass() {
		solrClient = solrApiAdapter.getSearchClient(searchUrl);
		solrClientWithTable = solrApiAdapter.getSearchClientWithTable(searchUrl, tableName);
	}
	

	@BeforeEach
	void setUpTestFiles() throws Exception {
		File testSchemaFile = new File(deleteSchemaAttributesFileTest);
		testSchemaFile.createNewFile();
		addSampleData(testSchemaFile);
		ReflectionTestUtils.setField(manageTableService,"deleteSchemaAttributesFilePath",deleteSchemaAttributesFileTest);
	}
	
	@BeforeEach
	void setUp() {
		setUpTestClass();
		Mockito.when(solrApiAdapterMocked.getSearchClient(searchUrl)).thenReturn(solrClient);
		Mockito.when(solrApiAdapterMocked.getSearchClientWithTable(Mockito.any(), Mockito.any()))
				.thenReturn(solrClientWithTable);
		configSetResponse.setResponse(test1());
		Mockito.when(searchJAdapter.getConfigSetFromSolrj(solrClient)).thenReturn(configSetResponse);
	}

	void configErrorResponse() {
		configSetResponse.setResponse(null);
		Mockito.when(searchJAdapter.getConfigSetFromSolrj(Mockito.any())).thenReturn(configSetResponse);
	}

	@Test
	void configSetError() {
		configErrorResponse();
		assertEquals(400, manageTableService.getConfigSets().getStatusCode());
	}

	@Test
	void getTablesInvalidData() {
		setMockitoBadResponseForService();
		Response resp = manageTableService.getTables(tenantId);
		assertEquals(400, resp.getStatusCode());
	}

	@Test
	void testGetTables() {
		setMockitoSuccessResponseForService();
		Response resp = manageTableService.getTables(tenantId);

		assertEquals(200, resp.getStatusCode());

	}

	@Test
	void getSchemaNonExistingTable() {
		setMockitoTableNotExist();
		try {
			manageTableService.getCurrentTableSchema(tenantId, "InvalidTable_101");
		} catch (TableNotFoundException e) {
			assertEquals(108, e.getExceptioncode());
		}
	}

	@Test
	void deleteTableSuccess() {
		setMockitoSuccessResponseForService();
		Response rs = manageTableService.deleteTable(tableName);
		assertEquals(200, rs.getStatusCode());
	}

	@Test
	void deleteNonExistingTable() {
		setMockitoTableNotExist();
		try {
			manageTableService.deleteTable(tableName + "_123");
		} catch (TableNotFoundException e) {
			assertEquals(108, e.getExceptioncode());
		}
	}

	@Test
	void testCapacityPlans() {
		Mockito.when(solrApiAdapterMocked.getSearchClient(searchUrl)).thenReturn(solrClient);
		assertNotNull(manageTableService.capacityPlans().getPlans());

	}

	@Test
	void getTableSchemaIfPresentNonExistingTable() {
		setMockitoTableNotExist();
		try {
			manageTableService.getTableSchemaIfPresent("InvalidTable");
		} catch (TableNotFoundException e) {
			assertEquals(HttpStatusCode.TABLE_NOT_FOUND.getCode(), e.getExceptioncode());
		}
	}

	@Test
	void createTableIfNotPresentNonExistingTable() {
		setMockitoTableNotExist();
		try {
			manageTableService.createTableIfNotPresent(manageTable);
		} catch (TableAlreadyExistsException e) {
			assertEquals(110, e.getExceptioncode());
		}
	}

	@Test
	void createTableIfNotPresentNullColumns() {
		setMockitoBadResponseForService();
		try {
			manageTableService.createTableIfNotPresent(manageTable);
		} catch (BadRequestOccurredException e) {
			assertEquals(400, e.getExceptionCode());
		}
	}

	@Test
	void checkInvalidTableName() {
		try {
			manageTableService.checkIfTableNameisValid("");
		} catch (InvalidInputOccurredException e) {
			assertEquals(101, e.getExceptioncode());
		}
	}

	@Test
	void testGetTableSchemaIfPresent() {
		setMockitoSuccessResponseForService();
		TableSchemav2 tableSchemaResponse = manageTableService.getTableSchemaIfPresent(tableName);
		assertEquals(200, tableSchemaResponse.getStatusCode());
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
		try {
			manageTableService.isConfigSetExists(null);
		} catch (NullPointerOccurredException e) {
			assertEquals(404, e.getExceptionCode());
		}
	}

	@Test
	void addAliasTable() {

		setMockitoSuccessResponseForService();
		Response rs = manageTableService.addAliasTable(tableName, "AutomatedTesting");
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

		try {
			manageTableService.initializeSchemaDeletion(tenantId, tableName, searchUrl);
		} catch (BadRequestOccurredException e) {
			assertEquals(400, e.getExceptionCode());
		}
	}

	@Test
	void getTableSchema() {
		setMockitoSuccessResponseForService();

		TableSchemav2 tableSchemaResponseDTO = manageTableService.getTableSchema(tableName);
		assertEquals(200, tableSchemaResponseDTO.getStatusCode());
	}

	@Test
	void checkIfTableNameisValid() {

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
		Response rs = manageTableService.deleteTable(tableName);
		assertEquals(200, rs.getStatusCode());
	}

	@Test
	void compareCloudSchemaWithSoftDeleteSchemaReturnCurrentSchema() {

		tableSchema.setStatusCode(200);
		tableSchema.setMessage("Testing");
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

		try {
			manageTableService.createConfigSet(configSetDTO);
		} catch (BadRequestOccurredException e) {
			assertEquals(400, e.getExceptionCode());
		}

	}

	@Test
	void isPartialSearchFieldTypePresent() {

		try {
			searchJAdapter.isPartialSearchFieldTypePresent(tableName);
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
		setMockitoSuccessResponseForService();
		try {
			manageTableService.updateSchemaFields(newTableSchemaDTO);
		} catch (BadRequestOccurredException e) {
			assertEquals(400, e.getExceptionCode());
		}

	}

	@Test
	void getCurrentTableSchema() {

		setMockitoSuccessResponseForService();
		TableSchemav2 getCurrentTableSchema = manageTableService.getCurrentTableSchema(tenantId, tableName);
		assertEquals(200, getCurrentTableSchema.getStatusCode());
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

		Response se = manageTableService.createTableIfNotPresent(manageTable);
		assertEquals(200, se.getStatusCode());
	}

	@Test
	void addSchemaAttributes() {

		setMockitoSuccessResponseForService();

		Response rs = manageTableService.addSchemaFields(newTableSchemaDTO);
		assertEquals(200, rs.getStatusCode());

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
			assertEquals(400, e.getExceptionCode());
		}
	}


	@Test
	void updateTableSchema() {
		setMockitoSuccessResponseForService();
		Response rs = manageTableService.updateTableSchema(tenantId, tableName, newTableSchemaDTO);
		assertEquals(200, rs.getStatusCode());
	}
	
	public void setTableSchemaDTO() {
		newTableSchemaDTO.setTableName(tableName);
		schemaField.setFilterable(true);
		schemaField.setMultiValue(true);
		schemaField.setName("_nest_path");
		schemaField.setPartialSearch(true);
		schemaField.setRequired(true);
		schemaField.setSortable(true);
		schemaField.setStorable(true);
		schemaField.setType("string");
		list.add(schemaField);
		newTableSchemaDTO.setColumns(list);
	}
	
	@Test
	void updateTableSchemaException() {
		setTableSchemaDTO();
		setMockitoBadResponseForService();
		Response rs = manageTableService.updateTableSchema(tenantId, tableName, newTableSchemaDTO);
		assertEquals(404, rs.getStatusCode());
	}

	@Test
	void checkTableDeletionStatusTest() {
		boolean b = manageTableService.checkTableDeletionStatus(0);
		assertFalse(b);
	}

	@Test
	void performSchemaDeletion() {
		assertFalse(manageTableService.performSchemaDeletion("101,automatedTestCollection,14-3-2022 05:05:26,name"));
		
	}

	@Test
	void checkIfSchemaFileExistInvalid() {
		boolean b = manageTableService
				.checkIfSchemaFileExist(new File(deleteSchemaAttributesFileTest + "/Testing"));
		assertFalse(b);

	}
	
	@Test
	void checkIfSchemaFileExistValid() {
		File testFile = new File(deleteSchemaAttributesFileTest  + "1"); 
		boolean b = manageTableService
				.checkIfSchemaFileExist(testFile);
		assertTrue(b);
		testFile.delete();
	}

	public NamedList<Object> test() {
		List<String> testing = new ArrayList<String>();
		testing.add(tableName);
		testing.add(tableName + "_" + tenantId);
		testing.add("Testing_101");
		NamedList<Object> lst = new NamedList<Object>();
		lst.add("collections", testing);
		return lst;
	}

	public NamedList<Object> emptyData() {
		List<String> testing = new ArrayList<String>();
		testing.add("_" + tenantId);
		NamedList<Object> lst = new NamedList<Object>();
		lst.add("collections", testing);
		return lst;
	}

	public NamedList<Object> test1() {
		List<String> testing = new ArrayList<String>();
		testing.add("_default");
		testing.add("Product_1.AUTOCREATED");
		NamedList<Object> lst = new NamedList<Object>();
		lst.add("configSets", testing);
		return lst;
	}

	public NamedList<Object> test3() {
		NamedList<Object> nl1 = new NamedList<Object>();
		List<NamedList<Object>> lst1 = new ArrayList<>();
		nl1.add("status", "0");
		nl1.add("QTime", "4055");
		lst1.add(nl1);
		NamedList<Object> nl2 = new NamedList<Object>();
		nl2.add("responseHeader", lst1);
		return nl2;
	}

	public NamedList<Object> test2() {
		Map<String, Object> map1 = new HashMap<>();
		NamedList<Object> nl1 = new NamedList<Object>();
		NamedList<Object> nl2 = new NamedList<Object>();
		NamedList<Object> nl3 = new NamedList<Object>();
		NamedList<Object> nl4 = new NamedList<Object>();
		List<NamedList<Object>> lst1 = new ArrayList<>();
		List<NamedList<Object>> lst2 = new ArrayList<>();
		List<NamedList<Object>> lst3 = new ArrayList<>();
		List<NamedList<Object>> lst4 = new ArrayList<>();
		nl1.add("name", "_nest_path");
		nl1.add("type", "_nest_path");
		nl2.add("name", "*_txt_en_split_tight");
		nl2.add("type", "text_en_splitting_tight");
		nl2.add("indexed", "true");
		nl2.add("stored", "true");
		nl3.add("name", "*_txt_en_split_tight");
		nl3.add("type", "text_en_splitting_tight");
		nl3.add("indexed", "true");
		nl3.add("stored", "true");
		nl3.add("class", "solr.NestPathField");
		nl3.add("maxCharsForDocValues", "-1");
		nl3.add("multiValued", "false");
		nl4.add("source", "name");
		nl4.add("dest", "name_str");
		nl4.add("maxChars", "256");
		map1.put("name", "default_config");
		map1.put("version", 1.6f);
		map1.put("uniqueKey", "id");
		lst1.add(nl1);
		lst2.add(nl2);
		lst3.add(nl3);
		lst4.add(nl4);
		map1.put("fields", lst1);
		map1.put("dynamicFields", lst2);
		map1.put("fieldTypes", lst3);
		map1.put("copyFields", lst4);
		NamedList<Object> lst = new NamedList<Object>();
		lst.add("schema", map1);
		return lst;
	}
	
	public List<Map<String, Object>> testing(SchemaField fieldDto){
		List<Map<String, Object>> schemaFieldsListOfMap = new ArrayList<>();
		Map<String, Object> fieldDtoMap = new HashMap<>();
		fieldDtoMap.put("name", fieldDto.getName());
		fieldDtoMap.put(STORED, fieldDto.isStorable());
		fieldDtoMap.put(MULTIVALUED, fieldDto.isMultiValue());
		fieldDtoMap.put(REQUIRED, fieldDto.isRequired());
		fieldDtoMap.put(DOCVALUES, fieldDto.isSortable());
		fieldDtoMap.put(INDEXED, fieldDto.isFilterable());
		schemaFieldsListOfMap.add(fieldDtoMap);
		return schemaFieldsListOfMap;
		
	}
	
	public void addSampleData(File file) {
		 int lineNumber = 0;
		 while(lineNumber!=2) {	
		try (FileWriter fw = new FileWriter(file, true); BufferedWriter bw = new BufferedWriter(fw);) {
			if(lineNumber == 0) {
				bw.write("TenantID,TableName,RequestTime,ColumnName\n");
			}
			else {
				bw.write("1,Demo,11-4-2022 07:17:04,category\n");
				bw.write("1,Demo,11-4-2022 07:17:04,is_available\n");
				bw.write("1,Demo,11-4-2022 07:17:04,price\n");
				bw.write(STORED);
			}
			lineNumber++;
			}catch (Exception e) {
				e.printStackTrace();		
			} 
		 }
	}
	
	@AfterAll
	void deleteAllTestFiles() {
		File file = new File("src/test/resources");
		for(File f: file.listFiles()) {
			if(f.toString().endsWith(".csv")) {
				f.delete();
			}
		}
	}

}