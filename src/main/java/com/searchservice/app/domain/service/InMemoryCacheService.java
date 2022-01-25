package com.searchservice.app.domain.service;


import com.searchservice.app.domain.dto.document.DocumentDTO;
import com.searchservice.app.domain.dto.document.DocumentResponseDTO;
import com.searchservice.app.domain.dto.schema.FieldDTO;
import com.searchservice.app.domain.port.api.InMemoryCacheServicePort;
import com.searchservice.app.infrastructure.adaptor.SchemaAPIAdapter;
import com.searchservice.app.infrastructure.solrenum.SchemaFieldType;
import com.searchservice.app.rest.errors.SchemaValidationException;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient.RemoteExecutionException;
import org.apache.solr.client.solrj.request.schema.FieldTypeDefinition;
import org.apache.solr.client.solrj.request.schema.SchemaRequest;
import org.apache.solr.client.solrj.response.schema.SchemaRepresentation;
import org.apache.solr.client.solrj.response.schema.SchemaResponse;
import org.apache.solr.client.solrj.response.schema.SchemaResponse.UpdateResponse;
import org.apache.solr.common.SolrException;
import org.apache.solr.common.util.NamedList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("deprecation")
@Service
@Transactional

public class InMemoryCacheService implements InMemoryCacheServicePort {

	private final Logger log = LoggerFactory.getLogger(InMemoryCacheService.class);
	
//	@Value("techproducts")
//	private static String TEST1;
//	private static String DEFAULT_COLLECTION = "techproducts";
//	private static String DEFAULT_SOLR_CLOUD_COLLECTION = "gettingstarted3";
//	@Value("solr.client.url.static")
//	private static String TEST2;
//	private static String URL_STRING = "http://localhost:8984/solr/";
//	@Value("solr.client.url.cloud")
//	private static String TEST3;
//	private static String URL_STRING_SOLR_CLOUD = "http://localhost:8984/solr/";
	
	@Value("${base-solr-url}")
	String URL_STRING;
	
//	@Value("${base-solr-url-8984}")
//	String URL_STRING_SOLR_CLOUD;
	
	// call for solr client
	@Autowired
	SchemaAPIAdapter schemaAPIAdapter = new SchemaAPIAdapter();

