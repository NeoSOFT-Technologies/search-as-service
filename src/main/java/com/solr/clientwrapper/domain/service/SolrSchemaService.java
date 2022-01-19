package com.solr.clientwrapper.domain.service;

import com.solr.clientwrapper.domain.dto.solr.SolrFieldDTO;
import com.solr.clientwrapper.domain.dto.solr.SolrSchemaDTO;
import com.solr.clientwrapper.domain.dto.solr.SolrSchemaResponseDTO;
import com.solr.clientwrapper.domain.port.api.SolrSchemaServicePort;
import com.solr.clientwrapper.infrastructure.Enum.SolrFieldType;
import com.solr.clientwrapper.infrastructure.adaptor.SolrSchemaAPIAdapter;
import com.solr.clientwrapper.rest.errors.SolrSchemaValidationException;
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

@Service
@Transactional
@SuppressWarnings({"deprecation", "unused"})
public class SolrSchemaService implements SolrSchemaServicePort {

	private final Logger log = LoggerFactory.getLogger(SolrSchemaService.class);
	
	@Value("techproducts")
	private static String TEST1;
	private static String DEFAULT_COLLECTION = "techproducts";
	private static String DEFAULT_SOLR_CLOUD_COLLECTION = "gettingstarted3";
	@Value("solr.client.url.static")
	private static String TEST2;
	private static String URL_STRING = "http://localhost:8984/solr/";
	@Value("solr.client.url.cloud")
	private static String TEST3;
	private static String URL_STRING_SOLR_CLOUD = "http://localhost:8984/solr/";
	
	// call for solr client
	@Autowired
	SolrSchemaAPIAdapter solrSchemaAPIAdapter = new SolrSchemaAPIAdapter();

	@Override
	public String getSolrClient(String tableName) {
		SolrClient solr = solrSchemaAPIAdapter.getSolrClient(URL_STRING, tableName);
		CloudSolrClient solrCloud = solrSchemaAPIAdapter.getCloudSolrClient(URL_STRING_SOLR_CLOUD, tableName);
		solrCloud.setDefaultCollection(tableName);
		log.debug("@Solr client : {}", solr);
		log.debug("@Solr cloud client : {}", solrCloud);
		return "Solr Clients are successfully retrieved.";
	}

	@Override
	public SolrSchemaResponseDTO get(String tableName) {
//		log.debug("Get Solr Schema: {}", name);

		CloudSolrClient solr = solrSchemaAPIAdapter.getCloudSolrClient(URL_STRING_SOLR_CLOUD, tableName);
		solr.setDefaultCollection(tableName);
		
		SchemaRequest schemaRequest = new SchemaRequest();
		SolrSchemaDTO solrSchemaDTO = new SolrSchemaDTO();
		SolrSchemaResponseDTO solrSchemaResponseDTO = new SolrSchemaResponseDTO();
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
//			name = schemaName;
			List<Map<String, Object>> schemaFields = schemaResponse.getSchemaRepresentation().getFields();
			int numOfFields = schemaFields.size();
			SolrFieldDTO[] solrSchemaFieldDTOs = new SolrFieldDTO[numOfFields];
			log.debug("Total number of fields: {}", numOfFields);
			
			int schemaFieldIdx = 0;
			for(Map<String, Object> f: schemaFields) {
				log.debug("\nField Object: {}", f);
				
				// Prepare the SolrFieldDTO
				SolrFieldDTO solrFieldDTO = new SolrFieldDTO();
				solrFieldDTO.setName((String)f.get("name"));
				
				// Parse Field Type Object(String) to Enum
				String fieldTypeObj = (String) f.get("type");				
				SolrFieldType solrFieldType = SolrFieldType.fromObject(fieldTypeObj);
				log.debug("SFT Enum val: {}", solrFieldType);
				
				solrFieldDTO.setType(solrFieldType);
				setFieldsToDefaults(solrFieldDTO);
				setFieldsAsPerTheSchema(solrFieldDTO, f);
				solrSchemaFieldDTOs[schemaFieldIdx] = solrFieldDTO;
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
			log.debug(e.toString());
		} catch (IOException e) {
			solrSchemaResponseDTO.setStatusCode(400);
			log.debug(e.toString());
		} catch (RemoteExecutionException e) {
			solrSchemaResponseDTO.setStatusCode(400);
			log.error("There's been an error in executing {} operation via schema API. "
					+ "Perhaps the target field- {} isn't present.", payloadOperation, errorCausingField);
			log.debug(e.toString());
		} catch (SolrException e) {
			solrSchemaResponseDTO.setStatusCode(400);
			log.error("The collection - {} is Not Found in the Solr Cloud!", tableName);
			log.debug(e.toString());
		}
		return solrSchemaResponseDTO;
	}
	
