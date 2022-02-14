package com.searchservice.app.domain.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.searchservice.app.domain.dto.logger.LoggersDTO;
import com.searchservice.app.domain.dto.throttler.ThrottlerResponseDTO;
import com.searchservice.app.domain.port.api.InputDocumentServicePort;
import com.searchservice.app.domain.utils.LoggerUtils;
import com.searchservice.app.domain.utils.UploadDocumentUtil;

@Service
public class InputDocumentService implements InputDocumentServicePort {

	@Value("${base-solr-url}")
	private String baseSolrUrl;

	private final Logger log = LoggerFactory.getLogger(InputDocumentService.class);
	
	private String servicename = "Input_Document_Service";

	private String username = "Username";

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

	public ThrottlerResponseDTO addDocuments(String collectionName, String payload,LoggersDTO loggersDTO) {
		log.debug(" Add Document");

		String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
		String timestamp = LoggerUtils.utcTime().toString();
		loggersDTO.setNameofmethod(nameofCurrMethod);
		loggersDTO.setTimestamp(timestamp);
		loggersDTO.setServicename(servicename);
		loggersDTO.setUsername(username);
		LoggerUtils.printlogger(loggersDTO,true,false);
		
		ThrottlerResponseDTO responseDTO = new ThrottlerResponseDTO();


		// CODE COMES HERE ONLY AFTER IT'S VERIFIED THAT THE PAYLOAD AND THE SCHEMA ARE
		// STRUCTURALLY CORRECT

		UploadDocumentUtil uploadDocumentUtil = extracted(collectionName, payload);

		UploadDocumentUtil.UploadDocumentSolrUtilRespnse response = uploadDocumentUtil.commit();

		extracted(responseDTO, response);
		timestamp=LoggerUtils.utcTime().toString();
        loggersDTO.setTimestamp(timestamp);
		LoggerUtils.printlogger(loggersDTO,false,false);
        
		return responseDTO;

	}

	@Override
	public ThrottlerResponseDTO addDocument(String collectionName, String payload,LoggersDTO loggersDTO) {
		log.debug("Add Document");
		String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
		String timestamp = LoggerUtils.utcTime().toString();
		loggersDTO.setNameofmethod(nameofCurrMethod);
		loggersDTO.setTimestamp(timestamp);
		loggersDTO.setServicename(servicename);
		loggersDTO.setUsername(username);
		LoggerUtils.printlogger(loggersDTO,true,false);
		ThrottlerResponseDTO responseDTO = new ThrottlerResponseDTO();


		// CODE COMES HERE ONLY AFTER IT'S VERIFIED THAT THE PAYLOAD AND THE SCHEMA ARE
		// STRUCTURALLY CORRECT

		UploadDocumentUtil uploadDocumentUtil = extracted(collectionName, payload);

		UploadDocumentUtil.UploadDocumentSolrUtilRespnse response = uploadDocumentUtil.softcommit();

		extracted(responseDTO, response);
		timestamp=LoggerUtils.utcTime().toString();
        loggersDTO.setTimestamp(timestamp);
		
		LoggerUtils.printlogger(loggersDTO,false,false);
        
		return responseDTO;
	}

}