	@Override
	public String getSolrClient(String tableName) {
		SolrClient solr = schemaAPIAdapter.getSolrClient(URL_STRING, tableName);
		CloudSolrClient solrCloud = schemaAPIAdapter.getCloudSolrClient(URL_STRING, tableName);
		solrCloud.setDefaultCollection(tableName);
		log.debug("@Solr client : {}", solr);
		log.debug("@Solr cloud client : {}", solrCloud);
		return "Solr Clients are successfully retrieved.";
	}

	
	@Override
	//@Cacheable(value = "solrcache",key = "#tableName")
	public DocumentResponseDTO get(String tableName, String name) {
		log.debug("Get Solr Schema: {}", name);

		CloudSolrClient solr = schemaAPIAdapter.getCloudSolrClient(URL_STRING, tableName);
		solr.setDefaultCollection(tableName);
		
		SchemaRequest schemaRequest = new SchemaRequest();
		DocumentDTO solrSchemaDTO = new DocumentDTO();
		DocumentResponseDTO solrSchemaResponseDTO = new DocumentResponseDTO();
		String schemaName = "";
		String errorCausingField = null;
		String payloadOperation = "SchemaRequest";
		try {
			SchemaResponse schemaResponse = schemaRequest.process(solr);
			log.debug("Get request has been processed. Setting status code = 200");
			solrSchemaResponseDTO.setStatusCode(200);
			
			// explore response content
			log.debug("\nSchema Response : {}", schemaResponse);
			log.debug("\nResponse header : {}", schemaResponse.getResponseHeader());
			log.debug("Response class : {}", schemaResponse.getResponseHeader().getClass());
			
			SchemaRepresentation schemaRepresentation = schemaResponse.getSchemaRepresentation();
			schemaName = schemaRepresentation.getName();
			name = schemaName;
			List<Map<String, Object>> schemaFields = schemaResponse.getSchemaRepresentation().getFields();
			int numOfFields = schemaFields.size();
			FieldDTO[] solrSchemaFieldDTOs = new FieldDTO[numOfFields];
			log.debug("Total number of fields: {}", numOfFields);
			
			int schemaFieldIdx = 0;
			for(Map<String, Object> f: schemaFields) {
				log.debug("\nField Object: {}", f);
				
				// Prepare the FieldDTO
				FieldDTO fieldDTO = new FieldDTO();
				fieldDTO.setName((String)f.get("name"));
				
				// Parse Field Type Object(String) to Enum
				String fieldTypeObj = (String) f.get("type");				
				SchemaFieldType schemaFieldType = SchemaFieldType.fromObject(fieldTypeObj);
				log.debug("SFT Enum val: {}", schemaFieldType);
				
				fieldDTO.setType(schemaFieldType);
				setFieldsToDefaults(fieldDTO);
				setFieldsAsPerTheSchema(fieldDTO, f);
				solrSchemaFieldDTOs[schemaFieldIdx] = fieldDTO;
				schemaFieldIdx++;
			}
			log.debug("Total fields stored in attributes array: {}", schemaFieldIdx);
			log.debug("Response class: {}", schemaResponse.getSchemaRepresentation().getClass());
			
			solrSchemaDTO.setTableName(tableName);
			solrSchemaDTO.setName(schemaRepresentation.getName());
			solrSchemaDTO.setAttributes(solrSchemaFieldDTOs);
			// prepare response dto
			solrSchemaResponseDTO.setName(schemaName);
			solrSchemaResponseDTO.setTableName(tableName);
			solrSchemaResponseDTO.setAttributes(solrSchemaFieldDTOs);
			solrSchemaResponseDTO.setStatusCode(200);
		} catch (SolrServerException e) {
			solrSchemaResponseDTO.setStatusCode(400);
			e.printStackTrace();
		} catch (IOException e) {
			solrSchemaResponseDTO.setStatusCode(400);
			e.printStackTrace();
		} catch (RemoteExecutionException e) {
			solrSchemaResponseDTO.setStatusCode(400);
			log.error("There's been an error in executing {} operation via schema API. "
					+ "Perhaps the target field- {} isn't present.", payloadOperation, errorCausingField);
			e.printStackTrace();
		} catch (SolrException e) {
			solrSchemaResponseDTO.setStatusCode(400);
			log.error("The collection - {} is Not Found in the Solr Cloud!", tableName);
			e.printStackTrace();
		}
		return solrSchemaResponseDTO;
	}
	
