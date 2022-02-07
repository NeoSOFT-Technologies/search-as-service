package com.searchservice.app.domain.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import com.searchservice.app.domain.dto.schema.FieldDTO;
import com.searchservice.app.domain.dto.schema.SchemaDTO;
import com.searchservice.app.domain.dto.schema.SchemaResponseDTO;
import com.searchservice.app.domain.port.api.SchemaServicePort;
import com.searchservice.app.infrastructure.adaptor.SolrAPIAdapter;
import com.searchservice.app.infrastructure.enums.SchemaFieldType;
import com.searchservice.app.rest.errors.SchemaValidationException;

@Service
public class SchemaService implements SchemaServicePort {

	private final Logger log = LoggerFactory.getLogger(SchemaService.class);

	@Value("${base-solr-url}")
	private String solrURL;
	private static final String SOLR_EXCEPTION_MSG = "The collection - {} is Not Found in the Solr Cloud!";
	private static final String SCHEMA_FILED_EXCEPTION_MSG = "So schema fields can't be found/deleted! ";
	private static final String SOLR_SCHEMA_EXCEPTION_MSG = "There's been an error in executing {} operation via schema API. "
			+ "Perhaps the target field- {} isn't present.";
	private static final String MULTIVALUED = "multiValued";
	private static final String DOCVALUES = "docValues";
	private static final String STORED = "stored";
	private static final String REQUIRED = "required";
	private static final String DEFAULT = "default";
	private static final String VALIDATED = "validated";
	private static final String INDEXED = "indexed";

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
	public SchemaResponseDTO get(String tableName) {
		log.debug("Get Solr Schema");

		HttpSolrClient solrClient = solrAPIAdapter.getSolrClientWithTable(solrURL, tableName);

		SchemaRequest schemaRequest = new SchemaRequest();
		SchemaDTO schemaDTO = new SchemaDTO();
		SchemaResponseDTO schemaResponseDTO = new SchemaResponseDTO();
		String schemaName = "";
		String errorCausingField = null;
		String payloadOperation = "SchemaRequest";
		try {
			SchemaResponse schemaResponse = schemaRequest.process(solrClient);
			log.debug("Get request has been processed. Setting status code = 200");
			schemaResponseDTO.setStatusCode(200);

			SchemaRepresentation schemaRepresentation = schemaResponse.getSchemaRepresentation();
			schemaName = schemaRepresentation.getName();
			List<Map<String, Object>> schemaFields = schemaResponse.getSchemaRepresentation().getFields();
			int numOfFields = schemaFields.size();
			FieldDTO[] solrSchemaFieldDTOs = new FieldDTO[numOfFields];
			log.debug("Total number of fields: {}", numOfFields);

			int schemaFieldIdx = 0;
			for (Map<String, Object> f : schemaFields) {

				// Prepare the FieldDTO
				FieldDTO fieldDTO = new FieldDTO();
				fieldDTO.setName((String) f.get("name"));

				// Parse Field Type Object(String) to Enum
				String fieldTypeObj = (String) f.get("type");
				SchemaFieldType schemaFieldType = SchemaFieldType.fromObject(fieldTypeObj);

				fieldDTO.setType(schemaFieldType);
				setFieldsToDefaults(fieldDTO);
				setFieldsAsPerTheSchema(fieldDTO, f);
				solrSchemaFieldDTOs[schemaFieldIdx] = fieldDTO;
				schemaFieldIdx++;
			}
			log.debug("Total fields stored in attributes array: {}", schemaFieldIdx);

			schemaDTO.setTableName(tableName);
			schemaDTO.setName(schemaRepresentation.getName());
			schemaDTO.setAttributes(solrSchemaFieldDTOs);
			// prepare response dto
			schemaResponseDTO.setName(schemaName);
			schemaResponseDTO.setTableName(tableName);
			schemaResponseDTO.setAttributes(solrSchemaFieldDTOs);
			schemaResponseDTO.setStatusCode(200);
		} catch (SolrServerException | IOException e) {
			schemaResponseDTO.setStatusCode(400);
			schemaResponseDTO.setMessage(SOLR_SCHEMA_EXCEPTION_MSG +" "+payloadOperation+" "+errorCausingField);
			log.error(SOLR_SCHEMA_EXCEPTION_MSG, payloadOperation, errorCausingField);
			log.debug(e.toString());
		} catch (SolrException e) {
			schemaResponseDTO.setStatusCode(400);
			schemaResponseDTO.setMessage(SOLR_EXCEPTION_MSG+" "+ tableName);
			log.error(SOLR_EXCEPTION_MSG, tableName);
			log.debug(e.toString());
		}
		return schemaResponseDTO;
	}

