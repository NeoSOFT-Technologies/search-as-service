package com.searchservice.app.domain.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.searchservice.app.domain.dto.throttler.ThrottlerResponse;
import com.searchservice.app.domain.port.api.InputDocumentServicePort;
import com.searchservice.app.domain.utils.UploadDocumentUtil;

@Service
public class InputDocumentService implements InputDocumentServicePort {
	private final Logger log = LoggerFactory.getLogger(InputDocumentService.class);
	
	@Value("${base-solr-url}")
	private String baseSolrUrl;

	
	private void extracted(ThrottlerResponse responseDTO,
			UploadDocumentUtil.UploadDocumentSolrUtilRespnse response) {
		if (response.isDocumentUploaded()) {
			responseDTO.setResponseMessage("Successfully Added!");
			responseDTO.setStatusCode(200);
		} else {
			responseDTO.setResponseMessage(response.getMessage());
			responseDTO.setStatusCode(400);
		}
	}

	private UploadDocumentUtil extracted(String tableName, String payload) {
		UploadDocumentUtil uploadDocumentUtil = new UploadDocumentUtil();

		uploadDocumentUtil.setBaseSolrUrl(baseSolrUrl);
		uploadDocumentUtil.setTableName(tableName);
		uploadDocumentUtil.setContent(payload);
		return uploadDocumentUtil;
	}

	@Override
	public ThrottlerResponse addDocuments(String tableName, String payload) {

		ThrottlerResponse responseDTO = new ThrottlerResponse();

		// CODE COMES HERE ONLY AFTER IT'S VERIFIED THAT THE PAYLOAD AND THE SCHEMA ARE
		// STRUCTURALLY CORRECT

		UploadDocumentUtil uploadDocumentUtil = extracted(tableName, payload);

		UploadDocumentUtil.UploadDocumentSolrUtilRespnse response = uploadDocumentUtil.commit();

		extracted(responseDTO, response);
		return responseDTO;

	}

	@Override
	public ThrottlerResponse addDocument(String tableName, String payload) {
		ThrottlerResponse responseDTO = new ThrottlerResponse();

		// CODE COMES HERE ONLY AFTER IT'S VERIFIED THAT THE PAYLOAD AND THE SCHEMA ARE
		// STRUCTURALLY CORRECT

		UploadDocumentUtil uploadDocumentUtil = extracted(tableName, payload);

		UploadDocumentUtil.UploadDocumentSolrUtilRespnse response = uploadDocumentUtil.softcommit();

		extracted(responseDTO, response);

		return responseDTO;
	}

}