	@Override
	//@CachePut(value = "solrcache",key = "#tableName")
	public DocumentResponseDTO update(String tableName,
									  String name,
									  DocumentDTO documentDTO) {
		log.debug("Update Solr Schema: {}", name);
		log.debug("Target Schema: {}", documentDTO);
		
		SchemaRequest schemaRequest = new SchemaRequest();
		CloudSolrClient solr = schemaAPIAdapter.getCloudSolrClient(URL_STRING, tableName);
		solr.setDefaultCollection(tableName);
		
		DocumentResponseDTO solrSchemaResponseDTOBefore = new DocumentResponseDTO();
		DocumentResponseDTO solrSchemaResponseDTOAfter = new DocumentResponseDTO();
		String schemaName = "";
		String errorCausingField = null;
		String payloadOperation = "";
		try {
			SchemaResponse schemaResponse = schemaRequest.process(solr);
			solrSchemaResponseDTOBefore.setStatusCode(200);
			
			SchemaRepresentation retrievedSchema = schemaResponse.getSchemaRepresentation();
			schemaName = retrievedSchema.getName();
			solrSchemaResponseDTOBefore = get(tableName, schemaName);
			// explore response content
			log.debug("\nResponse header : {}", schemaResponse.getResponseHeader());
			log.debug("Response class : {}", schemaResponse.getResponseHeader().getClass());
			
			List<Map<String, Object>> schemaFields = schemaResponse.getSchemaRepresentation().getFields();
			int numOfFields = schemaFields.size();
			log.debug("Total number of fields: {}", numOfFields);
			
			// Get all fields from incoming(from req Body) schemaDTO
			FieldDTO[] newSchemaFields = documentDTO.getAttributes();
			List<Map<String, Object>> targetSchemafields = parseSchemaFieldDtosToListOfMaps(documentDTO);
			// Validate Solr Schema Fields
			Map<String, Object> validationEntry = targetSchemafields.get(0);
			if(validationEntry.containsKey("validated")) {
				Object validatedFields = validationEntry.get("validated");
				if(validatedFields.equals(false))
					throw new SchemaValidationException("Target Schema Fields validation falied!");
			}
				
			int totalUpdatesRequired = newSchemaFields.length;
			
			// Update Schema Logic
			UpdateResponse updateFieldsResponse = new UpdateResponse();
			NamedList<Object> schemaResponseUpdateFields = new NamedList<Object>();
			payloadOperation = "SchemaRequest.ReplaceField";
			int updatedFields = 0;
			for(Map<String, Object> currField: targetSchemafields) {
				errorCausingField = (String) currField.get("name");
				// Pass all fieldAttributes to be updated
				SchemaRequest.ReplaceField updateFieldsRequest = new SchemaRequest.ReplaceField(currField);
				updateFieldsResponse = updateFieldsRequest.process(solr);
				solrSchemaResponseDTOAfter.setStatusCode(200);
				
				schemaResponseUpdateFields.add((String) currField.get("name"), updateFieldsResponse.getResponse());
				updatedFields++;
				log.debug("Field- {} is successfully updated", currField.get("name"));
			}
			// Compare required Vs Updated Fields
			log.debug("Total field updates required in the current schema: {}", totalUpdatesRequired);
			log.debug("Total fields updated in the current schema: {}", updatedFields);
			log.debug("Logging newly added fields' responses--");
			for(Object field: schemaResponseUpdateFields) {
				log.debug("### Updated Field Response : {}", field);
			}
		} catch (SolrServerException e) {
			solrSchemaResponseDTOAfter.setStatusCode(400);
			e.printStackTrace();
		} catch (IOException e) {
			solrSchemaResponseDTOAfter.setStatusCode(400);
			e.printStackTrace();
		} catch (NullPointerException e) {
			solrSchemaResponseDTOAfter.setStatusCode(400);
			log.error("Null value detected!", e);
			e.printStackTrace();
		} catch (RemoteExecutionException e) {
			solrSchemaResponseDTOAfter.setStatusCode(400);
			log.error("There's been an error in executing {} operation via schema API. "
					+ "Perhaps the target field- {} isn't present.", payloadOperation, errorCausingField);
			e.printStackTrace();
		} catch (SolrException e) {
			solrSchemaResponseDTOAfter.setStatusCode(400);
			log.error("The collection- {} is Not Found in the Solr Cloud. So schema fields can't be found/deleted!", tableName);
			e.printStackTrace();
		} catch (SchemaValidationException e) {
			solrSchemaResponseDTOAfter.setStatusCode(400);
			log.error("Error Message: {}", e.getMessage());
			e.printStackTrace();
		}
		solrSchemaResponseDTOAfter = get(tableName, schemaName);
		log.debug("Schema for collection- {}, before CREATE: {}", tableName, solrSchemaResponseDTOBefore);
		log.debug("Schema for collection- {}, after CREATE: {}", tableName, solrSchemaResponseDTOAfter);
		return solrSchemaResponseDTOAfter;
	}
	