	@Override
	public SolrSchemaResponseDTO update(String tableName,
								SolrSchemaDTO newSolrSchemaDTO) {
//		log.debug("Update Solr Schema: {}", name);
		log.debug("Target Schema: {}", newSolrSchemaDTO);
		
		SchemaRequest schemaRequest = new SchemaRequest();
		CloudSolrClient solr = solrSchemaAPIAdapter.getCloudSolrClient(URL_STRING_SOLR_CLOUD, tableName);
		solr.setDefaultCollection(tableName);
		
		SolrSchemaResponseDTO solrSchemaResponseDTOBefore = new SolrSchemaResponseDTO();
		SolrSchemaResponseDTO solrSchemaResponseDTOAfter = new SolrSchemaResponseDTO();
		String schemaName = "";
		String errorCausingField = null;
		String payloadOperation = "";
		try {
			SchemaResponse schemaResponse = schemaRequest.process(solr);
			solrSchemaResponseDTOBefore.setStatusCode(200);
			
			SchemaRepresentation retrievedSchema = schemaResponse.getSchemaRepresentation();
			schemaName = retrievedSchema.getName();
			solrSchemaResponseDTOBefore = get(tableName);
			// explore response content
			log.debug("\nResponse header : {}", schemaResponse.getResponseHeader());
			log.debug("Response class : {}", schemaResponse.getResponseHeader().getClass());
			
			List<Map<String, Object>> schemaFields = schemaResponse.getSchemaRepresentation().getFields();
			int numOfFields = schemaFields.size();
			log.debug("Total number of fields: {}", numOfFields);
			
			// Get all fields from incoming(from req Body) schemaDTO
			SolrFieldDTO[] newSchemaFields = newSolrSchemaDTO.getAttributes();
			List<Map<String, Object>> targetSchemafields = parseSchemaFieldDtosToListOfMaps(newSolrSchemaDTO);
			// Validate Solr Schema Fields
			Map<String, Object> validationEntry = targetSchemafields.get(0);
			if(validationEntry.containsKey("validated")) {
				Object validatedFields = validationEntry.get("validated");
				if(validatedFields.equals(false))
					throw new SolrSchemaValidationException("Target Schema Fields validation falied!");
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
			log.debug(e.toString());
		} catch (IOException e) {
			solrSchemaResponseDTOAfter.setStatusCode(400);
			log.debug(e.toString());
		} catch (NullPointerException e) {
			solrSchemaResponseDTOAfter.setStatusCode(400);
			log.error("Null value detected!", e);
			log.debug(e.toString());
		} catch (RemoteExecutionException e) {
			solrSchemaResponseDTOAfter.setStatusCode(400);
			log.error("There's been an error in executing {} operation via schema API. "
					+ "Perhaps the target field- {} isn't present.", payloadOperation, errorCausingField);
			log.debug(e.toString());
		} catch (SolrException e) {
			solrSchemaResponseDTOAfter.setStatusCode(400);
			log.error("The collection- {} is Not Found in the Solr Cloud. So schema fields can't be found/deleted!", tableName);
			log.debug(e.toString());
		} catch (SolrSchemaValidationException e) {
			solrSchemaResponseDTOAfter.setStatusCode(400);
			log.error("Error Message: {}", e.getMessage());
			log.debug(e.toString());
		}
		solrSchemaResponseDTOAfter = get(tableName);
		log.debug("Schema for collection- {}, before CREATE: {}", tableName, solrSchemaResponseDTOBefore);
		log.debug("Schema for collection- {}, after CREATE: {}", tableName, solrSchemaResponseDTOAfter);
		return solrSchemaResponseDTOAfter;
	}
	
	@Override
	public SolrSchemaResponseDTO create(String tableName, 
								String name, 
								SolrSchemaDTO newSolrSchemaDTO) {
		log.debug("Create Solr Schema: {}", name);

		CloudSolrClient solr = solrSchemaAPIAdapter.getCloudSolrClient(URL_STRING_SOLR_CLOUD, tableName);
		solr.setDefaultCollection(tableName);
		SchemaRequest schemaRequest = new SchemaRequest();
		
		SolrSchemaResponseDTO solrSchemaResponseDTOBefore = new SolrSchemaResponseDTO();
		SolrSchemaResponseDTO solrSchemaResponseDTOAfter = new SolrSchemaResponseDTO();
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
			solrSchemaResponseDTOBefore = get(tableName);
			// explore response content
			log.debug("\nResponse header : {}", schemaResponse.getResponseHeader());
			log.debug("Response class : {}", schemaResponse.getResponseHeader().getClass());
			log.debug("\nDefault Schema fields : {}", schemaFields);
			
			// Add new fields present in the Target Schema to the given collection schema
			SolrFieldDTO[] newSolrFieldDTOs = newSolrSchemaDTO.getAttributes();
			log.debug("\nTarget Schema fields : {}", (Object[]) newSolrFieldDTOs);
			// ####### Add Schema Fields logic #######
			UpdateResponse addFieldResponse = new UpdateResponse();
			NamedList<Object> schemaResponseAddFields = new NamedList<>();
			payloadOperation = "SchemaRequest.AddField";
			boolean newFieldFound = false;
			for(SolrFieldDTO fieldDto : newSolrFieldDTOs) {
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
			for(SolrFieldDTO fieldDto : newSolrFieldDTOs) {
				if(!validateSchemaField(fieldDto)) {
					log.debug("Validate SolrFieldDTO before updating the current schema- {}", schemaName);
					solrSchemaResponseDTOAfter.setStatusCode(400);
					break;
				}
				errorCausingField = fieldDto.getName();
				Map<String, Object> newField = new HashMap<>();
				newField.put("name", fieldDto.getName());
				newField.put("type", SolrFieldType.fromEnumToString(fieldDto.getType()));
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
			log.debug(e.toString());
		} catch (RemoteExecutionException e) {
			solrSchemaResponseDTOAfter.setStatusCode(400);
			log.error("There's been an error in executing {} operation via schema API. Perhaps the target field- {} isn't present.", payloadOperation, errorCausingField);
			log.debug(e.toString());
		} catch (SolrException e) {
			solrSchemaResponseDTOAfter.setStatusCode(400);
			log.error("The collection- {} is Not Found in the Solr Cloud. So schema fields can't be found/deleted!", tableName);
			log.debug(e.toString());
		} 
		solrSchemaResponseDTOAfter = get(tableName);
		log.debug("Schema for collection- {}, before CREATE: {}", tableName, solrSchemaResponseDTOBefore);
		log.debug("Schema for collection- {}, after CREATE: {}", tableName, solrSchemaResponseDTOAfter);
		return solrSchemaResponseDTOAfter;
	}

	@Override
	public SolrSchemaResponseDTO delete(String tableName) {
		CloudSolrClient solr = solrSchemaAPIAdapter.getCloudSolrClient(URL_STRING_SOLR_CLOUD, tableName);
		solr.setDefaultCollection(tableName);
		
		SchemaRequest schemaRequest = new SchemaRequest();
		
		SolrSchemaResponseDTO solrSchemaResponseDTOBefore = new SolrSchemaResponseDTO();
		SolrSchemaResponseDTO solrSchemaResponseDTOAfter = new SolrSchemaResponseDTO();
		String schemaName = "";
		String errorCausingField = null;
		String payloadOperation = "";
		try {
			SchemaResponse schemaResponse = schemaRequest.process(solr);
			solrSchemaResponseDTOBefore.setStatusCode(200);
			
			SchemaRepresentation retrievedSchema = schemaResponse.getSchemaRepresentation();
			schemaName = retrievedSchema.getName();
			List<Map<String, Object>> schemaFields = retrievedSchema.getFields();
			solrSchemaResponseDTOBefore = get(tableName);
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
			log.debug(e.toString());
		} catch (RemoteExecutionException e) {
			solrSchemaResponseDTOAfter.setStatusCode(400);
			log.error("There's been an error in executing {} operation via schema API. Perhaps the target field- {} isn't present.", payloadOperation, errorCausingField);
			log.debug(e.toString());
		} catch (SolrException e) {
			solrSchemaResponseDTOAfter.setStatusCode(400);
			log.error("The collection- {} is Not Found in the Solr Cloud. So schema fields can't be found/deleted!", tableName);
			log.debug(e.toString());
		} 
		solrSchemaResponseDTOAfter = get(tableName);
		// Compare Pre-and-Post DELETE Operation
		log.debug("Schema for collection- {}, before DELETE: {}", tableName, solrSchemaResponseDTOBefore);
		log.debug("Schema for collection- {}, after DELETE: {}", tableName, solrSchemaResponseDTOAfter);
		return solrSchemaResponseDTOAfter;
	}
	
	@Override
	public List<FieldTypeDefinition> getSchemaFieldTypes(SolrSchemaDTO solrSchemaDTO) {
		log.debug("get schema field types.");
		
		SchemaRequest schemaRequest = new SchemaRequest();
		SolrClient solr = new HttpSolrClient.Builder(URL_STRING+solrSchemaDTO.getTableName()).build();
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
			log.debug(e.toString());
		} catch (IOException e) {
			log.debug(e.toString());
		}
		return schemaFieldTypes;
	}
	
	@Override
	public boolean validateSchemaField(SolrFieldDTO solrFieldDTO) {
		log.debug("Validate schema field: {}", solrFieldDTO);
		boolean fieldValidated = true;
		String fieldName = solrFieldDTO.getName();
		SolrFieldType fieldType = solrFieldDTO.getType();
		
		if(fieldName.length() < 1) {
			fieldValidated = false;
			log.debug("Invalid schema field name received: {}", fieldName);
		} else if(fieldType == null || !SolrFieldType.doesExist(SolrFieldType.fromEnumToString(fieldType))) {
			fieldValidated = false;
			log.debug("Invalid/Empty schema field type received: {}", fieldType);
		} else if(!validateSchemaFieldBooleanAttributes(solrFieldDTO)) {
			fieldValidated = false;
			log.debug("Invalid/Empty schema field boolean attributes received: {}", solrFieldDTO);
		}
		return fieldValidated;
	}

	@Override
	public boolean validateSchemaFieldBooleanAttributes(SolrFieldDTO solrFieldDTO) {
		log.debug("Validate schema field boolean attributes: {}", solrFieldDTO);
		
		boolean fieldAttributesValidated = true;
		String invalidAttribute = "";
		if(solrFieldDTO.isRequired() != true && solrFieldDTO.isRequired() != false) {
			fieldAttributesValidated = false;
			invalidAttribute = "required";
		} else if(solrFieldDTO.isFilterable() != true && solrFieldDTO.isFilterable() != false) {
			fieldAttributesValidated = false;
			invalidAttribute = "filtered";
		} else if(solrFieldDTO.isMultiValue() != true && solrFieldDTO.isMultiValue() != false) {
			fieldAttributesValidated = false;
			invalidAttribute = "multValued";
		} else if(solrFieldDTO.isStorable() != true && solrFieldDTO.isStorable() != false) {
			fieldAttributesValidated = false;
			invalidAttribute = "stored";
		} else if(solrFieldDTO.isSortable() != true && solrFieldDTO.isSortable() != false) {
			fieldAttributesValidated = false;
			invalidAttribute = "sorted";
		}
		if(!fieldAttributesValidated)
			log.debug("Invalid entry for field attribute: \"{}\"", invalidAttribute);
		log.debug("All Schema field boolean attributes are valid");
		return fieldAttributesValidated;
	}

	@Override
	public void setFieldsAsPerTheSchema(SolrFieldDTO solrFieldDTO, Map<String, Object> schemaField) {
		if(schemaField.containsKey("filtered"))
			solrFieldDTO.setFilterable((boolean)schemaField.get("filtered"));
		if(schemaField.containsKey("multiValued"))
			solrFieldDTO.setMultiValue((boolean)schemaField.get("multiValued"));;
		if(schemaField.containsKey("default"))
			solrFieldDTO.setDefault_((String)schemaField.get("default"));;
		if(schemaField.containsKey("required"))
			solrFieldDTO.setRequired((boolean)schemaField.get("required"));;
		if(schemaField.containsKey("sorted"))
			solrFieldDTO.setSortable((boolean)schemaField.get("sorted"));;
		if(schemaField.containsKey("stored"))
			solrFieldDTO.setStorable((boolean)schemaField.get("stored"));;
	}

	@Override
	public void setFieldsToDefaults(SolrFieldDTO solrFieldDTO) {
		solrFieldDTO.setFilterable(false);
		solrFieldDTO.setMultiValue(false);
		solrFieldDTO.setDefault_("mydefault");
		solrFieldDTO.setRequired(false);
		solrFieldDTO.setSortable(false);
		solrFieldDTO.setStorable(true);
	}

	@Override
	public List<Map<String, Object>> parseSchemaFieldDtosToListOfMaps(SolrSchemaDTO solrSchemaDTO) {
		List<Map<String, Object>> schemaFieldsList = new ArrayList<>();
		SolrFieldDTO[] schemaFields = solrSchemaDTO.getAttributes();
		
		Map<String, Object> fieldDtoMap = new HashMap<String, Object>();
		for(SolrFieldDTO fieldDto: schemaFields) {
			log.debug("Validate SolrFieldDTO before parsing it- {}", fieldDto);
			if(!validateSchemaField(fieldDto)) {
				fieldDtoMap = new HashMap<>();
				fieldDtoMap.put("validated", false);
				return schemaFieldsList;
			}
			fieldDtoMap.put("name", fieldDto.getName());
			fieldDtoMap.put("type", SolrFieldType.fromEnumToString(fieldDto.getType()));
			fieldDtoMap.put("stored", fieldDto.isStorable());
			fieldDtoMap.put("multiValued", fieldDto.isMultiValue());
			fieldDtoMap.put("required", fieldDto.isRequired());
			/* fieldDtoMap.put("default", fieldDto.getDefault_()); */
			schemaFieldsList.add(fieldDtoMap);
		}
		return schemaFieldsList;
	}
}
