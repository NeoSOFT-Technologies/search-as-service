package com.searchservice.app.domain.service;

import java.io.IOException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.solr.client.solrj.SolrRequest.METHOD;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.request.CollectionAdminRequest;
import org.apache.solr.client.solrj.request.ConfigSetAdminRequest;
import org.apache.solr.client.solrj.request.schema.SchemaRequest;
import org.apache.solr.client.solrj.response.CollectionAdminResponse;
import org.apache.solr.client.solrj.response.ConfigSetAdminResponse;
import org.apache.solr.client.solrj.response.schema.SchemaRepresentation;
import org.apache.solr.client.solrj.response.schema.SchemaResponse;
import org.apache.solr.client.solrj.response.schema.SchemaResponse.UpdateResponse;
import org.apache.solr.common.SolrException;
import org.apache.solr.common.util.NamedList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.searchservice.app.config.CapacityPlanProperties;
import com.searchservice.app.domain.dto.ApiResponseDTO;
import com.searchservice.app.domain.dto.GetListItemsResponseDTO;
import com.searchservice.app.domain.dto.SchemaFieldDTO;
import com.searchservice.app.domain.dto.logger.CorrelationID;
import com.searchservice.app.domain.dto.logger.IPAddress;
import com.searchservice.app.domain.dto.schema.SchemaResponseDTO;
import com.searchservice.app.domain.dto.table.ConfigSetDTO;
import com.searchservice.app.domain.dto.table.GetCapacityPlanDTO;
import com.searchservice.app.domain.dto.table.ManageTableDTO;
import com.searchservice.app.domain.dto.table.TableSchemaDTO;
import com.searchservice.app.domain.dto.table.TableSchemaResponseDTO;
import com.searchservice.app.domain.port.api.ManageTableServicePort;
import com.searchservice.app.domain.utils.TableSchemaParser;
import com.searchservice.app.domain.utils.TypeCastingUtil;
import com.searchservice.app.infrastructure.adaptor.SolrAPIAdapter;
import com.searchservice.app.infrastructure.enums.SchemaFieldType;
import com.searchservice.app.rest.errors.BadRequestOccurredException;
import com.searchservice.app.rest.errors.ContentNotFoundException;
import com.searchservice.app.rest.errors.NullPointerOccurredException;
import com.searchservice.app.rest.errors.SolrSchemaValidationException;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Service
@Transactional
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ManageTableService implements ManageTableServicePort {
	private static final String TABLE_NOT_FOUND_MSG = "Table: %s, does not exist";
	// Schema
	private static final String SOLR_EXCEPTION_MSG = "The table - {} is Not Found in the Solr Cloud!";
	private static final String SOLR_SCHEMA_EXCEPTION_MSG = "There's been an error in executing {} operation via schema API. "
	+ "Perhaps the target field- {} isn't present.";
	private static final String SCHEMA_UPDATE_SUCCESS = "Schema is updated successfully";
	private static final String MULTIVALUED = "multiValued";
	private static final String STORED = "stored";
	private static final String REQUIRED = "required";
	private static final String VALIDATED = "validated";
	
	private final Logger logger = LoggerFactory.getLogger(ManageTableService.class);
	
	@Value("${base-solr-url}")
	private String solrURL;
	@Value("${basic-auth.username}")
	private String basicAuthUsername;
	@Value("${basic-auth.password}")
	private String basicAuthPassword;
	// ConfigSet
	@Value("${base-configset}")
	private String baseConfigSet;
	
	
    @Autowired
    CapacityPlanProperties capacityPlanProperties;
	
	@Autowired
	SolrAPIAdapter solrAPIAdapter;
	HttpSolrClient solrClient;
	
	CorrelationID correlationID=new CorrelationID();
    
	 @Autowired
	 HttpServletRequest request;
    
    ZonedDateTime utc = ZonedDateTime.now(ZoneOffset.UTC);
    
    private String servicename = "Manage_Table_Service";
    
    private String username = "Username";
	public ManageTableService(String solrUrl, SolrAPIAdapter solrAPIAdapter, HttpSolrClient solrClient) {
		this.solrURL = solrUrl;
		this.solrAPIAdapter = solrAPIAdapter;
		this.solrClient = solrClient;
	}
	

	@Override
	public GetCapacityPlanDTO capacityPlans() {
        List<CapacityPlanProperties.Plan> capacityPlans = capacityPlanProperties.getPlans();
        if(capacityPlans != null)
        	return new GetCapacityPlanDTO(capacityPlans);
        else
        	throw new NullPointerOccurredException(404, "No capacity plans found. Null returned");
	}

	@Override
	public ApiResponseDTO isTablePresent(String tableName) {
		String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
		String correlationid = correlationID.generateUniqueCorrelationId();
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.set(CorrelationID.CORRELATION_ID_HEADER_NAME, correlationid); 
		String ipaddress=request.getRemoteAddr();
		String timestamp=utc.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        logger.info("--------Started request of Service Name : {} , Username : {}, Corrlation Id : {}, IP Address : {} , TimeStamp : {},  Method name : {}",servicename,username,correlationid,ipaddress,timestamp,nameofCurrMethod);

		ApiResponseDTO apiResponseDTO=new ApiResponseDTO();
        CollectionAdminRequest.List request = new CollectionAdminRequest.List();
        solrClient = new HttpSolrClient.Builder(solrURL).build();
        try {
            CollectionAdminResponse response = request.process(solrClient);
            List<String> allCollections=TypeCastingUtil.castToListOfStrings(response.getResponse().get("collections"));
            if(allCollections.contains(tableName)){
                apiResponseDTO.setResponseStatusCode(200);
                apiResponseDTO.setResponseMessage("true");
                logger.info("-----------Successfully response Corrlation Id : {}, Username : {}, IP Address : {} , TimeStamp : {},  Method name : {}",username,correlationid,ipaddress,timestamp,nameofCurrMethod);
            }else{
                apiResponseDTO.setResponseStatusCode(400);
                apiResponseDTO.setResponseMessage("false");
                logger.info("-----------Failed response Username : {},Corrlation Id : {}, IP Address : {} , TimeStamp : {},  Method name : {}",username,correlationid,ipaddress,timestamp,nameofCurrMethod);
            }
        } catch (Exception e) {
            logger.error(e.toString());
            apiResponseDTO.setResponseStatusCode(400);
            apiResponseDTO.setResponseMessage("Error!");
            logger.info("-----------Failed response Username : {}, Corrlation Id : {}, IP Address : {} , TimeStamp : {},  Method name : {}",username,correlationid,ipaddress,nameofCurrMethod);
        }
        return apiResponseDTO;
	}

	
	@Override
	public TableSchemaResponseDTO getTableSchemaIfPresent(String tableName,String correlationid,String ipaddress) {
		String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.set(CorrelationID.CORRELATION_ID_HEADER_NAME, correlationid); 
		String timestamp=utc.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        logger.info("--------Started request of Service Name : {} , Username : {}, Corrlation Id : {}, IP Address : {} , TimeStamp : {},  Method name : {}",servicename,username,correlationid,ipaddress,timestamp,nameofCurrMethod);

		if(!isTableExists(tableName)) {
			logger.info("-----------Failed response Username : {}, Corrlation Id : {}, IP Address : {} , TimeStamp : {},  Method name : {}",username,correlationid,ipaddress,timestamp,nameofCurrMethod);
			throw new BadRequestOccurredException(400, 
					String.format(TABLE_NOT_FOUND_MSG, tableName));
			
		}
		logger.info("-----------Successfully response Username : {}, Corrlation Id : {}, IP Address : {} , TimeStamp : {},  Method name : {}",username,correlationid,ipaddress,timestamp,nameofCurrMethod);
		return getTableSchema(tableName);
	}

	
	@Override
	public GetListItemsResponseDTO getTables(String correlationid,String ipaddress) {
		String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.set(CorrelationID.CORRELATION_ID_HEADER_NAME, correlationid); 	
		//String ipaddress=ipAddress.getIPAddress();
		String timestamp=utc.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        logger.info("--------Started request of Service Name : {} , Username : {}, Corrlation Id : {}, IP Address : {},TimeStamp : {}, Method name : {}",servicename,username,correlationid,ipaddress,timestamp,nameofCurrMethod);

        CollectionAdminRequest.List request = new CollectionAdminRequest.List();
        solrClient = solrAPIAdapter.getSolrClient(solrURL);

        GetListItemsResponseDTO getListItemsResponseDTO=new GetListItemsResponseDTO();
        try {
            CollectionAdminResponse response = request.process(solrClient);

            getListItemsResponseDTO.setItems(TypeCastingUtil.castToListOfStrings(response.getResponse().get("collections")));
            getListItemsResponseDTO.setStatusCode(200);
            getListItemsResponseDTO.setMessage("Successfully retrieved all tables");
            logger.info("-----------Successfully response of Service Name : {}, Username : {}, Corrlation Id : {} , IP Address : {}, TimeStamp : {}, Method name : {}",servicename,username,correlationid,ipaddress,timestamp,nameofCurrMethod);

        } catch (Exception e) {
            logger.error(e.toString());
            getListItemsResponseDTO.setStatusCode(400);
            getListItemsResponseDTO.setMessage("Unable to retrieve tables");
            logger.info("-----------Failed response of Service Name : {}, Username : {}, Corrlation Id : {} , IP Address : {}, TimeStamp : {}, Method name : {}",servicename,username,correlationid,ipaddress,timestamp,nameofCurrMethod);
        }
        return getListItemsResponseDTO;
	}
	

	@Override
	public GetListItemsResponseDTO getConfigSets() {
		String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
		String correlationid = correlationID.generateUniqueCorrelationId();
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.set(CorrelationID.CORRELATION_ID_HEADER_NAME, correlationid); 	
		String ipaddress=request.getRemoteAddr();
		String timestamp=utc.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        logger.info("--------Started request of Service Name : {} , Username : {}, Corrlation Id : {}, IP Address : {} , TimeStamp : {}, Method name : {}",servicename,username,correlationid,ipaddress,timestamp,nameofCurrMethod);

		solrClient = solrAPIAdapter.getSolrClient(solrURL);
		ConfigSetAdminRequest.List configSetRequest = new ConfigSetAdminRequest.List();
		
		GetListItemsResponseDTO getListItemsResponseDTO = new GetListItemsResponseDTO();
		try {
			ConfigSetAdminResponse configSetResponse = configSetRequest.process(solrClient);
			NamedList<Object> configResponseObjects = configSetResponse.getResponse();
			getListItemsResponseDTO.setItems(TypeCastingUtil.castToListOfStrings(configResponseObjects.get("configSets")));
			getListItemsResponseDTO.setStatusCode(200);
			getListItemsResponseDTO.setMessage("Successfully retrieved all config sets");
			logger.info("-----------Successfully response Username : {}, Corrlation Id : {}, IP Address : {} , TimeStamp : {}, Method name : {}",username,correlationid,ipaddress,timestamp,nameofCurrMethod);
		} catch(Exception e) {
			getListItemsResponseDTO.setStatusCode(400);
			getListItemsResponseDTO.setMessage("Configsets could not be retrieved. Error occured");
			logger.info("-----------Failed response Username : {}, Corrlation Id : {}, IP Address : {} , TimeStamp : {}, Method name : {}",username,correlationid,ipaddress,timestamp,nameofCurrMethod);
			logger.error("Error caused while retrieving configsets. Exception: ", e);
		}
		return getListItemsResponseDTO;
	}


	@Override
	public ApiResponseDTO createConfigSet(ConfigSetDTO configSetDTO) {
		String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
		String correlationid = correlationID.generateUniqueCorrelationId();
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.set(CorrelationID.CORRELATION_ID_HEADER_NAME, correlationid); 	
		String ipaddress=request.getRemoteAddr();
		String timestamp=utc.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        logger.info("--------Started request of Service Name : {} , Username : {}, Corrlation Id : {}, IP Address : {} , TimeStamp : {}, Method name : {}",servicename,username,correlationid,ipaddress,timestamp,nameofCurrMethod);

		solrClient = solrAPIAdapter.getSolrClient(solrURL);
		ConfigSetAdminRequest.Create configSetRequest = new ConfigSetAdminRequest.Create();	
		ApiResponseDTO apiResponseDTO = new ApiResponseDTO();
		
		configSetRequest.setBaseConfigSetName(configSetDTO.getBaseConfigSetName());
		configSetRequest.setConfigSetName(configSetDTO.getConfigSetName());
		/** configSetRequest.setNewConfigSetProperties(new Properties(969)); */
		configSetRequest.setMethod(METHOD.POST);

		try {
			/**
			 * Authenticate in order to access @schema_designer API 
			 */
			configSetRequest.setBasicAuthCredentials(basicAuthUsername, basicAuthPassword);
			configSetRequest.process(solrClient);
			apiResponseDTO = new ApiResponseDTO(
					200, 
					"ConfigSet is created successfully");
			logger.info("-----------Successfully response Username : {}, Corrlation Id : {}, IP Address : {} , TimeStamp : {}, Method name : {}",username,correlationid,ipaddress,timestamp,nameofCurrMethod);
		} catch(Exception e) {
			apiResponseDTO.setResponseMessage("ConfigSet could not be created");
			apiResponseDTO.setResponseStatusCode(400);
			logger.error("Error caused while creating ConfigSet. Exception: ", e);
			logger.info("-----------Failed response Username : {}, Corrlation Id : {}, Username : {}, IP Address : {} , TimeStamp : {}, Method name : {}",username,correlationid,ipaddress,timestamp,nameofCurrMethod);
		}
		return apiResponseDTO;
	}


	@Override
	public ApiResponseDTO createTableIfNotPresent(ManageTableDTO manageTableDTO,String correlationid,String ipaddress) {	
		String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.set(CorrelationID.CORRELATION_ID_HEADER_NAME, correlationid); 
		
		String timestamp=utc.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        logger.info("--------Started request of Service Name : {} , Username : {}, Corrlation Id : {}, IP Address : {} , TimeStamp : {}, Method name : {}",servicename,username,correlationid,ipaddress,timestamp,nameofCurrMethod);

		if(isTableExists(manageTableDTO.getTableName()))
			throw new BadRequestOccurredException(400, 
					manageTableDTO.getTableName()+" table already exists");
		
		if(!isConfigSetExists(manageTableDTO.getSchemaName())) {
			// Create Configset if not present
			logger.debug("{} configset is not present, creating..", manageTableDTO.getSchemaName());
			ConfigSetDTO configSetDTO = new ConfigSetDTO(
					baseConfigSet, 
					manageTableDTO.getSchemaName());
			createConfigSet(configSetDTO);
		}
		// Configset is present, proceed
		ApiResponseDTO apiResponseDTO = createTable(manageTableDTO);
		if(apiResponseDTO.getResponseStatusCode()==200) {
			// Add schemaAttributes
			TableSchemaDTO tableSchemaDTO = new TableSchemaDTO(
					manageTableDTO.getTableName(), 
					manageTableDTO.getSchemaName(), 
					manageTableDTO.getAttributes());
			TableSchemaResponseDTO tableSchemaResponseDTO = addSchemaAttributes(tableSchemaDTO);
			apiResponseDTO.setResponseStatusCode(tableSchemaResponseDTO.getStatusCode());
			apiResponseDTO.setResponseMessage(tableSchemaResponseDTO.getMessage());
		}
		logger.info("-----------Successfully response Corrlation Id : {}, Username : {}, IP Address : {} , TimeStamp : {}, Method name : {}",username,correlationid,ipaddress,timestamp,nameofCurrMethod);
		return apiResponseDTO;
	}


	@Override
	public ApiResponseDTO deleteConfigSet(String configSetName) {
		String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
		String correlationid = correlationID.generateUniqueCorrelationId();
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.set(CorrelationID.CORRELATION_ID_HEADER_NAME, correlationid); 	
		String ipaddress=request.getRemoteAddr();
		String timestamp=utc.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        logger.info("--------Started request of Service Name : {} , Username : {}, Corrlation Id : {}, IP Address : {} , TimeStamp : {}, Method name : {}",servicename,username,correlationid,ipaddress,timestamp,nameofCurrMethod);

		solrClient = solrAPIAdapter.getSolrClient(solrURL);
		ConfigSetAdminRequest.Delete configSetRequest = new ConfigSetAdminRequest.Delete();
		
		ApiResponseDTO apiResponseDTO = new ApiResponseDTO();
		configSetRequest.setMethod(METHOD.DELETE);
		configSetRequest.setConfigSetName(configSetName);
		try {
			configSetRequest.setBasicAuthCredentials(basicAuthUsername, basicAuthPassword);
			configSetRequest.process(solrClient);
			apiResponseDTO = new ApiResponseDTO(
					200, 
					"ConfigSet got deleted successfully");
			logger.info("-----------Successfully response Username : {}, Corrlation Id : {}, IP Address : {} , TimeStamp : {}, Method name : {}",username,correlationid,ipaddress,timestamp,nameofCurrMethod);
		} catch (Exception e) {
			apiResponseDTO.setResponseMessage("ConfigSet could not be deleted");
			apiResponseDTO.setResponseStatusCode(401);
			logger.error("Error occured while deleting Config set. Exception: ", e);
			logger.info("-----------Failed response Username : {}, Corrlation Id : {}, IP Address : {} , TimeStamp : {}, Method name : {}",username,correlationid,ipaddress,timestamp,nameofCurrMethod);
		}
		return apiResponseDTO;
	}


	@Override
	public ApiResponseDTO deleteTable(String tableName,String correlationid,String ipaddress) {
		String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.set(CorrelationID.CORRELATION_ID_HEADER_NAME, correlationid); 	
		String timestamp=utc.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        logger.info("--------Started request of Service Name : {} , Username : {}, Corrlation Id : {}, IP Address : {} , TimeStamp : {}, Method name : {}",servicename,username,correlationid,ipaddress,timestamp,nameofCurrMethod);

		if(!isTableExists(tableName))
			throw new ContentNotFoundException(404, 
					String.format(TABLE_NOT_FOUND_MSG, tableName));
		
		// Delete table
        CollectionAdminRequest.Delete request = CollectionAdminRequest.deleteCollection(tableName);
        CollectionAdminRequest.DeleteAlias deleteAliasRequest=CollectionAdminRequest.deleteAlias(tableName);
        solrClient = new HttpSolrClient.Builder(solrURL).build();
        
        ApiResponseDTO apiResponseDTO=new ApiResponseDTO();
        try {
        	request.setBasicAuthCredentials(basicAuthUsername, basicAuthPassword);
        	deleteAliasRequest.setBasicAuthCredentials(basicAuthUsername, basicAuthPassword);
            request.process(solrClient);
            deleteAliasRequest.process(solrClient);

            apiResponseDTO.setResponseStatusCode(200);
            apiResponseDTO.setResponseMessage("Table: "+tableName+", is successfully deleted");
            logger.info("-----------Successfully response Username : {}, Corrlation Id : {}, IP Address : {} , TimeStamp : {}, Method name : {}",username,correlationid,ipaddress,timestamp,nameofCurrMethod);
        } catch (Exception e) {
            logger.error("Exception occurred: ", e);
            apiResponseDTO.setResponseStatusCode(400);
            apiResponseDTO.setResponseMessage("Unable to delete table: "+tableName);
            logger.info("-----------Failed response Username : {}, Corrlation Id : {}, IP Address : {} , TimeStamp : {}, Method name : {}",username,correlationid,ipaddress,timestamp,nameofCurrMethod);
        }
        
        // Delete configSet attached to the table
		/*
		 * String configSetName = ""; if(apiResponseDTO.getResponseStatusCode()==200)
		 * apiResponseDTO = deleteConfigSet(configSetName);
		 */
        return apiResponseDTO;
	}


	@Override
	public ApiResponseDTO updateTableSchema(String tableName, TableSchemaDTO tableSchemaDTO,String correlationid,String ipaddress) {
		String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.set(CorrelationID.CORRELATION_ID_HEADER_NAME, correlationid); 
		
		String timestamp=utc.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        logger.info("--------Started request of Service Name : {} , Username : {}, Corrlation Id : {}, IP Address : {} , TimeStamp : {}, Method name : {}",servicename,username,correlationid,ipaddress,timestamp,nameofCurrMethod);

		return updateSchemaAttributes(tableSchemaDTO);
	}


	@Override
	public ApiResponseDTO addAliasTable(String tableOriginalName, String tableAlias) {
		String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
		String correlationid = correlationID.generateUniqueCorrelationId();
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.set(CorrelationID.CORRELATION_ID_HEADER_NAME, correlationid); 	
		String ipaddress=request.getRemoteAddr();
		String timestamp=utc.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        logger.info("--------Started request of Service Name : {} , Username : {}, Corrlation Id : {}, IP Address : {} , TimeStamp : {}, Method name : {}",servicename,username,correlationid,ipaddress,timestamp,nameofCurrMethod);

        CollectionAdminRequest.Rename request = CollectionAdminRequest.renameCollection(tableOriginalName,tableAlias);
        solrClient = new HttpSolrClient.Builder(solrURL).build();
        
        ApiResponseDTO apiResponseDTO=new ApiResponseDTO();
        try {
        	request.setBasicAuthCredentials(basicAuthUsername, basicAuthPassword);
            request.process(solrClient);
            apiResponseDTO.setResponseStatusCode(200);
            apiResponseDTO.setResponseMessage("Successfully renamed Solr Collection: "+tableOriginalName+" to "+tableAlias);
            logger.info("-----------Successfully response Username : {}, Corrlation Id : {}, IP Address : {} , TimeStamp : {}, Method name : {}",username,correlationid,ipaddress,timestamp,nameofCurrMethod);
        } catch (Exception e) {
            logger.error(e.toString());
            apiResponseDTO.setResponseStatusCode(400);
            apiResponseDTO.setResponseMessage("Unable to rename Solr Collection: "+tableOriginalName+". Exception.");
            logger.info("-----------Failed response Username : {}, Corrlation Id : {}, IP Address : {} , TimeStamp : {}, Method name : {}",username,correlationid,ipaddress,timestamp,nameofCurrMethod);
        }
        return apiResponseDTO;
	}


	// AUXILIARY methods implementations >>>>>>>>>>>>>>>>>>
	@Override
	public boolean isConfigSetExists(String configSetName) {
		String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
		String correlationid = correlationID.generateUniqueCorrelationId();
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.set(CorrelationID.CORRELATION_ID_HEADER_NAME, correlationid); 	
		String ipaddress=request.getRemoteAddr();
		String timestamp=utc.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        logger.info("--------Started request of Service Name : {} , Username : {}, Corrlation Id : {}, IP Address : {} , TimeStamp : {}, Method name : {}",servicename,username,correlationid,ipaddress,timestamp,nameofCurrMethod);

		GetListItemsResponseDTO configSets = getConfigSets();
		if(configSetName != null) {
			logger.info("-----------Successfully response Username : {}, Corrlation Id : {}, IP Address : {} , TimeStamp : {}, Method name : {}",username,correlationid,ipaddress,timestamp,nameofCurrMethod);
			return configSets.getItems().contains(configSetName);
		}
		else {
			logger.info("-----------Failed response Username : {}, Corrlation Id : {}, IP Address : {} , TimeStamp : {}, Method name : {}",username,correlationid,ipaddress,timestamp,nameofCurrMethod);
			throw new NullPointerOccurredException(404, "Could not fetch any configset, null returned");
		}
	}


	@Override
	public boolean isTableExists(String tableName) {
		String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
		String correlationid = correlationID.generateUniqueCorrelationId();
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.set(CorrelationID.CORRELATION_ID_HEADER_NAME, correlationid); 	
		String ipaddress=request.getRemoteAddr();
		String timestamp=utc.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        logger.info("--------Started request of Service Name : {} , Username : {}, Corrlation Id : {}, IP Address : {} , TimeStamp : {}, Method name : {}",servicename,username,correlationid,ipaddress,timestamp,nameofCurrMethod);

        CollectionAdminRequest.List request = new CollectionAdminRequest.List();
        solrClient = new HttpSolrClient.Builder(solrURL).build();
        try {
            CollectionAdminResponse response = request.process(solrClient);
            List<String> allTables=TypeCastingUtil.castToListOfStrings(response.getResponse().get("collections"));
            logger.info("-----------Successfully response Username : {}, Corrlation Id : {}, IP Address : {} , TimeStamp : {}, Method name : {}",username,correlationid,ipaddress,timestamp,nameofCurrMethod);
            return allTables.contains(tableName);
        } catch (Exception e) {
            logger.error(e.toString());
            logger.info("-----------Failed response Username : {}, Corrlation Id : {}, IP Address : {} , TimeStamp : {}, Method name : {}",username,correlationid,ipaddress,timestamp,nameofCurrMethod);
            throw new BadRequestOccurredException(400, "Table Search operation could not be completed");
        }
	}


	@Override
	public ApiResponseDTO createTable(ManageTableDTO manageTableDTO) {
		logger.info("creating table..");
		String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
		String correlationid = correlationID.generateUniqueCorrelationId();
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.set(CorrelationID.CORRELATION_ID_HEADER_NAME, correlationid); 	
		String ipaddress=request.getRemoteAddr();
		String timestamp=utc.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        logger.info("--------Started request of Service Name : {} , Username : {}, Corrlation Id : {}, IP Address : {} , TimeStamp : {}, Method name : {}",servicename,username,correlationid,ipaddress,timestamp,nameofCurrMethod);

        ApiResponseDTO apiResponseDTO=new ApiResponseDTO();

        List<CapacityPlanProperties.Plan> capacityPlans = capacityPlanProperties.getPlans();
        CapacityPlanProperties.Plan selectedCapacityPlan=null;

        for (CapacityPlanProperties.Plan capacityPlan : capacityPlans) {
            if(capacityPlan.getSku().equals(manageTableDTO.getSku())){
                selectedCapacityPlan=capacityPlan;
            }
        }

        if(selectedCapacityPlan==null){
            //INVALD SKU
            apiResponseDTO.setResponseStatusCode(400);
            apiResponseDTO.setResponseMessage("Invalid SKU: "+manageTableDTO.getSku());
            return apiResponseDTO;
        }

        CollectionAdminRequest.Create request = CollectionAdminRequest.createCollection(
        		manageTableDTO.getTableName(), manageTableDTO.getSchemaName(), 
        		selectedCapacityPlan.getShards(), 
        		selectedCapacityPlan.getReplicas());
        solrClient = new HttpSolrClient.Builder(solrURL).build();

        request.setMaxShardsPerNode(selectedCapacityPlan.getShards()*selectedCapacityPlan.getReplicas());
        try {
        	logger.info("Going to process TABLE CREATE request!!");
        	request.setBasicAuthCredentials(basicAuthUsername, basicAuthPassword);
            request.process(solrClient);
            apiResponseDTO.setResponseStatusCode(200);
            apiResponseDTO.setResponseMessage("Successfully created table: "+manageTableDTO.getTableName());
            logger.info("-----------Successfully response Username : {}, Corrlation Id : {}, IP Address : {} , TimeStamp : {}, Method name : {}",username,correlationid,ipaddress,timestamp,nameofCurrMethod);
        } catch (Exception e) {
            logger.error(e.toString());
            apiResponseDTO.setResponseStatusCode(400);
            apiResponseDTO.setResponseMessage("Unable to create table: "+manageTableDTO.getTableName()+". Exception.");
            logger.info("-----------Failed response Username : {}, Corrlation Id : {}, IP Address : {} , TimeStamp : {}, Method name : {}",username,correlationid,ipaddress,timestamp,nameofCurrMethod);
        }
        return apiResponseDTO;
	}


	@Override
	public TableSchemaResponseDTO addSchemaAttributes(TableSchemaDTO newTableSchemaDTO) {
		logger.debug("Add schema attributes");
		String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
		String correlationid = correlationID.generateUniqueCorrelationId();
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.set(CorrelationID.CORRELATION_ID_HEADER_NAME, correlationid); 
		String ipaddress=request.getRemoteAddr();
		String timestamp=utc.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        logger.info("--------Started request of Service Name : {} , Username : {}, Corrlation Id : {}, IP Address : {} , TimeStamp : {}, Method name : {}",servicename,username,correlationid,ipaddress,timestamp,nameofCurrMethod);

		solrClient = solrAPIAdapter.getSolrClientWithTable(solrURL, newTableSchemaDTO.getTableName());
		SchemaRequest schemaRequest = new SchemaRequest();
		TableSchemaResponseDTO tableSchemaResponseDTO = new TableSchemaResponseDTO();
		
		SchemaResponseDTO schemaResponseDTOBefore = new SchemaResponseDTO();
		SchemaResponseDTO schemaResponseDTOAfter = new SchemaResponseDTO();
		String schemaName = "";
		String errorCausingField = null;
		String payloadOperation = "";
		try {
			// logic
			schemaRequest.setBasicAuthCredentials(basicAuthUsername, basicAuthPassword);
			SchemaResponse schemaResponse = schemaRequest.process(solrClient);
			schemaResponseDTOBefore.setStatusCode(200);
			
			SchemaRepresentation retrievedSchema = schemaResponse.getSchemaRepresentation();
			schemaName = retrievedSchema.getName();
			List<Map<String, Object>> schemaFields = schemaResponse.getSchemaRepresentation().getFields();
	
			// Add new fields present in the Target Schema to the given collection schema
			List<SchemaFieldDTO> newAttributes = newTableSchemaDTO.getAttributes();
			SchemaFieldDTO[] newSolrFieldDTOs = newAttributes.toArray(new SchemaFieldDTO[0]);
			logger.debug("\nTarget Schema fields : {}", (Object[]) newSolrFieldDTOs);
			// ####### Add Schema Fields logic #######
			UpdateResponse addFieldResponse;
			NamedList<Object> schemaResponseAddFields = new NamedList<>();
			payloadOperation = "SchemaRequest.AddField";
			boolean newFieldFound = false;
			for(SchemaFieldDTO fieldDto : newSolrFieldDTOs) {
				boolean isPresent = false;
				for(Map<String, Object> field: schemaFields) {
					if(field.containsKey(fieldDto.getName())) {
						isPresent = true;
						break;
					}
				}
				if(!isPresent)
					newFieldFound = true;
			}
			if(!newFieldFound) {
				schemaResponseDTOAfter.setStatusCode(400);
			}
			for(SchemaFieldDTO fieldDto : newSolrFieldDTOs) {
				if(!TableSchemaParser.validateSchemaField(fieldDto)) {
					logger.debug("Validate SolrFieldDTO before updating the current schema- {}", schemaName);
					schemaResponseDTOAfter.setStatusCode(400);
					break;
				}
				errorCausingField = fieldDto.getName();
				Map<String, Object> newField = new HashMap<>();
				newField.put("name", fieldDto.getName());
				newField.put("type", SchemaFieldType.fromEnumToString(fieldDto.getType()));
				newField.put(REQUIRED, fieldDto.isRequired());
				newField.put(STORED, fieldDto.isStorable());
				newField.put(MULTIVALUED, fieldDto.isMultiValue());

				SchemaRequest.AddField addFieldRequest = new SchemaRequest.AddField(newField);
				addFieldResponse = addFieldRequest.process(solrClient);
				schemaResponseDTOAfter.setStatusCode(200);
				
				schemaResponseAddFields.add(fieldDto.getName(), addFieldResponse.getResponse());
			}
			tableSchemaResponseDTO.setStatusCode(200);
			tableSchemaResponseDTO.setMessage("Schema is created successfully");
			logger.debug("Logging newly added fields' responses--");
			for(Object field: schemaResponseAddFields) {
				logger.debug("### Added Field Response : {}", field);
			}
			logger.info("-----------Successfully response Username : {}, Corrlation Id : {}, IP Address : {} , TimeStamp : {}, Method name : {}",username,correlationid,ipaddress,timestamp,nameofCurrMethod);
		} catch (SolrServerException | IOException e) {
			schemaResponseDTOAfter.setStatusCode(400);
			tableSchemaResponseDTO.setStatusCode(400);
			tableSchemaResponseDTO.setMessage(SCHEMA_UPDATE_SUCCESS);
			logger.error(SOLR_SCHEMA_EXCEPTION_MSG, payloadOperation, errorCausingField);
			logger.debug(e.toString());
			logger.info("-----------Failed response Username : {}, Corrlation Id : {} , TimeStamp : {}, Method name : {}",username,correlationid,ipaddress,timestamp,nameofCurrMethod);
		} catch (SolrException e) {
			schemaResponseDTOAfter.setStatusCode(400);
			tableSchemaResponseDTO.setStatusCode(400);
			tableSchemaResponseDTO.setMessage("Schema attributes could not be added to the table");
			logger.error(SOLR_EXCEPTION_MSG+" So schema fields can't be found/deleted!", newTableSchemaDTO.getTableName());
			logger.debug(e.toString());
			logger.info("-----------Failed response Username : {}, Corrlation Id : {}, IP Address : {} , TimeStamp : {}, Method name : {}",username,correlationid,ipaddress,timestamp,nameofCurrMethod);
		} 
		return tableSchemaResponseDTO;
	}


	@Override
	public ApiResponseDTO updateSchemaAttributes(TableSchemaDTO newTableSchemaDTO) {
		logger.debug("Update Solr Schema");
		String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
		String correlationid = correlationID.generateUniqueCorrelationId();
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.set(CorrelationID.CORRELATION_ID_HEADER_NAME, correlationid); 	
		String ipaddress=request.getRemoteAddr();
		String timestamp=utc.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        logger.info("--------Started request of Service Name : {} , Username : {}, Corrlation Id : {}, IP Address : {} , TimeStamp : {}, Method name : {}",servicename,username,correlationid,ipaddress,timestamp,nameofCurrMethod);

		SchemaRequest schemaRequest = new SchemaRequest();
		HttpSolrClient solrClientUpdate = solrAPIAdapter.getSolrClientWithTable(solrURL, newTableSchemaDTO.getTableName());
		ApiResponseDTO apiResponseDTO = new ApiResponseDTO();
		
		SchemaResponseDTO schemaResponseDTOBefore = new SchemaResponseDTO();
		SchemaResponseDTO schemaResponseDTOAfter = new SchemaResponseDTO();
		String errorCausingField = null;
		String payloadOperation = "";
		try {
			schemaRequest.setBasicAuthCredentials(basicAuthUsername, basicAuthPassword);
			SchemaResponse schemaResponse = schemaRequest.process(solrClientUpdate);
			schemaResponseDTOBefore.setStatusCode(200);
		
			List<Map<String, Object>> schemaFields = schemaResponse.getSchemaRepresentation().getFields();
			int numOfFields = schemaFields.size();
			logger.debug("Total number of fields: {}", numOfFields);
			
			// Get all fields from incoming(from req Body) schemaDTO
			SchemaFieldDTO[] newSchemaFields = newTableSchemaDTO.getAttributes().toArray(new SchemaFieldDTO[0]);
			List<Map<String, Object>> targetSchemafields = TableSchemaParser.parseSchemaFieldDtosToListOfMaps(newTableSchemaDTO);
			// Validate Solr Schema Fields
			Map<String, Object> validationEntry = targetSchemafields.get(0);
			if(validationEntry.containsKey(VALIDATED)) {
				Object validatedFields = validationEntry.get(VALIDATED);
				if(validatedFields.equals(false))
					throw new SolrSchemaValidationException("Target Schema Fields validation falied!");
			}
				
			int totalUpdatesRequired = newSchemaFields.length;
			
			// Update Schema Logic
			UpdateResponse updateFieldsResponse;
			NamedList<Object> schemaResponseUpdateFields = new NamedList<>();
			payloadOperation = "SchemaRequest.ReplaceField";
			int updatedFields = 0;
			for(Map<String, Object> currField: targetSchemafields) {
				errorCausingField = (String) currField.get("name");
				// Pass all fieldAttributes to be updated
				SchemaRequest.ReplaceField updateFieldsRequest = new SchemaRequest.ReplaceField(currField);
				updateFieldsResponse = updateFieldsRequest.process(solrClientUpdate);
				schemaResponseDTOAfter.setStatusCode(200);
				
				schemaResponseUpdateFields.add((String) currField.get("name"), updateFieldsResponse.getResponse());
				updatedFields++;
				logger.debug("Field- {} is successfully updated", currField.get("name"));
			}
			apiResponseDTO.setResponseStatusCode(200);
			apiResponseDTO.setResponseMessage(SCHEMA_UPDATE_SUCCESS);
			// Compare required Vs Updated Fields
			logger.debug("Total field updates required in the current schema: {}", totalUpdatesRequired);
			logger.debug("Total fields updated in the current schema: {}", updatedFields);
			logger.info("-----------Successfully response Username : {}, Corrlation Id : {}, IP Address : {} , TimeStamp : {}, Method name : {}",username,correlationid,ipaddress,timestamp,nameofCurrMethod);

		} catch (SolrServerException | IOException e) {
			logger.error(SOLR_SCHEMA_EXCEPTION_MSG, payloadOperation, errorCausingField);
			logger.debug(e.toString());
		} catch (NullPointerException e) {
			schemaResponseDTOAfter.setStatusCode(400);
			apiResponseDTO.setResponseStatusCode(200);
			apiResponseDTO.setResponseMessage(SCHEMA_UPDATE_SUCCESS);
			logger.error("Null value detected!", e);
			logger.debug(e.toString());
			logger.info("-----------Failed response Corrlation Id : {}, IP Address : {} and Method name : {}",username,correlationid,ipaddress,timestamp,nameofCurrMethod);
		} catch (SolrException e) {
			apiResponseDTO.setResponseStatusCode(400);
			apiResponseDTO.setResponseMessage("Schema could not be updated");
			logger.error(SOLR_EXCEPTION_MSG+" So schema fields can't be found/deleted!", newTableSchemaDTO.getTableName());
			logger.debug(e.toString());
			logger.info("-----------Failed response Username : {}, Corrlation Id : {}, IP Address : {} , TimeStamp : {}, Method name : {}",username,correlationid,ipaddress,timestamp,nameofCurrMethod);
		} catch (SolrSchemaValidationException e) {
			apiResponseDTO.setResponseStatusCode(400);
			apiResponseDTO.setResponseMessage("Schema could not be updated");
			logger.error("Error Message: {}", e.getMessage());
			logger.debug(e.toString());
			logger.info("-----------Failed response Username : {}, Corrlation Id : {}, IP Address : {} , TimeStamp : {}, Method name : {}",username,correlationid,ipaddress,timestamp,nameofCurrMethod);
		}
		return apiResponseDTO;
	}


	@Override
	public TableSchemaResponseDTO getTableSchema(String tableName) {
		logger.debug("Getting table schema");
		String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
		String correlationid = correlationID.generateUniqueCorrelationId();
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.set(CorrelationID.CORRELATION_ID_HEADER_NAME, correlationid); 	
		String ipaddress=request.getRemoteAddr();
		String timestamp=utc.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        logger.info("--------Started request of Service Name : {} , Username : {}, Corrlation Id : {}, IP Address : {} , TimeStamp : {}, Method name : {}",servicename,username,correlationid,ipaddress,timestamp,nameofCurrMethod);

		solrClient = solrAPIAdapter.getSolrClientWithTable(solrURL, tableName);
		SchemaRequest schemaRequest = new SchemaRequest();
		
		TableSchemaDTO tableSchemaDTO = new TableSchemaDTO();
		TableSchemaResponseDTO tableSchemaResponseDTO = new TableSchemaResponseDTO();
		String schemaName = "";
		String errorCausingField = null;
		String payloadOperation = "SchemaRequest";
		try {
			SchemaResponse schemaResponse = schemaRequest.process(solrClient);
			logger.debug("Get request has been processed. Setting status code = 200");
			tableSchemaResponseDTO.setStatusCode(200);
			
			SchemaRepresentation schemaRepresentation = schemaResponse.getSchemaRepresentation();
			schemaName = schemaRepresentation.getName();
			List<Map<String, Object>> schemaFields = schemaResponse.getSchemaRepresentation().getFields();
			int numOfFields = schemaFields.size();
			SchemaFieldDTO[] solrSchemaFieldDTOs = new SchemaFieldDTO[numOfFields];
			logger.debug("Total number of fields: {}", numOfFields);
			
			int schemaFieldIdx = 0;
			for(Map<String, Object> f: schemaFields) {
				
				// Prepare the SolrFieldDTO
				SchemaFieldDTO solrFieldDTO = new SchemaFieldDTO();
				solrFieldDTO.setName((String)f.get("name"));
				
				// Parse Field Type Object(String) to Enum
				String fieldTypeObj = (String) f.get("type");				
				SchemaFieldType solrFieldType = SchemaFieldType.fromObject(fieldTypeObj);
				
				solrFieldDTO.setType(solrFieldType);
				TableSchemaParser.setFieldsToDefaults(solrFieldDTO);
				TableSchemaParser.setFieldsAsPerTheSchema(solrFieldDTO, f);
				solrSchemaFieldDTOs[schemaFieldIdx] = solrFieldDTO;
				schemaFieldIdx++;
			}
			logger.debug("Total fields stored in attributes array: {}", schemaFieldIdx);
	
			tableSchemaDTO.setTableName(tableName);
			tableSchemaDTO.setSchemaName(schemaRepresentation.getName());
			tableSchemaDTO.setAttributes(Arrays.asList(solrSchemaFieldDTOs));
			// prepare response dto
			tableSchemaResponseDTO.setSchemaName(schemaName);
			tableSchemaResponseDTO.setTableName(tableName);
			tableSchemaResponseDTO.setAttributes(Arrays.asList(solrSchemaFieldDTOs));
			tableSchemaResponseDTO.setStatusCode(200);
			tableSchemaResponseDTO.setMessage("Schema is retrieved successfully");
			logger.info("-----------Successfully response Username : {}, Corrlation Id : {}, IP Address : {} , TimeStamp : {}, Method name : {}",username,correlationid,ipaddress,timestamp,nameofCurrMethod);
		} catch (SolrServerException | IOException e) {
			tableSchemaResponseDTO.setStatusCode(400);
			logger.error(SOLR_SCHEMA_EXCEPTION_MSG, payloadOperation, errorCausingField);
			logger.debug(e.toString());
			logger.info("-----------Failed response Username : {}, Corrlation Id : {}, IP Address : {} , TimeStamp : {}, Method name : {}",username,correlationid,ipaddress,timestamp,nameofCurrMethod);
		} catch (SolrException e) {
			tableSchemaResponseDTO.setStatusCode(400);
			logger.error(SOLR_EXCEPTION_MSG, tableName);
			logger.debug(e.toString());
			logger.info("-----------Failed response Username : {}, Corrlation Id : {}, IP Address : {} , TimeStamp : {}, Method name : {}",username,correlationid,ipaddress,timestamp,nameofCurrMethod);
		}
		
		return tableSchemaResponseDTO;
	}
}