	@Override
	public DocumentResponseDTO create(String tableName,
									  String name,
									  DocumentDTO newDocumentDTO) {
		log.debug("Create Solr Schema: {}", name);

		CloudSolrClient solr = schemaAPIAdapter.getCloudSolrClient(URL_STRING, tableName);
		solr.setDefaultCollection(tableName);
		SchemaRequest schemaRequest = new SchemaRequest();
		
		DocumentResponseDTO solrSchemaResponseDTOBefore = new DocumentResponseDTO();
		DocumentResponseDTO solrSchemaResponseDTOAfter = new DocumentResponseDTO();
		String schemaName = "";
		String errorCausingField = null;
		String payloadOperation = "";
		try {
			// logic
			SchemaResponse schemaResponse = schemaRequest.process(solr);
			solrSchemaResponseDTOBefore.setStatusCode(200);
			
			SchemaRepresentation retrievedSchema = schemaResponse.getSchemaRepresentation();
			schemaName = retrievedSchema.getName();
			List<Map<String, Object>> schemaFields = schemaResponse.getSchemaRepresentation().getFields();
			solrSchemaResponseDTOBefore = get(tableName, "default-config");
			// explore response content
			log.debug("\nResponse header : {}", schemaResponse.getResponseHeader());
			log.debug("Response class : {}", schemaResponse.getResponseHeader().getClass());
			log.debug("\nDefault Schema fields : {}", schemaFields);
			
			// Add new fields present in the Target Schema to the given collection schema
			FieldDTO[] newFieldDTOS = newDocumentDTO.getAttributes();
			log.debug("\nTarget Schema fields : {}", (Object[]) newFieldDTOS);
			// ####### Add Schema Fields logic #######
			UpdateResponse addFieldResponse = new UpdateResponse();
			NamedList<Object> schemaResponseAddFields = new NamedList<>();
			payloadOperation = "SchemaRequest.AddField";
			boolean newFieldFound = false;
			for(FieldDTO fieldDto : newFieldDTOS) {
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
				solrSchemaResponseDTOAfter.setStatusCode(400);
			}
			for(FieldDTO fieldDto : newFieldDTOS) {
				if(!validateSchemaField(fieldDto)) {
					log.debug("Validate FieldDTO before updating the current schema- {}", schemaName);
					solrSchemaResponseDTOAfter.setStatusCode(400);
					break;
				}
				errorCausingField = fieldDto.getName();
				Map<String, Object> newField = new HashMap<>();
				newField.put("name", fieldDto.getName());
				newField.put("type", SchemaFieldType.fromEnumToString(fieldDto.getType()));
				newField.put("required", fieldDto.isRequired());
				newField.put("stored", fieldDto.isStorable());
				newField.put("multiValued", fieldDto.isMultiValue());
				/*
				 * newField.put("default", fieldDto.getDefault_()); 
				 * newField.put("filtered", fieldDto.isFilterable()); 
				 * newField.put("sorted", fieldDto.isSortable());
				 */

				SchemaRequest.AddField addFieldRequest = new SchemaRequest.AddField(newField);
				addFieldResponse = addFieldRequest.process(solr);
				solrSchemaResponseDTOAfter.setStatusCode(200);
				
				schemaResponseAddFields.add(fieldDto.getName(), addFieldResponse.getResponse());
			}
			log.debug("Logging newly added fields' responses--");
			for(Object field: schemaResponseAddFields) {
				log.debug("### Added Field Response : {}", field);
			}
		} catch (SolrServerException | IOException e) {
			solrSchemaResponseDTOAfter.setStatusCode(400);
			e.printStackTrace();
		} catch (RemoteExecutionException e) {
			solrSchemaResponseDTOAfter.setStatusCode(400);
			log.error("There's been an error in executing {} operation via schema API. Perhaps the target field- {} isn't present.", payloadOperation, errorCausingField);
			e.printStackTrace();
		} catch (SolrException e) {
			solrSchemaResponseDTOAfter.setStatusCode(400);
			log.error("The collection- {} is Not Found in the Solr Cloud. So schema fields can't be found/deleted!", tableName);
			e.printStackTrace();
		} 
		solrSchemaResponseDTOAfter = get(tableName, name);
		log.debug("Schema for collection- {}, before CREATE: {}", tableName, solrSchemaResponseDTOBefore);
		log.debug("Schema for collection- {}, after CREATE: {}", tableName, solrSchemaResponseDTOAfter);
		return solrSchemaResponseDTOAfter;
	}

