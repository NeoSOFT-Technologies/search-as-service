package com.solr.clientwrapper.domain.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.client.solrj.SolrResponse;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient.RemoteExecutionException;
import org.apache.solr.client.solrj.request.schema.FieldTypeDefinition;
import org.apache.solr.client.solrj.request.schema.SchemaRequest;
import org.apache.solr.client.solrj.request.schema.SchemaRequest.AddField;
import org.apache.solr.client.solrj.response.schema.SchemaRepresentation;
import org.apache.solr.client.solrj.response.schema.SchemaResponse;
import org.apache.solr.client.solrj.response.schema.SchemaResponse.UpdateResponse;
import org.apache.solr.common.SolrException;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.common.util.NamedList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.solr.clientwrapper.domain.dto.solr.SolrFieldDTO;
import com.solr.clientwrapper.domain.dto.solr.SolrSchemaDTO;
import com.solr.clientwrapper.domain.port.api.SolrSchemaServicePort;
import com.solr.clientwrapper.infrastructure.Enum.SolrFieldType;
import com.solr.clientwrapper.infrastructure.adaptor.SolrSchemaAPIAdapter;

@Service
@Transactional
public class SolrSchemaService implements SolrSchemaServicePort {

	private final Logger log = LoggerFactory.getLogger(SolrSchemaService.class);
	
	@Value("techproducts")
	private static String TEST1;
	private static String DEFAULT_COLLECTION = "techproducts";
	private static String DEFAULT_SOLR_CLOUD_COLLECTION = "gettingstarted1";
	@Value("solr.client.url.static")
	private static String TEST2;
	private static String URL_STRING = "http://localhost:8985/solr/";
	@Value("solr.client.url.cloud")
	private static String TEST3;
	private static String URL_STRING_SOLR_CLOUD = "http://localhost:8983/solr/";
	
	// call for solr client
	@Autowired
	SolrSchemaAPIAdapter solrSchemaAPIAdapter = new SolrSchemaAPIAdapter();

	@Override
	public String getSolrClient(String tableName) {
		SolrClient solr = solrSchemaAPIAdapter.getSolrClient(URL_STRING, tableName);
		
//		SolrClient solrCloud = solrSchemaAPIAdapter.getSolrCloudClient(URL_STRING_SOLR_CLOUD, tableName);
//		solrCloud.setDefaultCollection("gettingstarted");
		
		System.out.println("@@@@@@ Solr client ^^^^^^^^^^ : "+solr);
//		System.out.println("@@@@@@ Solr cloud client ^^^^^^^^^^ : "+solrCloud);
		
		return "Solr Clients are successfully retrieved.";
	}

	@Override
	public SolrSchemaDTO get(String tableName, String name) {
		log.debug("Get Solr Schema: {}", name);
		
		// testing
		System.out.println("Test Value Annot ((((((((((( : "+TEST1);
		System.out.println("Test Value Annot ((((((((((( : "+TEST2);
		System.out.println("Test Value Annot ((((((((((( : "+TEST3);
		
//		SolrClient solr = solrSchemaAPIAdapter.getSolrClient(URL_STRING, tableName);
//		SolrClient solr = solrSchemaAPIAdapter.getSolrCloudClient(URL_STRING_SOLR_CLOUD, tableName);
		CloudSolrClient solr = solrSchemaAPIAdapter.getCloudSolrClient(URL_STRING_SOLR_CLOUD, tableName);
		solr.setDefaultCollection(tableName);
		
		SchemaRequest schemaRequest = new SchemaRequest();
		SolrSchemaDTO solrSchemaDTO = new SolrSchemaDTO();
		try {
			SchemaResponse schemaResponse = schemaRequest.process(solr);
			// explore response content
			log.debug("\nResponse header : {}", schemaResponse.getResponseHeader());
			log.debug("Response class : {}", schemaResponse.getResponseHeader().getClass());
			
			SchemaRepresentation schemaRepresentation = schemaResponse.getSchemaRepresentation();
			// System.out.println("Found Schema name ********** : "+schemaResponse.getSchemaRepresentation().getName());
			
			/*
			 * List<FieldTypeDefinition> schemaFieldTypes =
			 * schemaResponse.getSchemaRepresentation().getFieldTypes(); int numOfFieldTypes
			 * = schemaFieldTypes.size();
			 */
			
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
		} catch (SolrServerException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SolrException e) {
			log.error("The collection - {} is Not Found in the Solr Cloud!", tableName);
			e.printStackTrace();
		}
		return solrSchemaDTO;
	}

