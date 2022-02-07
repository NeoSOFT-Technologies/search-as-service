package com.searchservice.app.domain.service;

import com.searchservice.app.domain.dto.ResponseDTO;
import com.searchservice.app.domain.port.api.InputDocumentServicePort;
import com.searchservice.app.domain.utils.DocumentParserUtil;
import com.searchservice.app.domain.utils.UploadDocumentUtil;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class InputDocumentService implements InputDocumentServicePort {

	@Value("${base-solr-url}")
	private String baseSolrUrl;

	private final Logger log = LoggerFactory.getLogger(InputDocumentService.class);

	private ResponseDTO extracted(ResponseDTO responseDTO, Exception e, Exception e1) {
		log.debug(e.toString());
		log.debug(e1.toString());

		String message = "Invalid input JSON array of document.";
		log.debug(message);
		responseDTO.setMessage(message);
		responseDTO.setStatusCode(400);
		return responseDTO;
	}

	private void extracted(ResponseDTO responseDTO, UploadDocumentUtil.UploadDocumentSolrUtilRespnse response) {
		if (response.isDocumentUploaded()) {
			responseDTO.setMessage("Successfully Added!");
			responseDTO.setStatusCode(200);
		} else {
			responseDTO.setMessage(response.getMessage());
			responseDTO.setStatusCode(400);
		}
	}
	
	private UploadDocumentUtil extracted(String collectionName, String payload) {
		UploadDocumentUtil uploadDocumentUtil = new UploadDocumentUtil();

		uploadDocumentUtil.setBaseSolrUrl(baseSolrUrl);
		uploadDocumentUtil.setCollectionName(collectionName);
		uploadDocumentUtil.setContent(payload);
		return uploadDocumentUtil;
	}

	@Override
	public ResponseDTO addDocuments(String collectionName, String payload) {

		ResponseDTO responseDTO = new ResponseDTO(collectionName);

		Map<String, Map<String, Object>> schemaKeyValuePair = DocumentParserUtil.getSchemaOfCollection(baseSolrUrl,
				collectionName);

		if (schemaKeyValuePair == (null)) {
			String message = "Unable to get the Schema. Please check the collection name again!";
			log.debug(message);
			responseDTO.setMessage(message);
			responseDTO.setStatusCode(400);
			return responseDTO;
		}

		JSONArray payloadJSONArray = null;
		try {
			payloadJSONArray = new JSONArray(payload);

		} catch (Exception e) {

			// TRY BY REMOVING THE QUOTES FROM THE STRING
			try {
				payload = payload.substring(1, payload.length() - 1);
				payloadJSONArray = new JSONArray(payload);

			} catch (Exception e1) {
				return extracted(responseDTO, e, e1);
			}
		}

		// CODE COMES HERE ONLY AFTER IT'S VERIFIED THAT THE PAYLOAD AND THE SCHEMA ARE
		// STRUCTURALLY CORRECT

		UploadDocumentUtil uploadDocumentUtil = extracted(collectionName, payload);

		UploadDocumentUtil.UploadDocumentSolrUtilRespnse response = uploadDocumentUtil.commit();

		extracted(responseDTO, response);
		return responseDTO;

	}

	@Override
	public ResponseDTO addDocument(String collectionName, String payload) {
		ResponseDTO responseDTO = new ResponseDTO(collectionName);

		Map<String, Map<String, Object>> schemaKeyValuePair = DocumentParserUtil.getSchemaOfCollection(baseSolrUrl,
				collectionName);
		if (schemaKeyValuePair == null) {
			String message = "Unable to get the Schema. Please check the collection name again!";
			log.debug(message);
			responseDTO.setMessage(message);
			responseDTO.setStatusCode(400);
			return responseDTO;
		}

		JSONArray payloadJSONArray = null;
		try {
			payloadJSONArray = new JSONArray(payload);
		} catch (Exception e) {

			// TRY BY REMOVING THE QUOTES FROM THE STRING
			try {
				payload = payload.substring(1, payload.length() - 1);
				payloadJSONArray = new JSONArray(payload);

			} catch (Exception e1) {
				return extracted(responseDTO, e, e1);
			}

		}               
		// CODE COMES HERE ONLY AFTER IT'S VERIFIED THAT THE PAYLOAD AND THE SCHEMA ARE
		// STRUCTURALLY CORRECT
		
		UploadDocumentUtil uploadDocumentUtil = extracted(collectionName, payload);

		UploadDocumentUtil.UploadDocumentSolrUtilRespnse response = uploadDocumentUtil.softcommit();

		extracted(responseDTO, response);

		return responseDTO;
	}



}