	@Override
	//@CacheEvict(value="solrcache", key = "#tableName")
	public DocumentResponseDTO delete(String tableName, String name) {
		CloudSolrClient solr = schemaAPIAdapter.getCloudSolrClient(URL_STRING, tableName);
		solr.setDefaultCollection(tableName);
		
		SchemaRequest schemaRequest = new SchemaRequest();
		
		DocumentResponseDTO solrSchemaResponseDTOBefore = new DocumentResponseDTO();
		DocumentResponseDTO solrSchemaResponseDTOAfter = new DocumentResponseDTO();
		String schemaName = "";
		String errorCausingField = null;
		String payloadOperation = "";
		try {
			SchemaResponse schemaResponse = schemaRequest.process(solr);
			solrSchemaResponseDTOBefore.setStatusCode(200);
			
			SchemaRepresentation retrievedSchema = schemaResponse.getSchemaRepresentation();
			schemaName = retrievedSchema.getName();
			List<Map<String, Object>> schemaFields = retrievedSchema.getFields();
			solrSchemaResponseDTOBefore = get(tableName, schemaName);
			// API purpose
			log.debug("Delete fields of Schema - {} in the the collection - {}", schemaName, tableName);
			// explore response content
			log.debug("\nResponse header : {}", schemaResponse.getResponseHeader());
			log.debug("Response class : {}", schemaResponse.getResponseHeader().getClass());
			log.debug("\nRetrieved Schema fields : {}", schemaFields);
			
			// ####### Delete Schema Fields logic #######
			/*
			 * Delete all the fields of current Schema in the given collection
			 */
			UpdateResponse deleteFieldResponse = new UpdateResponse();
			NamedList<Object> schemaResponseDeleteFields = new NamedList<Object>();
			payloadOperation = "SchemaRequest.DeleteField";
			for (Map<String, Object> currField : schemaFields) {
				errorCausingField = (String) currField.get("name");
				/* 
				 * Define payload for DeleteField Operation 
				 */
				SchemaRequest.DeleteField deleteFieldRequest = new SchemaRequest.DeleteField((String) currField.get("name"));
				deleteFieldResponse = deleteFieldRequest.process(solr);
				schemaResponseDeleteFields.add((String) currField.get("name"), deleteFieldResponse.getResponse());
			}
			// Define payload for DeleteField Operation
			String targetFieldToDelete = "testField1";
			errorCausingField = targetFieldToDelete;
			SchemaRequest.DeleteField deleteFieldRequest = new SchemaRequest.DeleteField(targetFieldToDelete);
			deleteFieldResponse = deleteFieldRequest.process(solr);
			solrSchemaResponseDTOAfter.setStatusCode(200);
			
			schemaResponseDeleteFields.add(targetFieldToDelete, deleteFieldResponse.getResponse());

			log.debug("Logging all deleted fields' responses--");
			for(Object field: schemaResponseDeleteFields) {
				log.debug("### Added Field Response : {}", field);
			}
		} catch (SolrServerException | IOException e) {
			solrSchemaResponseDTOAfter.setStatusCode(400);
			e.printStackTrace();
		} catch (RemoteExecutionException e) {
			solrSchemaResponseDTOAfter.setStatusCode(400);
			log.error("There's been an error in executing {} operation via schema API. Perhaps the target field- {} isn't present.", payloadOperation, errorCausingField);
			e.printStackTrace();
		} catch (SolrException e) {
			solrSchemaResponseDTOAfter.setStatusCode(400);
			log.error("The collection- {} is Not Found in the Solr Cloud. So schema fields can't be found/deleted!", tableName);
			e.printStackTrace();
		} 
		solrSchemaResponseDTOAfter = get(tableName, schemaName);
		// Compare Pre-and-Post DELETE Operation
		log.debug("Schema for collection- {}, before DELETE: {}", tableName, solrSchemaResponseDTOBefore);
		log.debug("Schema for collection- {}, after DELETE: {}", tableName, solrSchemaResponseDTOAfter);
		return solrSchemaResponseDTOAfter;
	}
	