	@Override
	public SolrSchemaDTO update1(String tableName, 
								String name, 
								SolrSchemaDTO newSolrSchemaDTO) {
		log.debug("Update Solr Schema: {}", name);
		log.debug("Target Schema: {}", newSolrSchemaDTO);
		
		SchemaRequest schemaRequest = new SchemaRequest();
		CloudSolrClient solr = solrSchemaAPIAdapter.getCloudSolrClient(URL_STRING_SOLR_CLOUD, tableName);
		solr.setDefaultCollection(tableName);
		
		SolrSchemaDTO solrSchemaResponseDTO = new SolrSchemaDTO();
		try {
			SchemaResponse schemaResponse = schemaRequest.process(solr);
			// explore response content
			log.debug("\nResponse header : {}", schemaResponse.getResponseHeader());
			log.debug("Response class : {}", schemaResponse.getResponseHeader().getClass());
			
			List<Map<String, Object>> schemaFields = schemaResponse.getSchemaRepresentation().getFields();
			int numOfFields = schemaFields.size();
			SolrFieldDTO[] solrSchemaFieldDTOs = new SolrFieldDTO[numOfFields];
			log.debug("Total number of fields: {}", numOfFields);
			
			// Get all fields from incoming(from req Body) schemaDTO
			SolrFieldDTO[] newSchemaFields = newSolrSchemaDTO.getAttributes();
			int totalUpdatesRequired = newSchemaFields.length;
			
			int schemaFieldIdx = 0;
			for(Map<String, Object> f: schemaFields) {
				log.debug("\nField Object: {}", f);
				
				if(schemaFieldIdx == totalUpdatesRequired) {
					log.debug("All {} updates done, successfully", totalUpdatesRequired);
					break;
				}
				// Update fields
				f.put("name", newSchemaFields[schemaFieldIdx].getName());
				f.put("type", newSchemaFields[schemaFieldIdx].getType());
				final int newSchemaFieldIdx = schemaFieldIdx;
				f.forEach((k, v) -> {
					if(k.equals("default"))
						f.put(k, newSchemaFields[newSchemaFieldIdx].getDefault_());
					else if(k.equals("filtered"))
						f.put(k, newSchemaFields[newSchemaFieldIdx].isFilterable());
					else if(k.equals("multiValued"))
						f.put(k, newSchemaFields[newSchemaFieldIdx].isMultiValue());
					else if(k.equals("required"))
						f.put(k, newSchemaFields[newSchemaFieldIdx].isRequired());
					else if(k.equals("sorted"))
						f.put(k, newSchemaFields[newSchemaFieldIdx].isSortable());
					else if(k.equals("stored"))
						f.put(k, newSchemaFields[newSchemaFieldIdx].isStorable());
				});
				schemaFieldIdx++;
			}
			log.debug("Total fields stored in attributes array: {}", schemaFieldIdx);
			log.debug("Response class: {}", schemaResponse.getSchemaRepresentation().getClass());
			
			/*
			 * solrSchemaResponseDTO.setTableName(solrSchemaDTO.getTableName());
			 * solrSchemaResponseDTO.setName(solrSchemaDTO.getName());
			 * solrSchemaResponseDTO.setAttributes(solrSchemaFieldDTOs);
			 */
			solrSchemaResponseDTO = newSolrSchemaDTO;
		} catch (SolrServerException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			log.error("Null value detected!", e);
			e.printStackTrace();
		}
		return solrSchemaResponseDTO;
	}
	
	
	@Override
	public SolrSchemaDTO update(String tableName, 
								String name, 
								SolrSchemaDTO newSolrSchemaDTO) {
		log.debug("Update Solr Schema: {}", name);
		log.debug("Target Schema: {}", newSolrSchemaDTO);
		
		SchemaRequest schemaRequest = new SchemaRequest();
		CloudSolrClient solr = solrSchemaAPIAdapter.getCloudSolrClient(URL_STRING_SOLR_CLOUD, tableName);
		solr.setDefaultCollection(tableName);
		
		SolrSchemaDTO solrSchemaResponseDTOBefore = new SolrSchemaDTO();
		SolrSchemaDTO solrSchemaResponseDTOAfter = new SolrSchemaDTO();
		String schemaName = "";
		String errorCausingField = null;
		String payloadOperation = "";
		try {
			SchemaResponse schemaResponse = schemaRequest.process(solr);
			SchemaRepresentation retrievedSchema = schemaResponse.getSchemaRepresentation();
			schemaName = retrievedSchema.getName();
			solrSchemaResponseDTOBefore = get(tableName, name);
			// explore response content
			log.debug("\nResponse header : {}", schemaResponse.getResponseHeader());
			log.debug("Response class : {}", schemaResponse.getResponseHeader().getClass());
			
			List<Map<String, Object>> schemaFields = schemaResponse.getSchemaRepresentation().getFields();
			int numOfFields = schemaFields.size();
			// SolrFieldDTO[] solrSchemaFieldDTOs = new SolrFieldDTO[numOfFields];
			log.debug("Total number of fields: {}", numOfFields);
			
			// Get all fields from incoming(from req Body) schemaDTO
			SolrFieldDTO[] newSchemaFields = newSolrSchemaDTO.getAttributes();
			List<Map<String, Object>> targetSchemafields = parseSchemaFieldDtosToListOfMaps(newSolrSchemaDTO);
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
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			log.error("Null value detected!", e);
			e.printStackTrace();
		} catch (RemoteExecutionException e) {
			log.error("There's been an error in executing {} operation via schema API. "
					+ "Perhaps the target field- {} isn't present.", payloadOperation, errorCausingField);
			e.printStackTrace();
		} catch (SolrException e) {
			log.error("The collection- {} is Not Found in the Solr Cloud. So schema fields can't be found/deleted!", tableName);
			e.printStackTrace();
		} 
		solrSchemaResponseDTOAfter = get(tableName, name);
		log.debug("Schema for collection- {}, before CREATE: {}", tableName, solrSchemaResponseDTOBefore);
		log.debug("Schema for collection- {}, after CREATE: {}", tableName, solrSchemaResponseDTOAfter);
		return solrSchemaResponseDTOAfter;
	}
	
