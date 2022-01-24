package com.searchservice.app.domain.service;


import com.searchservice.app.domain.dto.SolrFieldDTO;
import com.searchservice.app.domain.dto.SolrSchemaDTO;
import com.searchservice.app.domain.dto.SolrSchemaResponseDTO;
import com.searchservice.app.domain.port.api.SolrSchemaServicePort;
import com.searchservice.app.infrastructure.adaptor.SolrAPIAdapter;
import com.searchservice.app.infrastructure.solrenum.SolrFieldType;
import com.searchservice.app.rest.errors.SolrSchemaValidationException;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
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
public class SolrSchemaService implements SolrSchemaServicePort {

	private final Logger log = LoggerFactory.getLogger(SolrSchemaService.class);
	

	@Value("${base-solr-url}")
	private String solrURL;
	private static final String SOLR_EXCEPTION_MSG = "The collection - {} is Not Found in the Solr Cloud!";
	private static final String SOLR_SCHEMA_EXCEPTION_MSG = "There's been an error in executing {} operation via schema API. "
	+ "Perhaps the target field- {} isn't present.";
	private static final String MULTIVALUED = "multiValued";
	private static final String SORTED = "sorted";
	private static final String STORED = "stored";
	private static final String REQUIRED = "required";
	private static final String DEFAULT = "default";
	private static final String VALIDATED = "validated";
	private static final String FILTERED = "filtered";

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

	
	// Add bean for solr client API adapter
	@Autowired
	SolrAPIAdapter solrAPIAdapter;

	@Override
	public SolrSchemaResponseDTO get(String tableName) {
		log.debug("Get Solr Schema");

		HttpSolrClient solrClient = solrAPIAdapter.getSolrClientWithTable(solrURL, tableName);
		
		SchemaRequest schemaRequest = new SchemaRequest();
		SolrSchemaDTO solrSchemaDTO = new SolrSchemaDTO();
		SolrSchemaResponseDTO solrSchemaResponseDTO = new SolrSchemaResponseDTO();
		String schemaName = "";
		String errorCausingField = null;
		String payloadOperation = "SchemaRequest";
		try {
			SchemaResponse schemaResponse = schemaRequest.process(solrClient);
			log.debug("Get request has been processed. Setting status code = 200");
			solrSchemaResponseDTO.setStatusCode(200);
			
			SchemaRepresentation schemaRepresentation = schemaResponse.getSchemaRepresentation();
			schemaName = schemaRepresentation.getName();
			List<Map<String, Object>> schemaFields = schemaResponse.getSchemaRepresentation().getFields();
			int numOfFields = schemaFields.size();
			SolrFieldDTO[] solrSchemaFieldDTOs = new SolrFieldDTO[numOfFields];
			log.debug("Total number of fields: {}", numOfFields);
			
			int schemaFieldIdx = 0;
			for(Map<String, Object> f: schemaFields) {
				
				// Prepare the SolrFieldDTO
				SolrFieldDTO solrFieldDTO = new SolrFieldDTO();
				solrFieldDTO.setName((String)f.get("name"));
				
				// Parse Field Type Object(String) to Enum
				String fieldTypeObj = (String) f.get("type");				
				SolrFieldType solrFieldType = SolrFieldType.fromObject(fieldTypeObj);
				
				solrFieldDTO.setType(solrFieldType);
				setFieldsToDefaults(solrFieldDTO);
				setFieldsAsPerTheSchema(solrFieldDTO, f);
				solrSchemaFieldDTOs[schemaFieldIdx] = solrFieldDTO;
				schemaFieldIdx++;
			}
			log.debug("Total fields stored in attributes array: {}", schemaFieldIdx);
	
			solrSchemaDTO.setTableName(tableName);
			solrSchemaDTO.setName(schemaRepresentation.getName());
			solrSchemaDTO.setAttributes(solrSchemaFieldDTOs);
			// prepare response dto
			solrSchemaResponseDTO.setName(schemaName);
			solrSchemaResponseDTO.setTableName(tableName);
			solrSchemaResponseDTO.setAttributes(solrSchemaFieldDTOs);
			solrSchemaResponseDTO.setStatusCode(200);
		} catch (SolrServerException | IOException e) {
			solrSchemaResponseDTO.setStatusCode(400);
			log.error(SOLR_SCHEMA_EXCEPTION_MSG, payloadOperation, errorCausingField);
			log.debug(e.toString());
		} catch (SolrException e) {
			solrSchemaResponseDTO.setStatusCode(400);
			log.error(SOLR_EXCEPTION_MSG, tableName);
			log.debug(e.toString());
		}
		return solrSchemaResponseDTO;
	}
	