	@Override
	public SchemaResponseDTO update(String tableName, SchemaDTO newSchemaDTO) {
		log.debug("Update Solr Schema");

		SchemaRequest schemaRequest = new SchemaRequest();
		HttpSolrClient solrClient = solrAPIAdapter.getSolrClientWithTable(solrURL, tableName);

		SchemaResponseDTO schemaResponseDTOBefore = new SchemaResponseDTO();
		SchemaResponseDTO schemaResponseDTOAfter = new SchemaResponseDTO();
		String errorCausingField = null;
		String payloadOperation = "";
		try {
			SchemaResponse schemaResponse = schemaRequest.process(solrClient);
			schemaResponseDTOBefore.setStatusCode(200);

			List<Map<String, Object>> schemaFields = schemaResponse.getSchemaRepresentation().getFields();
			int numOfFields = schemaFields.size();
			log.debug("Total number of fields: {}", numOfFields);

			// Get all fields from incoming(from req Body) schemaDTO
			FieldDTO[] newSchemaFields = newSchemaDTO.getAttributes();
			List<Map<String, Object>> targetSchemafields = parseSchemaFieldDtosToListOfMaps(newSchemaDTO);
			// Validate Solr Schema Fields
			Map<String, Object> validationEntry = targetSchemafields.get(0);
			if (validationEntry.containsKey(VALIDATED)) {
				Object validatedFields = validationEntry.get(VALIDATED);
				if (validatedFields.equals(false))
					throw new SchemaValidationException("Target Schema Fields validation falied!");
			}

			int totalUpdatesRequired = newSchemaFields.length;

			// Update Schema Logic
			UpdateResponse updateFieldsResponse;
			NamedList<Object> schemaResponseUpdateFields = new NamedList<>();
			payloadOperation = "SchemaRequest.ReplaceField";
			int updatedFields = 0;
			for (Map<String, Object> currField : targetSchemafields) {
				errorCausingField = (String) currField.get("name");
				// Pass all fieldAttributes to be updated
				SchemaRequest.ReplaceField updateFieldsRequest = new SchemaRequest.ReplaceField(currField);
				updateFieldsResponse = updateFieldsRequest.process(solrClient);
				schemaResponseDTOAfter.setStatusCode(200);

				schemaResponseUpdateFields.add((String) currField.get("name"), updateFieldsResponse.getResponse());
				updatedFields++;
				log.debug("Field- {} is successfully updated", currField.get("name"));
			}
			// Compare required Vs Updated Fields
			log.debug("Total field updates required in the current schema: {}", totalUpdatesRequired);
			log.debug("Total fields updated in the current schema: {}", updatedFields);

		} catch (SolrServerException | IOException e) {
			schemaResponseDTOAfter.setStatusCode(400);
			schemaResponseDTOAfter.setMessage(SOLR_SCHEMA_EXCEPTION_MSG +" "+payloadOperation+" "+errorCausingField);
			log.error(SOLR_SCHEMA_EXCEPTION_MSG, payloadOperation, errorCausingField);
			log.debug(e.toString());
		} catch (NullPointerException e) {
			schemaResponseDTOAfter.setStatusCode(400);
			schemaResponseDTOAfter.setMessage("Null Value Detected!");
			log.error("Null value detected!", e);
			log.debug(e.toString());
		} catch (SolrException e) {
			schemaResponseDTOAfter.setStatusCode(400);

			schemaResponseDTOAfter.setMessage(SOLR_EXCEPTION_MSG+ SCHEMA_FILED_EXCEPTION_MSG +tableName);
			log.error(SOLR_EXCEPTION_MSG+"So schema fields can't be found/deleted!", tableName);

			log.debug(e.toString());
		} catch (SchemaValidationException e) {
			schemaResponseDTOAfter.setStatusCode(400);
			schemaResponseDTOAfter.setMessage("Error Message: {} "+e.getMessage());
			log.error("Error Message: {}", e.getMessage());
			log.debug(e.toString());
		}
		schemaResponseDTOAfter = get(tableName);
		return schemaResponseDTOAfter;
	}