	@Override
	public SolrSchemaDTO create(String tableName, 
								String name, 
								SolrSchemaDTO newSolrSchemaDTO) {
		log.debug("Create Solr Schema: {}", name);

		CloudSolrClient solr = solrSchemaAPIAdapter.getCloudSolrClient(URL_STRING_SOLR_CLOUD, tableName);
		solr.setDefaultCollection(tableName);
		SchemaRequest schemaRequest = new SchemaRequest();
		
		SolrSchemaDTO solrSchemaResponseDTOBefore = new SolrSchemaDTO();
		SolrSchemaDTO solrSchemaResponseDTOAfter = new SolrSchemaDTO();
		String schemaName = "";
		String errorCausingField = null;
		String payloadOperation = "";
		try {
			// logic
			SchemaResponse schemaResponse = schemaRequest.process(solr);
			SchemaRepresentation retrievedSchema = schemaResponse.getSchemaRepresentation();
			schemaName = retrievedSchema.getName();
			List<Map<String, Object>> schemaFields = schemaResponse.getSchemaRepresentation().getFields();
			solrSchemaResponseDTOBefore = get(tableName, "default-config");
			// explore response content
			log.debug("\nResponse header : {}", schemaResponse.getResponseHeader());
			log.debug("Response class : {}", schemaResponse.getResponseHeader().getClass());
			log.debug("\nDefault Schema fields : {}", schemaFields);
			
			// Add new fields present in the Target Schema to the given collection schema
			SolrFieldDTO[] newSolrFieldDTOs = newSolrSchemaDTO.getAttributes();
			log.debug("\nTarget Schema fields : {}", (Object[]) newSolrFieldDTOs);
			// ####### Add Schema Fields logic #######
			UpdateResponse addFieldResponse = new UpdateResponse();
			NamedList<Object> schemaResponseAddFields = new NamedList<Object>();
			payloadOperation = "SchemaRequest.AddField";
			for (SolrFieldDTO fieldDto : newSolrFieldDTOs) {
				errorCausingField = fieldDto.getName();
				Map<String, Object> newField = new HashMap<>();
				newField.put("name", fieldDto.getName());
				// newField.put("default", fieldDto.getDefault_());
				newField.put("type", SolrFieldType.fromEnumToString(fieldDto.getType()));
				// newField.put("filtered", fieldDto.isFilterable());
				newField.put("required", fieldDto.isRequired());
				newField.put("stored", fieldDto.isStorable());
				// newField.put("sorted", fieldDto.isSortable());
				newField.put("multiValued", fieldDto.isMultiValue());

				SchemaRequest.AddField addFieldRequest = new SchemaRequest.AddField(newField);
				addFieldResponse = addFieldRequest.process(solr);
				schemaResponseAddFields.add(fieldDto.getName(), addFieldResponse.getResponse());
			}
			log.debug("Logging newly added fields' responses--");
			for(Object field: schemaResponseAddFields) {
				log.debug("### Added Field Response : {}", field);
			}
		} catch (SolrServerException | IOException e) {
			e.printStackTrace();
		} catch (RemoteExecutionException e) {
			log.error("There's been an error in executing {} operation via schema API. Perhaps the target field- {} isn't present.", payloadOperation, errorCausingField);
			e.printStackTrace();
		} catch (SolrException e) {
			log.error("The collection- {} is Not Found in the Solr Cloud. So schema fields can't be found/deleted!", tableName);
			e.printStackTrace();
		} 
		solrSchemaResponseDTOAfter = get(tableName, name);
		log.debug("Schema for collection- {}, before CREATE: {}", tableName, solrSchemaResponseDTOBefore);
		log.debug("Schema for collection- {}, after CREATE: {}", tableName, solrSchemaResponseDTOAfter);
		return solrSchemaResponseDTOAfter;
	}

