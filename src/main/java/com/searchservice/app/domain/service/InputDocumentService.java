
package com.searchservice.app.domain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.searchservice.app.domain.dto.throttler.ThrottlerResponse;
import com.searchservice.app.domain.port.api.InputDocumentServicePort;
import com.searchservice.app.domain.port.api.ManageTableServicePort;
import com.searchservice.app.domain.port.api.TableDeleteServicePort;
import com.searchservice.app.domain.utils.UploadDocumentUtil;
import com.searchservice.app.infrastructure.adaptor.SearchJAdapter;
import com.searchservice.app.rest.errors.CustomException;
import com.searchservice.app.rest.errors.HttpStatusCode;

@Service
public class InputDocumentService implements InputDocumentServicePort {
	

	@Value("${base-search-url}")
	private String searchURL;

     @Autowired
	UploadDocumentUtil uploadDocumentUtil;
     
    @Autowired
  	public  ManageTableServicePort manageTableServicePort;
  	
  	@Autowired
  	public TableDeleteServicePort tableDeleteServicePort;
  	
 	@Autowired
 	SearchJAdapter searchJAdapter;

 	public InputDocumentService(ManageTableServicePort manageTableServicePort,
 			TableDeleteServicePort tableDeleteServicePort) {
 		this.manageTableServicePort = manageTableServicePort;
 		this.tableDeleteServicePort = tableDeleteServicePort;

 	}
	
	private void documentUploadResponse(ThrottlerResponse responseDTO, UploadDocumentUtil.UploadDocumentSearchUtilRespnse response) {		
		if (response.isDocumentUploaded()) {
			responseDTO.setMessage("Successfully Added!");
			responseDTO.setStatusCode(200);
		} else {
			responseDTO.setMessage(response.getMessage());
			responseDTO.setStatusCode(HttpStatusCode.BAD_REQUEST_EXCEPTION.getCode());
		}
	}

	private UploadDocumentUtil documentUploadResponse(String tableName, String payload) {
		uploadDocumentUtil.setBaseSearchUrl(searchURL);
		uploadDocumentUtil.setTableName(tableName);
		uploadDocumentUtil.setContent(payload);
		return uploadDocumentUtil;
	}

	@Override
	public ThrottlerResponse addDocuments(boolean isNRT,String tableName, String payload) {

		if (tableDeleteServicePort.isTableUnderDeletion(tableName))
			throw new CustomException(HttpStatusCode.UNDER_DELETION_PROCESS.getCode(),HttpStatusCode.UNDER_DELETION_PROCESS,
					String.format("Table %s Having TenantID %s is %s", tableName.split("_")[0],
							tableName.split("_")[1], HttpStatusCode.UNDER_DELETION_PROCESS.getMessage()));
		if (!isValidJsonArray(payload))
			throw new CustomException(HttpStatusCode.INVALID_JSON_INPUT.getCode(),
					HttpStatusCode.INVALID_JSON_INPUT,HttpStatusCode.INVALID_JSON_INPUT.getMessage());
		
		if (!manageTableServicePort.isTableExists(tableName)) {
			throw new CustomException(HttpStatusCode.TABLE_NOT_FOUND.getCode(),
					HttpStatusCode.TABLE_NOT_FOUND, "Table "+tableName.split("_")[0]+" For Tenant ID: "+ tableName.split("_")[1]+ " "+ HttpStatusCode.TABLE_NOT_FOUND.getMessage());
		}
		
		ThrottlerResponse responseDTO = new ThrottlerResponse();

		// CODE COMES HERE ONLY AFTER IT'S VERIFIED THAT THE PAYLOAD AND THE SCHEMAARE
		// STRUCTURALLY CORRECT
		  documentUploadResponse(tableName, payload);
        if(isNRT)
        {
		UploadDocumentUtil.UploadDocumentSearchUtilRespnse response = uploadDocumentUtil.commit();
		documentUploadResponse(responseDTO, response);
		}
		else
		{
		  UploadDocumentUtil.UploadDocumentSearchUtilRespnse response = uploadDocumentUtil.softcommit();
		  documentUploadResponse(responseDTO, response);
		}
		

		return responseDTO;

	}

	public boolean isValidJsonArray(String jsonString) {
		boolean valid = true;
		try {
			if (null == jsonString || jsonString.trim().isEmpty() || !jsonString.trim().startsWith("[{")
					|| !jsonString.trim().endsWith("}]"))
				return false;
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
			objectMapper.readTree(jsonString);
		} catch (JsonProcessingException ex) {
			valid = false;
		}
		return valid;

	}

	@Override
	public ResponseEntity<ThrottlerResponse> performDocumentInjection(boolean isNrt, String tableName, String payload,
			ThrottlerResponse documentInjectionThrottlerResponse) {
		
		searchJAdapter.checkIfSearchServerDown();
		
		ThrottlerResponse documentInjectionResponse = addDocuments(isNrt, tableName, payload);

		documentInjectionThrottlerResponse.setMessage(documentInjectionResponse.getMessage());
		documentInjectionThrottlerResponse.setStatusCode(documentInjectionResponse.getStatusCode());
		if (documentInjectionThrottlerResponse.getStatusCode() == 200) {

			return ResponseEntity.status(HttpStatus.OK).body(documentInjectionThrottlerResponse);
		} else {

			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(documentInjectionThrottlerResponse);
		}
	}

}