	@Override
	public List<FieldTypeDefinition> getSchemaFieldTypes(DocumentDTO documentDTO) {
		log.debug("get schema field types.");
		
		SchemaRequest schemaRequest = new SchemaRequest();
		SolrClient solr = new HttpSolrClient.Builder(URL_STRING+ documentDTO.getTableName()).build();
		List<FieldTypeDefinition> schemaFieldTypes = null;
		try {
			SchemaResponse schemaResponse = schemaRequest.process(solr);
			// explore response content
			log.debug("Response header : {}", schemaResponse.getResponseHeader());	
			schemaFieldTypes = schemaResponse.getSchemaRepresentation().getFieldTypes();
			int numOfFieldTypes = schemaResponse.getSchemaRepresentation().getFieldTypes().size();
			log.debug("Response schema size : {}", schemaFieldTypes.size());
			for(int i=0; i<numOfFieldTypes; i++) {
				log.debug("Field Types : {}", schemaFieldTypes.get(i).getAttributes());
			}
		} catch (SolrServerException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return schemaFieldTypes;
	}
	
	@Override
	public boolean validateSchemaField(FieldDTO fieldDTO) {
		log.debug("Validate schema field: {}", fieldDTO);
		boolean fieldValidated = true;
		String fieldName = fieldDTO.getName();
		SchemaFieldType fieldType = fieldDTO.getType();
		
		if(fieldName.length() < 1) {
			fieldValidated = false;
			log.debug("Invalid schema field name received: {}", fieldName);
		} else if(fieldType == null || !SchemaFieldType.doesExist(SchemaFieldType.fromEnumToString(fieldType))) {
			fieldValidated = false;
			log.debug("Invalid/Empty schema field type received: {}", fieldType);
		} else if(!validateSchemaFieldBooleanAttributes(fieldDTO)) {
			fieldValidated = false;
			log.debug("Invalid/Empty schema field boolean attributes received: {}", fieldDTO);
		}
		return fieldValidated;
	}

	@Override
	public boolean validateSchemaFieldBooleanAttributes(FieldDTO fieldDTO) {
		log.debug("Validate schema field boolean attributes: {}", fieldDTO);
		
		boolean fieldAttributesValidated = true;
		String invalidAttribute = "";
		if(fieldDTO.isRequired() != true && fieldDTO.isRequired() != false) {
			fieldAttributesValidated = false;
			invalidAttribute = "required";
		} else if(fieldDTO.isFilterable() != true && fieldDTO.isFilterable() != false) {
			fieldAttributesValidated = false;
			invalidAttribute = "filtered";
		} else if(fieldDTO.isMultiValue() != true && fieldDTO.isMultiValue() != false) {
			fieldAttributesValidated = false;
			invalidAttribute = "multValued";
		} else if(fieldDTO.isStorable() != true && fieldDTO.isStorable() != false) {
			fieldAttributesValidated = false;
			invalidAttribute = "stored";
		} else if(fieldDTO.isSortable() != true && fieldDTO.isSortable() != false) {
			fieldAttributesValidated = false;
			invalidAttribute = "sorted";
		}
		if(!fieldAttributesValidated)
			log.debug("Invalid entry for field attribute: \"{}\"", invalidAttribute);
		log.debug("All Schema field boolean attributes are valid");
		return fieldAttributesValidated;
	}

	@Override
	public void setFieldsAsPerTheSchema(FieldDTO fieldDTO, Map<String, Object> schemaField) {
		if(schemaField.containsKey("filtered"))
			fieldDTO.setFilterable((boolean)schemaField.get("filtered"));
		if(schemaField.containsKey("multiValued"))
			fieldDTO.setMultiValue((boolean)schemaField.get("multiValued"));;
		if(schemaField.containsKey("default"))
			fieldDTO.setDefault_((String)schemaField.get("default"));;
		if(schemaField.containsKey("required"))
			fieldDTO.setRequired((boolean)schemaField.get("required"));;
		if(schemaField.containsKey("sorted"))
			fieldDTO.setSortable((boolean)schemaField.get("sorted"));;
		if(schemaField.containsKey("stored"))
			fieldDTO.setStorable((boolean)schemaField.get("stored"));;
	}

	@Override
	public void setFieldsToDefaults(FieldDTO fieldDTO) {
		fieldDTO.setFilterable(false);
		fieldDTO.setMultiValue(false);
		fieldDTO.setDefault_("mydefault");
		fieldDTO.setRequired(false);
		fieldDTO.setSortable(false);
		fieldDTO.setStorable(true);
	}

	@Override
	public List<Map<String, Object>> parseSchemaFieldDtosToListOfMaps(DocumentDTO documentDTO) {
		List<Map<String, Object>> schemaFieldsList = new ArrayList<>();
		FieldDTO[] schemaFields = documentDTO.getAttributes();
		
		Map<String, Object> fieldDtoMap = new HashMap<String, Object>();
		for(FieldDTO fieldDto: schemaFields) {
			log.debug("Validate FieldDTO before parsing it- {}", fieldDto);
			if(!validateSchemaField(fieldDto)) {
				fieldDtoMap = new HashMap<>();
				fieldDtoMap.put("validated", false);
				return schemaFieldsList;
			}
			fieldDtoMap.put("name", fieldDto.getName());
			fieldDtoMap.put("type", SchemaFieldType.fromEnumToString(fieldDto.getType()));
			fieldDtoMap.put("stored", fieldDto.isStorable());
			fieldDtoMap.put("multiValued", fieldDto.isMultiValue());
			fieldDtoMap.put("required", fieldDto.isRequired());
			/* fieldDtoMap.put("default", fieldDto.getDefault_()); */
			schemaFieldsList.add(fieldDtoMap);
		}
		return schemaFieldsList;
	}
}