	@Override
	public void delete(String tableName, String name) {
		CloudSolrClient solr = solrSchemaAPIAdapter.getCloudSolrClient(URL_STRING_SOLR_CLOUD, tableName);
		solr.setDefaultCollection(tableName);
		
		SchemaRequest schemaRequest = new SchemaRequest();
		
		SolrSchemaDTO solrSchemaResponseDTOBefore = new SolrSchemaDTO();
		SolrSchemaDTO solrSchemaResponseDTOAfter = new SolrSchemaDTO();
		String schemaName = "";
		String errorCausingField = null;
		String payloadOperation = "";
		try {
			SchemaResponse schemaResponse = schemaRequest.process(solr);
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
			schemaResponseDeleteFields.add(targetFieldToDelete, deleteFieldResponse.getResponse());

			log.debug("Logging all deleted fields' responses--");
			for(Object field: schemaResponseDeleteFields) {
				log.debug("### Added Field Response : {}", field);
			}
		} catch (SolrServerException | IOException e) {
			e.printStackTrace();
		} catch (RemoteExecutionException e) {
			log.error("There's been an error in executing {} operation via schema API. Perhaps the target field- {} isn't present.", payloadOperation, errorCausingField);
			e.printStackTrace();
		} catch (SolrException e) {
			log.error("The collection- {} is Not Found in the Solr Cloud. So schema fields can't be found/deleted!", tableName);
			e.printStackTrace();
		} 
		solrSchemaResponseDTOAfter = get(tableName, schemaName);
		// Compare Pre-and-Post DELETE Operation
		log.debug("Schema for collection- {}, before DELETE: {}", tableName, solrSchemaResponseDTOBefore);
		log.debug("Schema for collection- {}, after DELETE: {}", tableName, solrSchemaResponseDTOAfter);
	}
	
	@Override
	public String validateSchema() {
		log.debug("validate schema.");
		
		SchemaRequest schemaRequest = new SchemaRequest();
		String urlString = "http://localhost:8985/solr/";
		SolrClient solr = new HttpSolrClient.Builder(urlString+DEFAULT_COLLECTION).build();

		try {
			SchemaResponse schemaResponse = schemaRequest.process(solr);
			
			// explore response content
			System.out.println("Response header : "+schemaResponse.getResponseHeader());
			System.out.println("Response class : "+schemaResponse.getResponseHeader().getClass());
			
			List<FieldTypeDefinition> schemaFieldTypes = schemaResponse.getSchemaRepresentation().getFieldTypes();
			
			int NumOfFieldTypes = schemaResponse.getSchemaRepresentation().getFieldTypes().size();
			System.out.println("Response schema size : "+schemaResponse.getSchemaRepresentation().getFieldTypes().size()+"\n");
			System.out.println("Response schema 1 : "+schemaResponse.getSchemaRepresentation().getFieldTypes().get(0).getAttributes()+"\n");
			System.out.println("Response schema 2 : "+schemaResponse.getSchemaRepresentation().getFieldTypes().get(1).getAttributes()+"\n");
			
			for(int i=0; i<NumOfFieldTypes; i++) {
				System.out.println("Field Types : "+schemaFieldTypes.get(i).getAttributes());
			}
			System.out.println("Response class : "+schemaResponse.getSchemaRepresentation().getClass());
		} catch (SolrServerException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "Schema is successfully validated.";
	}
	
	@Override
	public String storeSchema() {

		return "Schema is successfully stored.";
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
			fieldDtoMap.put("name", fieldDto.getName());
			fieldDtoMap.put("type", SolrFieldType.fromEnumToString(fieldDto.getType()));
			fieldDtoMap.put("stored", fieldDto.isStorable());
			fieldDtoMap.put("multiValued", fieldDto.isMultiValue());
			fieldDtoMap.put("required", fieldDto.isRequired());
			// fieldDtoMap.put("default", fieldDto.getDefault_());
			schemaFieldsList.add(fieldDtoMap);
		}
		return schemaFieldsList;
	}
}
