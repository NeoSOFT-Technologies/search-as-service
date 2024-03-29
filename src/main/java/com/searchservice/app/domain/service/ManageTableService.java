package com.searchservice.app.domain.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.http.conn.HttpHostConnectException;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.request.CollectionAdminRequest;
import org.apache.solr.client.solrj.request.schema.SchemaRequest;
import org.apache.solr.client.solrj.response.CollectionAdminResponse;
import org.apache.solr.client.solrj.response.schema.SchemaRepresentation;
import org.apache.solr.client.solrj.response.schema.SchemaResponse;
import org.apache.solr.client.solrj.response.schema.SchemaResponse.UpdateResponse;
import org.apache.solr.common.SolrException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.searchservice.app.config.CapacityPlanProperties;
import com.searchservice.app.config.TenantInfoConfigProperties;
import com.searchservice.app.domain.dto.Response;
import com.searchservice.app.domain.dto.table.CapacityPlanResponse;
import com.searchservice.app.domain.dto.table.CreateTable;
import com.searchservice.app.domain.dto.table.ManageTable;
import com.searchservice.app.domain.dto.table.SchemaField;
import com.searchservice.app.domain.dto.table.SchemaLabel;
import com.searchservice.app.domain.dto.table.TableInfo;
import com.searchservice.app.domain.dto.table.TableSchema;
import com.searchservice.app.domain.dto.table.TableSchema.TableSchemaData;
import com.searchservice.app.domain.port.api.ManageTableServicePort;
import com.searchservice.app.domain.port.api.TableDeleteServicePort;
import com.searchservice.app.domain.port.spi.SearchAPIPort;
import com.searchservice.app.domain.service.security.KeycloakPermissionManagementService;
import com.searchservice.app.domain.utils.BasicUtil;
import com.searchservice.app.domain.utils.DateUtil;
import com.searchservice.app.domain.utils.ManageTableUtil;
import com.searchservice.app.domain.utils.SchemaFieldType;
import com.searchservice.app.domain.utils.SearchUtil;
import com.searchservice.app.domain.utils.TableSchemaParserUtil;
import com.searchservice.app.domain.utils.TypeCastingUtil;
import com.searchservice.app.infrastructure.adaptor.SearchJAdapter;
import com.searchservice.app.rest.errors.CustomException;
import com.searchservice.app.rest.errors.HttpStatusCode;
import com.searchservice.app.rest.errors.OperationIncompleteException;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Service
@Transactional
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ManageTableService implements ManageTableServicePort {
	
	private static final String TENANT_INFO_ERROR_MSG = "Tenant Info could not be set. ";
	// Schema
	private static final String SEARCH_EXCEPTION_MSG = "The table - {} is Not Found in the Search Cloud!";
	private static final String SEARCH_SCHEMA_EXCEPTION_MSG = "There's been an error in executing {} operation via schema API. "
			+ "Perhaps the target field- {} isn't present.";
	private static final String SCHEMA_UPDATE_SUCCESS = "Schema is updated successfully";
	private static final String FILE_CREATE_ERROR = "Error File Creating File {}";
	private static final String TABLE = "Table ";
	private static final String TABLE_RESPONSE_MSG = "Table %s Having TenantID: %d %s%s";
	private static final String FROM_CACHE = "[{}] is being fetched from cache";

	private final Logger logger = LoggerFactory.getLogger(ManageTableService.class);
	private SimpleDateFormat formatter = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
	
	private static final String COLLECTIONS = "collections";
	@Value("${base-search-url}")

	private String searchNonStatic;
	@Value("${basic-auth.username}")

	private String basicAuthUsernameNonStatic;
	@Value("${basic-auth.password}")

	private String basicAuthPasswordNonStatic;

	@Autowired
	TenantInfoConfigProperties tenantInfoConfigProperties;
	
	private String baseConfigSetNonStatic;

	// UPDATE Table
	@Value("${table-schema-attributes.delete-file-path}")

	private String deleteSchemaAttributesFilePathNonStatic;
	@Value("${table-schema-attributes.days}")

	private long schemaDeleteDurationNonStatic;

	// Init configurations
	private static String searchURL;
	private static String deleteSchemaAttributesFilePath;
	private static long schemaDeleteDuration;

	@Autowired
	public ManageTableService(@Value("${base-search-url}") String solrURLNonStatic,
			@Value("${table-schema-attributes.delete-file-path}") String deleteSchemaAttributesFilePathNonStatic,
			@Value("${table-schema-attributes.days}") long schemaDeleteDurationNonStatic) {

		searchURL = solrURLNonStatic;
		deleteSchemaAttributesFilePath = deleteSchemaAttributesFilePathNonStatic;
		schemaDeleteDuration = schemaDeleteDurationNonStatic;
	}

	private String servicename = "Manage_Table_Service";
	private String username = "Username";

	@Autowired
	CapacityPlanProperties capacityPlanProperties;

	@Autowired
	SearchAPIPort searchAPIPort;

    @Autowired
    TableDeleteServicePort tableDeleteServicePort;
    
	@Autowired
	SearchJAdapter searchJAdapter;

	@Autowired
	private KeycloakPermissionManagementService kpmService;
	
	
	@Override
	public CapacityPlanResponse capacityPlans() {
		List<CapacityPlanProperties.Plan> capacityPlans = capacityPlanProperties.getPlans();

		return new CapacityPlanResponse(200, "Successfully retrieved all Capacity Plans", capacityPlans);
	}

	@Override
	public Response getTablesForTenant(int tenantId) {
		
		searchJAdapter.checkIfSearchServerDown();
		
		Response getListItemsResponseDTO = new Response();
		try {
			List<String> existingTablesList = getTablesListFromServer();

			// Prepare tables list with corressponding tenantName (fetched from server)
			Map<String, String> tableTenantMap = getAllTableTenantMap(existingTablesList);

			// Prepare final-table-list for given tenantName
			List<Response.TableListResponse> tableListForGivenTenant = ManageTableUtil.getTableListForTenant(existingTablesList, tableTenantMap, tenantId);
			getListItemsResponseDTO.setTableList(
					tableListForGivenTenant);
			getListItemsResponseDTO.setDataSize(tableCountForTenantId(tenantId, existingTablesList));
			getListItemsResponseDTO.setData(null);
			getListItemsResponseDTO.setStatusCode(200);
			getListItemsResponseDTO.setMessage("Successfully retrieved all Tables With TenantId: " + tenantId);

		} catch (Exception e) {
			logger.error(e.toString());
			getListItemsResponseDTO.setStatusCode(HttpStatusCode.BAD_REQUEST_EXCEPTION.getCode());
			getListItemsResponseDTO.setMessage("Unable to retrieve tables for Tenant: "+tenantId);
		}

		return getListItemsResponseDTO;
	}
	
	public int tableCountForTenantId(int tenantId, List<String> tableList) {
		int tableCount = 0;
		for(String table : tableList) {
			if(Integer.parseInt(table.split("_")[1]) == tenantId) {
				tableCount ++;
			}
		}
		return tableCount;
	}
	
	@Override
	public Response getTablesForTenantPagination(int tenantId, int pageNumber, int pageSize) {
		
		searchJAdapter.checkIfSearchServerDown();
		
		Response getListItemsResponseDTO = new Response();
		try {
			List<String> existingTablesList = getTablesListFromServer();

			// Prepare tables list with corressponding tenantName (fetched from server)
			Map<String, String> tableTenantMap = getAllTableTenantMap(existingTablesList);

			// Get paginated list of tables
			List<Response.TableListResponse> paginatedTableListForGivenTenant = ManageTableUtil.getPaginatedTableListForTenant(
					existingTablesList, tableTenantMap, pageNumber, pageSize, tenantId);
			getListItemsResponseDTO.setTableList(
					paginatedTableListForGivenTenant);
			getListItemsResponseDTO.setData(null);
			getListItemsResponseDTO.setDataSize(tableCountForTenantId(tenantId, existingTablesList));
          
			getListItemsResponseDTO.setStatusCode(200);
			getListItemsResponseDTO.setMessage("Successfully retrieved all Tables With TenantId: " + tenantId);

		} catch (Exception e) {
			logger.error(e.toString());
			getListItemsResponseDTO.setStatusCode(HttpStatusCode.BAD_REQUEST_EXCEPTION.getCode());
			getListItemsResponseDTO.setMessage("Unable to retrieve tables for Tenant: "+tenantId);
		}

		return getListItemsResponseDTO;
	}
	

	@Override
	public Response getAllTables(int pageNumber, int pageSize) {
		
		searchJAdapter.checkIfSearchServerDown();
		
		Response getAllTableListResponse = new Response();
		try {
			List<String> existingTablesList = getTablesListFromServer();
			
			// Prepare tables list with corressponding tenantName (fetched from server)
			Map<String, String> tableTenantMap = getAllTableTenantMap(existingTablesList);
			
			// Get paginated list of tables
			getAllTableListResponse.setTableList(ManageTableUtil.getPaginatedTableList(
					existingTablesList, tableTenantMap, pageNumber, pageSize));
			getAllTableListResponse.setData(null);
			getAllTableListResponse.setDataSize(existingTablesList.size());
			
			getAllTableListResponse.setStatusCode(200);
			getAllTableListResponse.setMessage("Successfully retrieved all Tables from server");
		} catch(Exception e) {
			logger.error(e.toString());
			getAllTableListResponse.setStatusCode(HttpStatusCode.BAD_REQUEST_EXCEPTION.getCode());
			getAllTableListResponse.setMessage("Unable to retrieve tables from server");
		}

        return getAllTableListResponse;
	}
	
	@Override
	public TableSchema getCurrentTableSchema(int tenantId, String tableName) {

		searchJAdapter.checkIfSearchServerDown();
		
		if (!isTableExists(tableName + "_" + tenantId))
			throw new CustomException(HttpStatusCode.TABLE_NOT_FOUND.getCode(),HttpStatusCode.TABLE_NOT_FOUND,
					TABLE + tableName + " having TenantID: " + tenantId +" "+HttpStatusCode.TABLE_NOT_FOUND.getMessage());

		if (tableDeleteServicePort.isTableUnderDeletion(tableName + "_"+ tenantId)) {
			throw new CustomException(HttpStatusCode.UNDER_DELETION_PROCESS.getCode(),
					HttpStatusCode.UNDER_DELETION_PROCESS,String.format(TABLE_RESPONSE_MSG, tableName, tenantId, " is ", HttpStatusCode.UNDER_DELETION_PROCESS.getMessage()));
		}
		
		// GET tableSchema at Search cloud
		TableSchema tableSchema = getTableSchema(tableName + "_" + tenantId);

		// Compare tableSchema locally Vs. tableSchema at Search cloud
		TableSchema schemaResponse = compareCloudSchemaWithSoftDeleteSchemaReturnCurrentSchema(tableName, tenantId,
				tableSchema);
		schemaResponse.getData().setColumns(
				schemaResponse.getData().getColumns()
				.stream()
				.filter(s -> !s.getName().startsWith("_"))
				.collect(Collectors.toList()));

		return schemaResponse;
	}

	@Override
	public Response createTableIfNotPresent(CreateTable createTableDTO) {

		searchJAdapter.checkIfSearchServerDown();
		checkIfTableIsValidForCreation(createTableDTO);
		Response apiResponseDTO = createTable(createTableDTO);
		if (apiResponseDTO.getStatusCode() == 200) {
			// Check if new table columns are to be added(Non-null list of columns)
			if (createTableDTO.getColumns() == null) {
				String updatedMsg = String.format("%s. No new columns found", apiResponseDTO.getMessage());
				apiResponseDTO.setMessage(updatedMsg);
				return apiResponseDTO;
			} else if (createTableDTO.getColumns().isEmpty())
				return apiResponseDTO;

			// Add schema fields
			ManageTable tableSchemaDTO = new ManageTable(createTableDTO.getTableName(), createTableDTO.getColumns());
			Response tableSchemaResponseDTO = addSchemaFields(tableSchemaDTO);
			logger.info("Adding schema attributes response: {}", tableSchemaResponseDTO.getMessage());
		}
		return apiResponseDTO;
	}

	public void checkIfTableIsValidForCreation(CreateTable createTableDTO) {
		String tableName = createTableDTO.getTableName().split("_")[0];
		if(checkIfTableNameisValid(tableName)) {
			throw new CustomException(HttpStatusCode.INVALID_TABLE_NAME.getCode(),
					HttpStatusCode.INVALID_TABLE_NAME,"Creating Table Failed , as Invalid Table Name " + tableName + " is Provided");
		}
		else if (tableDeleteServicePort.isTableUnderDeletion(createTableDTO.getTableName())) {
			throw new CustomException(HttpStatusCode.UNDER_DELETION_PROCESS.getCode(),
					HttpStatusCode.UNDER_DELETION_PROCESS,String.format("Table %s Having TenantID %s %s %s", tableName, createTableDTO.getTableName().split("_")[1] ,"is",HttpStatusCode.UNDER_DELETION_PROCESS.getMessage()));
		}
		else if (isTableExists(createTableDTO.getTableName()))
			throw new CustomException(HttpStatusCode.TABLE_ALREADY_EXISTS.getCode(),HttpStatusCode.TABLE_ALREADY_EXISTS, 
					String.format("Table %s Having TenantID %s %s %s", tableName, createTableDTO.getTableName().split("_")[1] ,"",HttpStatusCode.TABLE_ALREADY_EXISTS.getMessage()));
        
		else if(!isColumnNameValid(createTableDTO.getColumns())) {
			   throw new CustomException(HttpStatusCode.INVALID_COLUMN_NAME.getCode()
					   ,HttpStatusCode.INVALID_COLUMN_NAME,HttpStatusCode.INVALID_COLUMN_NAME.getMessage());
		}	
		else if(Boolean.FALSE.equals(isValidFormatDataTypeForMultivalued(createTableDTO.getColumns()))) {		
			throw new CustomException(HttpStatusCode.WRONG_DATA_TYPE.getCode(), HttpStatusCode.WRONG_DATA_TYPE,
					HttpStatusCode.WRONG_DATA_TYPE.getMessage());
		}
	}
	@Override
	public Response deleteTable(String tableName) {
		
		searchJAdapter.checkIfSearchServerDown();
		
		if (!isTableExists(tableName))
			throw new CustomException(HttpStatusCode.TABLE_NOT_FOUND.getCode(),HttpStatusCode.TABLE_NOT_FOUND,
					TABLE + tableName.split("_")[0] + " having TenantID: " + tableName.split("_")[1] + " "+HttpStatusCode.TABLE_NOT_FOUND.getMessage());
		// Delete table
		Response apiResponseDTO = new Response();

		boolean response = searchJAdapter.deleteTableFromSolrj(tableName);
		if (response) {

			apiResponseDTO.setStatusCode(200);

			apiResponseDTO.setMessage("Table: " + tableName + ", is successfully deleted");

		} else {

			apiResponseDTO.setStatusCode(HttpStatusCode.BAD_REQUEST_EXCEPTION.getCode());

			apiResponseDTO.setMessage("Unable to delete table: " + tableName);
		}
		return apiResponseDTO;
	}

	
	@Override
	public Response updateTableSchema(int tenantId, String tableName, ManageTable tableSchemaDTO) {
		
		searchJAdapter.checkIfSearchServerDown();
		
		if (!isTableExists(tableName + "_" + tenantId)) {
			throw new CustomException(HttpStatusCode.TABLE_NOT_FOUND.getCode(), HttpStatusCode.TABLE_NOT_FOUND,
					String.format(TABLE_RESPONSE_MSG, tableName.split("_")[0], tenantId, "",
							HttpStatusCode.TABLE_NOT_FOUND.getMessage()));
		}
		
		if (tableDeleteServicePort.isTableUnderDeletion(tableName)) {
			throw new CustomException(HttpStatusCode.UNDER_DELETION_PROCESS.getCode(),
					HttpStatusCode.UNDER_DELETION_PROCESS, String.format(TABLE_RESPONSE_MSG, tableName.split("_")[0],
							tenantId, "is ", HttpStatusCode.UNDER_DELETION_PROCESS.getMessage()));
		}
		Response apiResponseDTO = new Response();

		// Compare tableSchema locally Vs. tableSchema at solr cloud
		checkForSchemaSoftDeletion(tenantId, tableName, tableSchemaDTO.getColumns());


		if (Boolean.FALSE.equals(isValidFormatDataTypeForMultivalued(tableSchemaDTO.getColumns()))) {

			throw new CustomException(HttpStatusCode.WRONG_DATA_TYPE.getCode(),HttpStatusCode.WRONG_DATA_TYPE,
					HttpStatusCode.WRONG_DATA_TYPE.getMessage());

		}
		
		// ADD new schema fields to the table
		Response tableSchemaResponseDTO = addSchemaFields(tableSchemaDTO);

		apiResponseDTO.setStatusCode(tableSchemaResponseDTO.getStatusCode());
		apiResponseDTO.setMessage(tableSchemaResponseDTO.getMessage());

		// UPDATE existing schema attributes
		apiResponseDTO = updateSchemaFields(tableSchemaDTO);

		return apiResponseDTO;
	}

	/**
	 *  AUXILIARY methods implementations :
	 */
	//---------------------------------------------------------------------------
	// VALIDATION methods
	@Override
	public boolean isTableExists(String tableName) {
		HttpSolrClient searchClientActive = searchAPIPort.getSearchClient(searchURL);
		try {
			CollectionAdminResponse response = searchJAdapter.getCollectionAdminRequestList(searchClientActive);
			List<String> allTables = TypeCastingUtil.castToListOfStrings(response.getResponse().get(COLLECTIONS));
			return allTables.contains(tableName);
		} catch (Exception e) {
			logger.error(e.toString());
			if ((e instanceof SolrServerException)
					&& (HttpHostConnectException) e.getCause() instanceof HttpHostConnectException)
				throw new CustomException(HttpStatusCode.SERVER_UNAVAILABLE.getCode(),HttpStatusCode.BAD_REQUEST_EXCEPTION,
						"Could not connect to Solr server");
			else {
				throw new CustomException(HttpStatusCode.BAD_REQUEST_EXCEPTION.getCode(),
						HttpStatusCode.BAD_REQUEST_EXCEPTION, "Connection Refused by the Backend Server!!");
			}
		}
	}

	public boolean checkTableDeletionStatus(int schemaDeleteRecordCount) {
		if (schemaDeleteRecordCount > 0) {

			logger.debug("Total Number of Schema's Found and Deleted: {}", schemaDeleteRecordCount);
			return true;
		} else {
			logger.debug("No Schema Records Were Found and Deleted With Request More Or Equal To 15 days");

			return false;
		}
	}

	@Override
	public boolean checkIfTableNameisValid(String tableName) {
		if (null == tableName || tableName.isBlank() || tableName.isEmpty())

			throw new CustomException(HttpStatusCode.INVALID_TABLE_NAME.getCode(),
					HttpStatusCode.INVALID_TABLE_NAME,"Provide valid Table Name");
		Pattern pattern = Pattern.compile("[^a-zA-Z0-9]");
		Matcher matcher = pattern.matcher(tableName);
		return matcher.find();
	}

	public boolean checkIfSchemaFileExist(File file) {
		if (!file.exists()) {
			try {
				boolean createFile = file.createNewFile();
				if (createFile) {
					logger.debug("File With Name: {} Created Succesfully", file.getName());
				}
				return true;
			} catch (IOException e) {
				logger.error(FILE_CREATE_ERROR, file.getName(), e);
				return false;
			}
		} else {
			return false;
		}
	}

	@Override
	public boolean isColumnNameValid(List<SchemaField> columns) {
		if (columns == null) {
			return true;
		} else {
			Pattern pattern = Pattern.compile("[^a-zA-Z0-9]");
			boolean columnNameIsValid = true;
			for (SchemaField column : columns) {
				Matcher matcher = pattern.matcher(column.getName());
				if (matcher.find()) {
					columnNameIsValid = false;
					break;
				}

			}
			return columnNameIsValid;
		}
	}
	
	@Override
	public Boolean isValidFormatDataTypeForMultivalued(List<SchemaField> columns) {
		if(columns == null) {
			return true;
		}else {
		boolean multiValueCheck = true;
		for (SchemaField column : columns) {
			if (Boolean.FALSE.equals(TableSchemaParserUtil.isMultivaluedDataTypePlural(column))) {

				multiValueCheck = false;
				break;
			}
		}
		return multiValueCheck;
		}
	}
	
	// table schema deletion
	public void checkForSchemaSoftDeletion(int tenantId, String tableName, List<SchemaField> schemaColumns) {
		List<SchemaField> existingSchemaAttributes = getTableSchema(tableName + "_" + tenantId).getData().getColumns();

		for (SchemaField existingSchemaAttribute : existingSchemaAttributes) {
			String exsitingSchemaName = existingSchemaAttribute.getName();
			boolean isContains = ManageTableUtil.checkIfListContainsSchemaColumn(schemaColumns,
					existingSchemaAttribute);

			if (!(exsitingSchemaName.equalsIgnoreCase("_nest_path_") || exsitingSchemaName.equalsIgnoreCase("_root_")
					|| exsitingSchemaName.equalsIgnoreCase("_text_") || exsitingSchemaName.equalsIgnoreCase("_version_")
					|| exsitingSchemaName.equalsIgnoreCase("id")) && !isContains) {
				initializeSchemaDeletion(tenantId, tableName, existingSchemaAttribute);
			}
		}

	}

	@Override
	public void checkForSchemaDeletion() {
		File existingSchemaFile = new File(deleteSchemaAttributesFilePath);
		checkIfSchemaFileExist(existingSchemaFile);
		File newSchemaFile = new File(
				deleteSchemaAttributesFilePath.substring(0, deleteSchemaAttributesFilePath.length() - 4) + "Temp.csv");
		int lineNumber = 0;
		int schemaDeleteRecordCount = 0;
		try (BufferedReader br = new BufferedReader(new FileReader(existingSchemaFile));
				PrintWriter pw = new PrintWriter(new FileWriter(newSchemaFile))) {
			String currentSchemaDeleteRecord;
			while ((currentSchemaDeleteRecord = br.readLine()) != null) {
				if (lineNumber != 0) {
					long diff = DateUtil.checkDatesDifference(currentSchemaDeleteRecord,formatter);
					if (diff < schemaDeleteDuration) {
						pw.println(currentSchemaDeleteRecord);
					} else {
						if (performSchemaDeletion(currentSchemaDeleteRecord)) {
							schemaDeleteRecordCount++;

						} else {
							pw.println(currentSchemaDeleteRecord);
						}
					}
				} else {
					pw.println(currentSchemaDeleteRecord);
				}
				lineNumber++;
			}
			pw.flush();
			makeDeleteTableFileChangesForDelete(newSchemaFile, existingSchemaFile, schemaDeleteRecordCount);
		} catch (IOException exception) {
			logger.error("Error While Performing Schema Deletion ", exception);
		}
	}

	//---------------------------------------------------------------------------
	// RETRIEVAL methods
	@Override
	public TableInfo getTableDetails(String tableName) {
		
		HttpSolrClient searchClientActive = searchAPIPort.getSearchClientWithTable(searchURL, tableName);
		TableInfo tableInfo = new TableInfo();
		try {
			String clusterStatusResponseString = searchJAdapter.getClusterStatusFromSolrjCluster(searchClientActive);			
			tableInfo = ManageTableUtil.getTableInfoFromClusterStatus(clusterStatusResponseString, tableName);	
			
			// Add TenantInfo to TableInfo
			Map<String, String> userPropsResponseMap = searchJAdapter.getUserPropsFromCollectionConfig(tableName);
			tableInfo.setTenantInfo(userPropsResponseMap);
			
		} catch(Exception e) {
			logger.error("Error occurred while fetching table details: ", e);
		}
		
		return tableInfo;
	}
	
	@Override
	public TableSchema compareCloudSchemaWithSoftDeleteSchemaReturnCurrentSchema(String tableName, int tenantId,
			TableSchema tableSchema) {

		TableSchemaData data = new TableSchemaData();
		data.setTableName(tableName);

		List<SchemaField> schemaAttributesCloud = tableSchema.getData().getColumns();

		// READ from SchemaDeleteRecord.csv and exclude the deleted attributes
		List<String> deletedSchemaAttributesNames = readSchemaInfoFromSchemaDeleteManager(tenantId, tableName);

		// Prepare the final tableSchema to return
		List<SchemaField> schemaAttributesFinal = new ArrayList<>();
		List<String> schemaAttributesToSkipNames = new ArrayList<>();
		// Note down schemaAttributes to skip
		for (SchemaField dto : schemaAttributesCloud) {
			if (!deletedSchemaAttributesNames.contains(dto.getName())) {
				schemaAttributesFinal.add(dto);
			}
			schemaAttributesToSkipNames.add(dto.getName());
		}
		data.setColumns(schemaAttributesFinal);
		data.setTableInfo(tableSchema.getData().getTableInfo());
		tableSchema.setData(data);

		return tableSchema;
	}

	@Override
	public TableSchema getTableSchema(String tableName) {

		TableSchema tableSchemaResponseDTO = new TableSchema();
		TableSchemaData data = new TableSchemaData();
		HttpSolrClient searchClientActive = searchAPIPort.getSearchClientWithTable(searchURL, tableName);
		try {
			SchemaResponse schemaResponse = searchJAdapter.getSchemaFields(searchClientActive);
			
			List<Map<String, Object>> schemaFields = schemaResponse.getSchemaRepresentation().getFields();
			tableSchemaResponseDTO.setStatusCode(200);

			List<SchemaField> solrSchemaFieldDTOs = new ArrayList<>();

			for (Map<String, Object> f : schemaFields) {

				// Prepare the SolrFieldDTO
				SchemaField solrFieldDTO = new SchemaField();
				solrFieldDTO.setName((String) f.get(SchemaLabel.NAME.getLabel()));

				// Parse Field Type Object(String) to Enum

				String solrFieldType = SchemaFieldType.fromSearchFieldTypeToStandardDataType((String) f.get("type"),
						f.get(SchemaLabel.MULTIVALUED.getLabel()));

				solrFieldDTO.setType(solrFieldType);
				TableSchemaParserUtil.setFieldsAsPerTheSchema(solrFieldDTO, f);
				solrSchemaFieldDTOs.add(solrFieldDTO);

			}
			
			// Get TableInfo
			TableInfo tableInfo = getTableDetails(tableName);

			// Prepare response dto
			data.setTableName(tableName.split("_")[0]);
			data.setColumns(solrSchemaFieldDTOs);
			data.setTableInfo(tableInfo);
			tableSchemaResponseDTO.setData(data);
			tableSchemaResponseDTO.setStatusCode(200);
			tableSchemaResponseDTO.setMessage("Schema is retrieved successfully");
		} catch (SolrException e) {
			tableSchemaResponseDTO.setStatusCode(HttpStatusCode.BAD_REQUEST_EXCEPTION.getCode());
			logger.error(SEARCH_EXCEPTION_MSG, tableName);
			logger.info(e.toString());
		}

		return tableSchemaResponseDTO;
	}

	public List<String> getTablesListFromServer() {
		HttpSolrClient searchClientActive = searchAPIPort.getSearchClient(searchURL);

		CollectionAdminResponse response = searchJAdapter.getCollectionAdminRequestList(searchClientActive);
		List<String> existingTablesList = TypeCastingUtil
				.castToListOfStrings(response.getResponse().get(COLLECTIONS));

		// Check for tables under deletion
		existingTablesList.removeAll(tableDeleteServicePort.getTablesUnderDeletion(true).getData());
		return existingTablesList;
	}
	
	@Override
	public Map<String, String> getAllTableTenantMap(List<String> tablesList) {
		Map<String, String> tableTenantMap = new HashMap<>();
		for(String tableWithTenantId: tablesList) {
			String table = tableWithTenantId.split("_")[0];

			Map<String, String> userPropsResponseMap = searchJAdapter.getUserPropsFromCollectionConfig(tableWithTenantId);
			if(userPropsResponseMap != null 
					&& !userPropsResponseMap.isEmpty() 
					&& userPropsResponseMap.containsKey("tenantName")) {
				String tenantName = userPropsResponseMap.get("tenantName");
				tableTenantMap.put(table, tenantName);
			} else
				tableTenantMap.put(table, "No Tenant Found");
		}
		
		return tableTenantMap;
	}
	
	public void fetchTenantNameFromCacheAndSetInCollectionConfig(CreateTable manageTableDTO) {
		String tenantName = null;
		String tenantKey = "";
		if(tenantInfoConfigProperties != null)
			tenantKey = tenantInfoConfigProperties.getTenant();

		if(kpmService.checkIfRealmNameExistsInCache(tenantKey)) {
			logger.info(FROM_CACHE, tenantKey);

			tenantName = kpmService.getRealmNameFromCache(tenantKey);
			// Remove tenantName entry from cache after this service call
			kpmService.evictRealmNameFromCache(tenantKey);
			
			if(tenantName == null)
				throw new CustomException(
						HttpStatusCode.SAAS_SERVER_ERROR.getCode(), 
						HttpStatusCode.SAAS_SERVER_ERROR, 
						TENANT_INFO_ERROR_MSG+HttpStatusCode.SAAS_SERVER_ERROR.getMessage());
			try {
				Map<String, String> userPropsMap = null;
				if(tenantInfoConfigProperties != null) {
					userPropsMap = Collections.singletonMap(
							tenantInfoConfigProperties.getTenant(), tenantName);
				} else
					throw new NullPointerException();
				searchJAdapter.setUserPropertiesInCollectionConfig(userPropsMap, manageTableDTO.getTableName());
			} catch(Exception e) {
				throw new CustomException(
						HttpStatusCode.SAAS_SERVER_ERROR.getCode(), 
						HttpStatusCode.SAAS_SERVER_ERROR, 
						TENANT_INFO_ERROR_MSG+HttpStatusCode.SAAS_SERVER_ERROR.getMessage());
			}

		} else {
			throw new CustomException(
					HttpStatusCode.SAAS_SERVER_ERROR.getCode(), 
					HttpStatusCode.SAAS_SERVER_ERROR, 
					TENANT_INFO_ERROR_MSG+HttpStatusCode.SAAS_SERVER_ERROR.getMessage());
		}
	}

	// Soft Delete Table Schema Info Retrieval
	public List<String> readSchemaInfoFromSchemaDeleteManager(int tenantId, String tableName) {
		List<String> deletedSchemaAttributes = new ArrayList<>();
		File file = new File(deleteSchemaAttributesFilePath);
		checkIfSchemaFileExist(file);
		try (FileReader fr = new FileReader(file)) {
			BufferedReader br = new BufferedReader(fr);
			int lineNumber = 0;
			String currentDeleteRecordLine;
			while ((currentDeleteRecordLine = br.readLine()) != null) {
				if (lineNumber > 0) {
					String[] currentRecordData = currentDeleteRecordLine.split(",");
					if (currentRecordData[0].equalsIgnoreCase(String.valueOf(tenantId))
							&& currentRecordData[1].equalsIgnoreCase(String.valueOf(tableName))) {
						deletedSchemaAttributes.add(currentRecordData[3]);
					}
				}
				lineNumber++;
			}
		} catch (Exception e) {
			logger.error("Soft Delete SchemaInfo could not be retrieved");
			throw new OperationIncompleteException(HttpStatusCode.INTERNAL_SERVER_ERROR.getCode(),
					"Soft Delete SchemaInfo could not be retrieved");
		}

		return deletedSchemaAttributes;
	}

	//---------------------------------------------------------------------------
	// ALTER DATA methods
	@Override
	public Response createTable(CreateTable manageTableDTO) {
		Response apiResponseDTO = new Response();

		List<CapacityPlanProperties.Plan> capacityPlans = capacityPlanProperties.getPlans();
		CapacityPlanProperties.Plan selectedCapacityPlan = null;

		for (CapacityPlanProperties.Plan capacityPlan : capacityPlans) {
			if (capacityPlan.getSku().equals(manageTableDTO.getSku())) {
				selectedCapacityPlan = capacityPlan;
			}
		}

		if (selectedCapacityPlan == null) {
			// INVALD SKU
			throw new CustomException(HttpStatusCode.INVALID_SKU_NAME.getCode(),HttpStatusCode.INVALID_SKU_NAME,
					HttpStatusCode.INVALID_SKU_NAME.getMessage() + " : " + manageTableDTO.getSku());
		}

		CollectionAdminRequest.Create request = CollectionAdminRequest.createCollection(manageTableDTO.getTableName(),
				selectedCapacityPlan.getShards(), selectedCapacityPlan.getReplicas());
		
		HttpSolrClient searchClientActive = new HttpSolrClient.Builder(searchURL).build();

		request.setMaxShardsPerNode(selectedCapacityPlan.getShards() * selectedCapacityPlan.getReplicas());
		try {
			searchJAdapter.createTableInSolrj(request, searchClientActive);
			apiResponseDTO.setStatusCode(200);
			
			// Set User Properties to Config Overlay
			fetchTenantNameFromCacheAndSetInCollectionConfig(manageTableDTO);
			
			apiResponseDTO.setMessage("Successfully created table: " + manageTableDTO.getTableName());
		} catch (Exception e) {
			logger.error(e.toString());
			apiResponseDTO.setStatusCode(HttpStatusCode.BAD_REQUEST_EXCEPTION.getCode());

			apiResponseDTO.setMessage("Unable to create table: " + manageTableDTO.getTableName() + ". Exception.");
		} finally {
			SearchUtil.closeSearchClientConnection(searchClientActive);

		}
		return apiResponseDTO;
	}

	@Override
	public Response addSchemaFields(ManageTable newTableSchemaDTO) {
		HttpSolrClient searchClientActive = searchAPIPort.getSearchClientWithTable(
				searchURL,
				newTableSchemaDTO.getTableName());

		SchemaRequest schemaRequest = new SchemaRequest();
		Response tableSchemaResponseDTO = new Response();

		String errorCausingField = null;
		String payloadOperation = "";
		try {
			SchemaResponse schemaResponse = searchJAdapter.processSchemaRequest(searchClientActive, schemaRequest);
			SchemaRepresentation retrievedSchema = schemaResponse.getSchemaRepresentation();
			List<Map<String, Object>> schemaFields = retrievedSchema.getFields();

			// Prepare final HashMap, and remove already existing schema fields
			List<SchemaField> newFields = newTableSchemaDTO.getColumns();
			Map<String, SchemaField> newFieldsHashMap = BasicUtil.convertSchemaFieldListToHashMap(newFields);
			newFieldsHashMap = ManageTableUtil.removeExistingFields(newFieldsHashMap, newFields, schemaFields);

			if (newFieldsHashMap.isEmpty()) {
				tableSchemaResponseDTO.setStatusCode(HttpStatusCode.BAD_REQUEST_EXCEPTION.getCode());
				tableSchemaResponseDTO.setMessage("No new fields found; add attributes operation NOT ALLOWED");
				return tableSchemaResponseDTO;
			} else {
				for (Map.Entry<String, SchemaField> fieldDtoEntry : newFieldsHashMap.entrySet()) {
					SchemaField fieldDto = fieldDtoEntry.getValue();
					payloadOperation = "SchemaRequest.AddField";
					errorCausingField = fieldDto.getName();

					if (!TableSchemaParserUtil.validateSchemaField(fieldDto)) {
						logger.info("Validation failed for SolrFieldDTO before updating the current schema");
						tableSchemaResponseDTO.setStatusCode(HttpStatusCode.BAD_REQUEST_EXCEPTION.getCode());
						break;
					}

					Map<String, Object> newField = new HashMap<>();
					// PARTIAL_SEARCH UPDATE
					searchJAdapter.partialSearchUpdate(newTableSchemaDTO, fieldDto, newField);
					newField = TableSchemaParserUtil.prepareNewField(newField, fieldDto);

					// Add new fields present in the Target Schema to the given collection/table
					SchemaRequest.AddField addFieldRequest = new SchemaRequest.AddField(newField);
					searchJAdapter.addFieldRequestInSolrj(addFieldRequest, searchClientActive);
				}
				tableSchemaResponseDTO.setStatusCode(200);
				tableSchemaResponseDTO.setMessage("New attributes are added successfully");
			}

		} catch (SolrException e) {
			tableSchemaResponseDTO.setStatusCode(HttpStatusCode.BAD_REQUEST_EXCEPTION.getCode());
			tableSchemaResponseDTO.setMessage("Schema attributes could not be added to the table " + e.getMessage());
			logger.error(SEARCH_SCHEMA_EXCEPTION_MSG, payloadOperation, errorCausingField);
		} finally {
			SearchUtil.closeSearchClientConnection(searchClientActive);
		}
		return tableSchemaResponseDTO;
	}

	@Override
	public Response updateSchemaFields(ManageTable newTableSchemaDTO) {
		// Prepare SearchClient instance
		HttpSolrClient searchClientActive = searchAPIPort.getSearchClientWithTable(searchURL,
				newTableSchemaDTO.getTableName());
		Response apiResponseDTO = new Response();

		try {
			// Get all fields from incoming(from req Body) schemaDTO and validate
			List<Map<String, Object>> targetSchemafields = searchJAdapter
					.parseSchemaFieldDtosToListOfMaps(newTableSchemaDTO);

			int updatedFields = 0;
			for (Map<String, Object> currField : targetSchemafields) {

				// Pass the field to be updated
				SchemaRequest.ReplaceField updateFieldsRequest = new SchemaRequest.ReplaceField(currField);
				searchJAdapter.updateSchemaLogic(searchClientActive, updateFieldsRequest);

				updatedFields++;
				logger.info("Field- {} is successfully updated", currField.get(SchemaLabel.NAME.getLabel()));
			}
			apiResponseDTO.setStatusCode(200);
			apiResponseDTO.setMessage(SCHEMA_UPDATE_SUCCESS);
			// Compare required Vs Updated Fields
			logger.debug("Total field updates required in the current schema: {}",
					newTableSchemaDTO.getColumns().size());
			logger.debug("Total fields updated in the current schema: {}", updatedFields);

		} catch (NullPointerException e) {
			apiResponseDTO.setStatusCode(HttpStatusCode.NULL_POINTER_EXCEPTION.getCode());
			apiResponseDTO.setMessage("Schema could not be updated successfully");
			logger.error("Null value detected!", e);
		} catch (SolrException e) {
			apiResponseDTO.setStatusCode(HttpStatusCode.BAD_REQUEST_EXCEPTION.getCode());
			apiResponseDTO.setMessage("Schema could not be updated");
			logger.error(SEARCH_EXCEPTION_MSG + " Existing schema fields couldn't be updated!",
					newTableSchemaDTO.getTableName(), e.getMessage());

		}
		return apiResponseDTO;
	}
	
	public void updateSofDeleteSchemaFieldRequiredFalse(ManageTable newTableSchemaDTO) {
		HttpSolrClient searchClientActive = searchAPIPort.getSearchClientWithTable(searchURL,
				newTableSchemaDTO.getTableName());
		try {
			List<Map<String, Object>> targetSchemafields = searchJAdapter
					.parseSchemaFieldDtosToListOfMaps(newTableSchemaDTO);
			for (Map<String, Object> currField : targetSchemafields) {
				SchemaRequest.ReplaceField updateFieldsRequest = new SchemaRequest.ReplaceField(currField);
				searchJAdapter.updateSchemaLogic(searchClientActive, updateFieldsRequest);
				logger.info("Field- {} Required Value Set False ", currField.get(SchemaLabel.NAME.getLabel()));
			}
		} catch (NullPointerException e) {
			logger.error("Null value detected!", e);
		} catch (SolrException e) {
			logger.error(SEARCH_EXCEPTION_MSG + " schema field Required Value could not be updated!",
					newTableSchemaDTO.getTableName(), e.getMessage());

		}
	}

	@Override
	public Response addAliasTable(String tableOriginalName, String tableAlias) {

		Response apiResponseDTO = new Response();
		CollectionAdminRequest.Rename request = CollectionAdminRequest.renameCollection(tableOriginalName, tableAlias);
		HttpSolrClient searchClientActive = searchAPIPort.getSearchClient(searchURL);
		try {
			searchJAdapter.addAliasTableInSolrj(searchClientActive, request);
			apiResponseDTO.setStatusCode(200);
		} catch (Exception e) {
			apiResponseDTO.setStatusCode(HttpStatusCode.BAD_REQUEST_EXCEPTION.getCode());
			logger.error(e.toString());
		}
		if (apiResponseDTO.getStatusCode() == 200) {
			apiResponseDTO
					.setMessage("Successfully renamed Solr Collection: " + tableOriginalName + " to " + tableAlias);
		} else {
			apiResponseDTO.setMessage("Unable to rename Solr Collection: " + tableOriginalName + ". Exception.");
		}
		return apiResponseDTO;
	}

	// soft deletion
	public void initializeSchemaDeletion(int tenantId, String tableName,SchemaField schemaField) {
		File file = new File(deleteSchemaAttributesFilePath);
		String columnName = schemaField.getName();	
		checkIfSchemaFileExist(file);
		try (FileWriter fw = new FileWriter(file, true); BufferedWriter bw = new BufferedWriter(fw)) {
			String newRecord = tenantId + "," + tableName + "," + DateUtil.getFormattedDate(formatter) + "," + columnName;
			bw.write(newRecord);
			bw.newLine();
			logger.debug("Schema {} Succesfully Initialized For Deletion ", columnName);
			schemaField.setRequired(false);
			ManageTable updateSchemaDTO = new ManageTable(tableName + "_" + tenantId, Arrays.asList(schemaField));
			updateSofDeleteSchemaFieldRequiredFalse(updateSchemaDTO);
		} catch (IOException e) {
			logger.error("Error While Intializing Deletion for Schema :{} ", columnName);
		}
	}
	
	public boolean performSchemaDeletion(String schemaDeleteData) {
		String columnName = schemaDeleteData.split(",")[3];
		String tableName = schemaDeleteData.split(",")[1];

		HttpSolrClient searchClientActive = searchAPIPort.getSearchClientWithTable(searchURL, tableName);
		SchemaRequest.DeleteField deleteFieldRequest = new SchemaRequest.DeleteField(columnName);
		try {
			UpdateResponse response = searchJAdapter.performSchemaDeletion(searchClientActive, deleteFieldRequest);
			int schemaDeletionStatus = response.getStatus();

			if (schemaDeletionStatus == 200) {
				logger.debug("Schema {} Succesfully Deleted ", columnName);
				return true;
			} else {
				logger.debug("Schema {} Deletion Failed ", columnName);
				return false;
			}
		} catch (Exception e) {
			logger.error("Exception Occured While Performing Deletion for Schema {} " + columnName, e);

		}
		return false;
	}

	public void makeDeleteTableFileChangesForDelete(File newFile, File existingFile, int schemaDeleteRecordCount) {
		File schemaDeleteRecordFile = new File(deleteSchemaAttributesFilePath);
		checkIfSchemaFileExist(schemaDeleteRecordFile);
		if (existingFile.delete() && newFile.renameTo(schemaDeleteRecordFile)) {
			checkTableDeletionStatus(schemaDeleteRecordCount);
		}
	}

}