	@Override
	public SchemaResponseDTO create(String tableName, SchemaDTO newSchemaDTO) {
		log.debug("Create Solr Schema");

		HttpSolrClient solrClient = solrAPIAdapter.getSolrClientWithTable(solrURL, tableName);
		SchemaRequest schemaRequest = new SchemaRequest();

		SchemaResponseDTO schemaResponseDTOBefore = new SchemaResponseDTO();
		SchemaResponseDTO schemaResponseDTOAfter = new SchemaResponseDTO();
		String schemaName = "";
		String errorCausingField = null;
		String payloadOperation = "";
		try {
			// logic
			SchemaResponse schemaResponse = schemaRequest.process(solrClient);
			schemaResponseDTOBefore.setStatusCode(200);

			SchemaRepresentation retrievedSchema = schemaResponse.getSchemaRepresentation();
			schemaName = retrievedSchema.getName();
			List<Map<String, Object>> schemaFields = schemaResponse.getSchemaRepresentation().getFields();

			// Add new fields present in the Target Schema to the given collection schema
			FieldDTO[] newFieldDTOS = newSchemaDTO.getAttributes();
			log.debug("\nTarget Schema fields : {}", (Object[]) newFieldDTOS);
			// ####### Add Schema Fields logic #######
			UpdateResponse addFieldResponse;
			NamedList<Object> schemaResponseAddFields = new NamedList<>();
			payloadOperation = "SchemaRequest.AddField";
			boolean newFieldFound = false;
			for (FieldDTO fieldDto : newFieldDTOS) {
				boolean isPresent = false;
				for (Map<String, Object> field : schemaFields) {
					if (field.containsKey(fieldDto.getName())) {
						isPresent = true;
						break;
					}
				}
				if (!isPresent)
					newFieldFound = true;
			}
			if (!newFieldFound) {
				schemaResponseDTOAfter.setStatusCode(400);
			}
			for (FieldDTO fieldDto : newFieldDTOS) {
				if (!validateSchemaField(fieldDto)) {
					log.debug("Validate FieldDTO before updating the current schema- {}", schemaName);
					schemaResponseDTOAfter.setStatusCode(400);
					schemaResponseDTOAfter.setMessage("Validation for FieldDTO Failed");
					break;
				}
			
				if(fieldDto.isSortable()) {
					fieldDto.setMultiValue(false); // For SortOnField UseCase MultiValue must be False
				
				}
				errorCausingField = fieldDto.getName();
				Map<String, Object> newField = new HashMap<>();
				newField.put("name", fieldDto.getName());
				newField.put("type", SchemaFieldType.fromEnumToString(fieldDto.getType()));
				newField.put(REQUIRED, fieldDto.isRequired());
				newField.put(STORED, fieldDto.isStorable());
				newField.put(MULTIVALUED, fieldDto.isMultiValue());
				newField.put(INDEXED, fieldDto.isFilterable());
				newField.put(DOCVALUES, fieldDto.isSortable());

				SchemaRequest.AddField addFieldRequest = new SchemaRequest.AddField(newField);
				addFieldResponse = addFieldRequest.process(solrClient);
				schemaResponseDTOAfter.setStatusCode(200);

				schemaResponseAddFields.add(fieldDto.getName(), addFieldResponse.getResponse());
			}
			log.debug("Logging newly added fields' responses--");
			for (Object field : schemaResponseAddFields) {
				log.debug("### Added Field Response : {}", field);
			}
		} catch (SolrServerException | IOException e) {
			schemaResponseDTOAfter.setStatusCode(400);
			schemaResponseDTOAfter.setMessage(SOLR_SCHEMA_EXCEPTION_MSG +" "+payloadOperation+" "+errorCausingField);
			log.error(SOLR_SCHEMA_EXCEPTION_MSG, payloadOperation, errorCausingField);
			log.debug(e.toString());
		} catch (SolrException e) {
			schemaResponseDTOAfter.setStatusCode(400);

			schemaResponseDTOAfter.setMessage(SOLR_EXCEPTION_MSG + SCHEMA_FILED_EXCEPTION_MSG + tableName);
			log.error(SOLR_EXCEPTION_MSG+" So schema fields can't be found/deleted!", tableName);

			log.debug(e.toString());
		}
		schemaResponseDTOAfter = get(tableName);
		return schemaResponseDTOAfter;
	}

