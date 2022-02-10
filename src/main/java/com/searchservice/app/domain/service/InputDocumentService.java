package com.searchservice.app.domain.service;

import java.util.Map;

import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.searchservice.app.domain.dto.throttler.ThrottlerResponseDTO;
import com.searchservice.app.domain.port.api.InputDocumentServicePort;
import com.searchservice.app.domain.utils.DocumentParserUtil;
import com.searchservice.app.domain.utils.UploadDocumentUtil;

@Service
public class InputDocumentService implements InputDocumentServicePort {

	@Value("${base-solr-url}")
	private String baseSolrUrl;

	private final Logger log = LoggerFactory.getLogger(InputDocumentService.class);

	private ThrottlerResponseDTO extracted(ThrottlerResponseDTO responseDTO, Exception e, Exception e1) {
		log.error("Exception: {} Exception: {}", e, e1);

		String message = "Invalid input JSON array of document.";
		log.debug(message);
		responseDTO.setResponseMessage(message);
		responseDTO.setStatusCode(400);
		return responseDTO;
	}

	private void extracted(ThrottlerResponseDTO responseDTO,
			UploadDocumentUtil.UploadDocumentSolrUtilRespnse response) {
		if (response.isDocumentUploaded()) {
			responseDTO.setResponseMessage("Successfully Added!");
			responseDTO.setStatusCode(200);
		} else {
			responseDTO.setResponseMessage(response.getMessage());
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
	public ThrottlerResponseDTO addDocuments(String collectionName, String payload) {

		ThrottlerResponseDTO responseDTO = new ThrottlerResponseDTO();

		// CODE COMES HERE ONLY AFTER IT'S VERIFIED THAT THE PAYLOAD AND THE SCHEMA ARE
		// STRUCTURALLY CORRECT

		UploadDocumentUtil uploadDocumentUtil = extracted(collectionName, payload);

		UploadDocumentUtil.UploadDocumentSolrUtilRespnse response = uploadDocumentUtil.commit();

		extracted(responseDTO, response);
		return responseDTO;

	}

	@Override
	public ThrottlerResponseDTO addDocument(String collectionName, String payload) {
		ThrottlerResponseDTO responseDTO = new ThrottlerResponseDTO();

		// CODE COMES HERE ONLY AFTER IT'S VERIFIED THAT THE PAYLOAD AND THE SCHEMA ARE
		// STRUCTURALLY CORRECT

		UploadDocumentUtil uploadDocumentUtil = extracted(collectionName, payload);

		UploadDocumentUtil.UploadDocumentSolrUtilRespnse response = uploadDocumentUtil.softcommit();

		extracted(responseDTO, response);

		return responseDTO;
	}

}