	@Override
	public SolrSchemaResponseDTO update(String tableName, 
								SolrSchemaDTO newSolrSchemaDTO) {
		log.debug("Update Solr Schema");
		
		SchemaRequest schemaRequest = new SchemaRequest();
		HttpSolrClient solrClient = solrAPIAdapter.getSolrClientWithTable(solrURL, tableName);
		
		SolrSchemaResponseDTO solrSchemaResponseDTOBefore = new SolrSchemaResponseDTO();
		SolrSchemaResponseDTO solrSchemaResponseDTOAfter = new SolrSchemaResponseDTO();
		String errorCausingField = null;
		String payloadOperation = "";
		try {
			SchemaResponse schemaResponse = schemaRequest.process(solrClient);
			solrSchemaResponseDTOBefore.setStatusCode(200);
		
			List<Map<String, Object>> schemaFields = schemaResponse.getSchemaRepresentation().getFields();
			int numOfFields = schemaFields.size();
			log.debug("Total number of fields: {}", numOfFields);
			
			// Get all fields from incoming(from req Body) schemaDTO
			SolrFieldDTO[] newSchemaFields = newSolrSchemaDTO.getAttributes();
			List<Map<String, Object>> targetSchemafields = parseSchemaFieldDtosToListOfMaps(newSolrSchemaDTO);
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
				updateFieldsResponse = updateFieldsRequest.process(solrClient);
				solrSchemaResponseDTOAfter.setStatusCode(200);
				
				schemaResponseUpdateFields.add((String) currField.get("name"), updateFieldsResponse.getResponse());
				updatedFields++;
				log.debug("Field- {} is successfully updated", currField.get("name"));
			}
			// Compare required Vs Updated Fields
			log.debug("Total field updates required in the current schema: {}", totalUpdatesRequired);
			log.debug("Total fields updated in the current schema: {}", updatedFields);

		} catch (SolrServerException | IOException e) {
			solrSchemaResponseDTOAfter.setStatusCode(400);
			log.error(SOLR_SCHEMA_EXCEPTION_MSG, payloadOperation, errorCausingField);
			log.debug(e.toString());
		} catch (NullPointerException e) {
			solrSchemaResponseDTOAfter.setStatusCode(400);
			log.error("Null value detected!", e);
			log.debug(e.toString());
		} catch (SolrException e) {
			solrSchemaResponseDTOAfter.setStatusCode(400);
			log.error(SOLR_EXCEPTION_MSG+"So schema fields can't be found/deleted!", tableName);
			log.debug(e.toString());
		} catch (SolrSchemaValidationException e) {
			solrSchemaResponseDTOAfter.setStatusCode(400);
			log.error("Error Message: {}", e.getMessage());
			log.debug(e.toString());
		}
		solrSchemaResponseDTOAfter = get(tableName);
		return solrSchemaResponseDTOAfter;
	}
	
	@Override
	public SolrSchemaResponseDTO create(String tableName, 
								SolrSchemaDTO newSolrSchemaDTO) {
		log.debug("Create Solr Schema");

		HttpSolrClient solrClient = solrAPIAdapter.getSolrClientWithTable(solrURL, tableName);
		SchemaRequest schemaRequest = new SchemaRequest();
		
		SolrSchemaResponseDTO solrSchemaResponseDTOBefore = new SolrSchemaResponseDTO();
		SolrSchemaResponseDTO solrSchemaResponseDTOAfter = new SolrSchemaResponseDTO();
		String schemaName = "";
		String errorCausingField = null;
		String payloadOperation = "";
		try {
			// logic
			SchemaResponse schemaResponse = schemaRequest.process(solrClient);
			solrSchemaResponseDTOBefore.setStatusCode(200);
			
			SchemaRepresentation retrievedSchema = schemaResponse.getSchemaRepresentation();
			schemaName = retrievedSchema.getName();
			List<Map<String, Object>> schemaFields = schemaResponse.getSchemaRepresentation().getFields();
	
			// Add new fields present in the Target Schema to the given collection schema
			SolrFieldDTO[] newSolrFieldDTOs = newSolrSchemaDTO.getAttributes();
			log.debug("\nTarget Schema fields : {}", (Object[]) newSolrFieldDTOs);
			// ####### Add Schema Fields logic #######
			UpdateResponse addFieldResponse;
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
				newField.put(REQUIRED, fieldDto.isRequired());
				newField.put(STORED, fieldDto.isStorable());
				newField.put(MULTIVALUED, fieldDto.isMultiValue());

				SchemaRequest.AddField addFieldRequest = new SchemaRequest.AddField(newField);
				addFieldResponse = addFieldRequest.process(solrClient);
				solrSchemaResponseDTOAfter.setStatusCode(200);
				
				schemaResponseAddFields.add(fieldDto.getName(), addFieldResponse.getResponse());
			}
			log.debug("Logging newly added fields' responses--");
			for(Object field: schemaResponseAddFields) {
				log.debug("### Added Field Response : {}", field);
			}
		} catch (SolrServerException | IOException e) {
			solrSchemaResponseDTOAfter.setStatusCode(400);
			log.error(SOLR_SCHEMA_EXCEPTION_MSG, payloadOperation, errorCausingField);
			log.debug(e.toString());
		} catch (SolrException e) {
			solrSchemaResponseDTOAfter.setStatusCode(400);
			log.error(SOLR_EXCEPTION_MSG+" So schema fields can't be found/deleted!", tableName);
			log.debug(e.toString());
		} 
		solrSchemaResponseDTOAfter = get(tableName);
		return solrSchemaResponseDTOAfter;
	}

	@Override
	public SolrSchemaResponseDTO delete(String tableName) {
		
		HttpSolrClient solrClient = solrAPIAdapter.getSolrClientWithTable(solrURL, tableName);	
		SchemaRequest schemaRequest = new SchemaRequest();
		
		SolrSchemaResponseDTO solrSchemaResponseDTOBefore = new SolrSchemaResponseDTO();
		SolrSchemaResponseDTO solrSchemaResponseDTOAfter = new SolrSchemaResponseDTO();
		String schemaName = "";
		String errorCausingField = null;
		String payloadOperation = "";
		try {
			SchemaResponse schemaResponse = schemaRequest.process(solrClient);
			solrSchemaResponseDTOBefore.setStatusCode(200);
			
			SchemaRepresentation retrievedSchema = schemaResponse.getSchemaRepresentation();
			schemaName = retrievedSchema.getName();
			List<Map<String, Object>> schemaFields = retrievedSchema.getFields();

			// API purpose
			log.debug("Delete fields of Schema - {} in the the table - {}", schemaName, tableName);
	
			// ####### Delete Schema Fields logic #######
			/*
			 * Delete all the fields of current Schema in the given collection
			 */
			UpdateResponse deleteFieldResponse;
			NamedList<Object> schemaResponseDeleteFields = new NamedList<>();
			payloadOperation = "SchemaRequest.DeleteField";
			for (Map<String, Object> currField : schemaFields) {
				errorCausingField = (String) currField.get("name");
				/* 
				 * Define payload for DeleteField Operation 
				 */
				SchemaRequest.DeleteField deleteFieldRequest = new SchemaRequest.DeleteField((String) currField.get("name"));
				deleteFieldResponse = deleteFieldRequest.process(solrClient);
				schemaResponseDeleteFields.add((String) currField.get("name"), deleteFieldResponse.getResponse());
			}
			// Define payload for DeleteField Operation
			String targetFieldToDelete = "testField1";
			errorCausingField = targetFieldToDelete;
			SchemaRequest.DeleteField deleteFieldRequest = new SchemaRequest.DeleteField(targetFieldToDelete);
			deleteFieldResponse = deleteFieldRequest.process(solrClient);
			solrSchemaResponseDTOAfter.setStatusCode(200);
			
			schemaResponseDeleteFields.add(targetFieldToDelete, deleteFieldResponse.getResponse());

			log.debug("Logging all deleted fields' responses--");
			for(Object field: schemaResponseDeleteFields) {
				log.debug("Added Field Response : {}", field);
			}
		} catch (SolrServerException | IOException e) {
			solrSchemaResponseDTOAfter.setStatusCode(400);
			log.error(SOLR_SCHEMA_EXCEPTION_MSG, payloadOperation, errorCausingField);
			log.debug(e.toString());
		} catch (SolrException e) {
			solrSchemaResponseDTOAfter.setStatusCode(400);
			log.error(SOLR_EXCEPTION_MSG+"So schema fields can't be found/deleted!", tableName);
			log.debug(e.toString());
		} 
		solrSchemaResponseDTOAfter = get(tableName);
		return solrSchemaResponseDTOAfter;
	}
	
	@Override
	public List<FieldTypeDefinition> getSchemaFieldTypes(SolrSchemaDTO solrSchemaDTO) {
		log.debug("get schema field types.");
		
		SchemaRequest schemaRequest = new SchemaRequest();
		HttpSolrClient solrClient = solrAPIAdapter.getSolrClientWithTable(solrURL, solrSchemaDTO.getTableName());
		
		List<FieldTypeDefinition> schemaFieldTypes = null;
		try {
			SchemaResponse schemaResponse = schemaRequest.process(solrClient);
			// explore response content
			log.debug("Response header : {}", schemaResponse.getResponseHeader());	
			schemaFieldTypes = schemaResponse.getSchemaRepresentation().getFieldTypes();
			int numOfFieldTypes = schemaResponse.getSchemaRepresentation().getFieldTypes().size();
			log.debug("Response schema size : {}", schemaFieldTypes.size());
			for(int i=0; i<numOfFieldTypes; i++) {
				log.debug("Field Types : {}", schemaFieldTypes.get(i).getAttributes());
			}
		} catch (SolrServerException |IOException e) {
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
			log.debug("Invalid/Empty schema field boolean attributes received");
		}
		return fieldValidated;
	}

	@Override
	public boolean validateSchemaFieldBooleanAttributes(SolrFieldDTO solrFieldDTO) {
		log.debug("Validate schema field boolean attributes: {}", solrFieldDTO);
		
		boolean fieldAttributesValidated = true;
		String invalidAttribute = "";
		if(!solrFieldDTO.isRequired() && solrFieldDTO.isRequired()) {
			fieldAttributesValidated = false;
			invalidAttribute = REQUIRED;
		} else if(!solrFieldDTO.isFilterable() && solrFieldDTO.isFilterable()) {
			fieldAttributesValidated = false;
			invalidAttribute = FILTERED;
		} else if(!solrFieldDTO.isMultiValue() && solrFieldDTO.isMultiValue()) {
			fieldAttributesValidated = false;
			invalidAttribute = "multValued";
		} else if(!solrFieldDTO.isStorable() && solrFieldDTO.isStorable()) {
			fieldAttributesValidated = false;
			invalidAttribute = STORED;
		} else if(!solrFieldDTO.isSortable() && solrFieldDTO.isSortable()) {
			fieldAttributesValidated = false;
			invalidAttribute = SORTED;
		}
		if(!fieldAttributesValidated)
			log.debug("Invalid entry for field attribute: \"{}\"", invalidAttribute);
		log.debug("All Schema field boolean attributes are valid");
		return fieldAttributesValidated;
	}

	@Override
	public void setFieldsAsPerTheSchema(SolrFieldDTO solrFieldDTO, Map<String, Object> schemaField) {
		if(schemaField.containsKey(FILTERED))
			solrFieldDTO.setFilterable((boolean)schemaField.get(FILTERED));
		if(schemaField.containsKey(MULTIVALUED))
			solrFieldDTO.setMultiValue((boolean)schemaField.get(MULTIVALUED));
		if(schemaField.containsKey(DEFAULT))
			solrFieldDTO.setDefault_((String)schemaField.get(DEFAULT));
		if(schemaField.containsKey(REQUIRED))
			solrFieldDTO.setRequired((boolean)schemaField.get(REQUIRED));
		if(schemaField.containsKey(SORTED))
			solrFieldDTO.setSortable((boolean)schemaField.get(SORTED));
		if(schemaField.containsKey(STORED))
			solrFieldDTO.setStorable((boolean)schemaField.get(STORED));
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
		
		Map<String, Object> fieldDtoMap = new HashMap<>();
		for(SolrFieldDTO fieldDto: schemaFields) {
			log.debug("Validate SolrFieldDTO before parsing it");
			if(!validateSchemaField(fieldDto)) {
				fieldDtoMap = new HashMap<>();
				fieldDtoMap.put(VALIDATED, false);
				return schemaFieldsList;
			}
			fieldDtoMap.put("name", fieldDto.getName());
			fieldDtoMap.put("type", SolrFieldType.fromEnumToString(fieldDto.getType()));
			fieldDtoMap.put(STORED, fieldDto.isStorable());
			fieldDtoMap.put(MULTIVALUED, fieldDto.isMultiValue());
			fieldDtoMap.put(REQUIRED, fieldDto.isRequired());
			schemaFieldsList.add(fieldDtoMap);
		}
		return schemaFieldsList;
	}
}