	@Override
	public SchemaResponseDTO delete(String tableName) {

		HttpSolrClient solrClient = solrAPIAdapter.getSolrClientWithTable(solrURL, tableName);
		SchemaRequest schemaRequest = new SchemaRequest();

		SchemaResponseDTO schemaResponseDTOBefore = new SchemaResponseDTO();
		SchemaResponseDTO schemaResponseDTOAfter = new SchemaResponseDTO();
		String schemaName = "";
		String errorCausingField = null;
		String payloadOperation = "";
		try {
			SchemaResponse schemaResponse = schemaRequest.process(solrClient);
			schemaResponseDTOBefore.setStatusCode(200);

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
				SchemaRequest.DeleteField deleteFieldRequest = new SchemaRequest.DeleteField(
						(String) currField.get("name"));
				deleteFieldResponse = deleteFieldRequest.process(solrClient);
				schemaResponseDeleteFields.add((String) currField.get("name"), deleteFieldResponse.getResponse());
			}
			// Define payload for DeleteField Operation
			String targetFieldToDelete = "testField1";
			errorCausingField = targetFieldToDelete;
			SchemaRequest.DeleteField deleteFieldRequest = new SchemaRequest.DeleteField(targetFieldToDelete);
			deleteFieldResponse = deleteFieldRequest.process(solrClient);
			schemaResponseDTOAfter.setStatusCode(200);

			schemaResponseDeleteFields.add(targetFieldToDelete, deleteFieldResponse.getResponse());

			log.debug("Logging all deleted fields' responses--");
			for (Object field : schemaResponseDeleteFields) {
				log.debug("Added Field Response : {}", field);
			}
		} catch (SolrServerException | IOException e) {
			schemaResponseDTOAfter.setStatusCode(400);
			schemaResponseDTOAfter.setMessage(SOLR_SCHEMA_EXCEPTION_MSG +" "+payloadOperation+" "+errorCausingField);
			log.error(SOLR_SCHEMA_EXCEPTION_MSG, payloadOperation, errorCausingField);
			log.debug(e.toString());
		} catch (SolrException e) {
			schemaResponseDTOAfter.setStatusCode(400);

			schemaResponseDTOAfter.setMessage(SOLR_EXCEPTION_MSG + SCHEMA_FILED_EXCEPTION_MSG + tableName);
			log.error(SOLR_EXCEPTION_MSG+"So schema fields can't be found/deleted!", tableName);

			log.debug(e.toString());
		}
		schemaResponseDTOAfter = get(tableName);
		return schemaResponseDTOAfter;
	}

	@Override
	public List<FieldTypeDefinition> getSchemaFieldTypes(SchemaDTO schemaDTO) {
		log.debug("get schema field types.");

		SchemaRequest schemaRequest = new SchemaRequest();
		HttpSolrClient solrClient = solrAPIAdapter.getSolrClientWithTable(solrURL, schemaDTO.getTableName());

		List<FieldTypeDefinition> schemaFieldTypes = null;
		try {
			SchemaResponse schemaResponse = schemaRequest.process(solrClient);
			// explore response content
			log.debug("Response header : {}", schemaResponse.getResponseHeader());
			schemaFieldTypes = schemaResponse.getSchemaRepresentation().getFieldTypes();
			int numOfFieldTypes = schemaResponse.getSchemaRepresentation().getFieldTypes().size();
			log.debug("Response schema size : {}", schemaFieldTypes.size());
			for (int i = 0; i < numOfFieldTypes; i++) {
				log.debug("Field Types : {}", schemaFieldTypes.get(i).getAttributes());
			}
		} catch (SolrServerException | IOException e) {
			log.debug(e.toString());
		}
		return schemaFieldTypes;
	}

	@Override
	public boolean validateSchemaField(FieldDTO fieldDTO) {
		log.debug("Validate schema field: {}", fieldDTO);
		boolean fieldValidated = true;
		String fieldName = fieldDTO.getName();
		SchemaFieldType fieldType = fieldDTO.getType();

		if (fieldName.length() < 1) {
			fieldValidated = false;
			log.debug("Invalid schema field name received: {}", fieldName);
		} else if (fieldType == null || !SchemaFieldType.doesExist(SchemaFieldType.fromEnumToString(fieldType))) {
			fieldValidated = false;
			log.debug("Invalid/Empty schema field type received: {}", fieldType);
		} else if (!validateSchemaFieldBooleanAttributes(fieldDTO)) {
			fieldValidated = false;
			log.debug("Invalid/Empty schema field boolean attributes received");
		}
		return fieldValidated;
	}

	@Override
	public boolean validateSchemaFieldBooleanAttributes(FieldDTO fieldDTO) {
		log.debug("Validate schema field boolean attributes: {}", fieldDTO);

		boolean fieldAttributesValidated = true;
		String invalidAttribute = "";
		if (!fieldDTO.isRequired() && fieldDTO.isRequired()) {
			fieldAttributesValidated = false;
			invalidAttribute = REQUIRED;
		} else if (!fieldDTO.isFilterable() && fieldDTO.isFilterable()) {
			fieldAttributesValidated = false;
			invalidAttribute = INDEXED;
		} else if (!fieldDTO.isMultiValue() && fieldDTO.isMultiValue()) {
			fieldAttributesValidated = false;
			invalidAttribute = "multValued";
		} else if (!fieldDTO.isStorable() && fieldDTO.isStorable()) {
			fieldAttributesValidated = false;
			invalidAttribute = STORED;
		} else if (!fieldDTO.isSortable() && fieldDTO.isSortable()) {
			fieldAttributesValidated = false;
			invalidAttribute = DOCVALUES;
		}
		if (!fieldAttributesValidated)
			log.debug("Invalid entry for field attribute: \"{}\"", invalidAttribute);
		log.debug("All Schema field boolean attributes are valid");
		return fieldAttributesValidated;
	}

	@Override
	public void setFieldsAsPerTheSchema(FieldDTO fieldDTO, Map<String, Object> schemaField) {
		if (schemaField.containsKey(INDEXED))
			fieldDTO.setFilterable((boolean) schemaField.get(INDEXED));
		if (schemaField.containsKey(MULTIVALUED))
			fieldDTO.setMultiValue((boolean) schemaField.get(MULTIVALUED));
		if (schemaField.containsKey(DEFAULT))
			fieldDTO.setDefault_((String) schemaField.get(DEFAULT));
		if (schemaField.containsKey(REQUIRED))
			fieldDTO.setRequired((boolean) schemaField.get(REQUIRED));
		if (schemaField.containsKey(DOCVALUES))
			fieldDTO.setSortable((boolean) schemaField.get(DOCVALUES));
		if (schemaField.containsKey(STORED))
			fieldDTO.setStorable((boolean) schemaField.get(STORED));
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
	public List<Map<String, Object>> parseSchemaFieldDtosToListOfMaps(SchemaDTO schemaDTO) {
		List<Map<String, Object>> schemaFieldsList = new ArrayList<>();
		FieldDTO[] schemaFields = schemaDTO.getAttributes();

		Map<String, Object> fieldDtoMap = new HashMap<>();
		for (FieldDTO fieldDto : schemaFields) {
			log.debug("Validate FieldDTO before parsing it");
			if (!validateSchemaField(fieldDto)) {
				fieldDtoMap = new HashMap<>();
				fieldDtoMap.put(VALIDATED, false);
				return schemaFieldsList;
			}
			fieldDtoMap.put("name", fieldDto.getName());
			fieldDtoMap.put("type", SchemaFieldType.fromEnumToString(fieldDto.getType()));
			fieldDtoMap.put(STORED, fieldDto.isStorable());
			fieldDtoMap.put(MULTIVALUED, fieldDto.isMultiValue());
			fieldDtoMap.put(REQUIRED, fieldDto.isRequired());
			fieldDtoMap.put(DOCVALUES, fieldDto.isSortable());
			fieldDtoMap.put(INDEXED, fieldDto.isFilterable());
			schemaFieldsList.add(fieldDtoMap);
		}
		return schemaFieldsList;
	}
}
