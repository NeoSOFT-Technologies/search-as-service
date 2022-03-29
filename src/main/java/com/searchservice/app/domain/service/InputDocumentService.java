
package com.searchservice.app.domain.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.searchservice.app.domain.dto.logger.LoggersDTO;
import com.searchservice.app.domain.dto.throttler.ThrottlerResponse;
import com.searchservice.app.domain.port.api.InputDocumentServicePort;
import com.searchservice.app.domain.port.api.ManageTableServicePort;
import com.searchservice.app.domain.utils.LoggerUtils;
import com.searchservice.app.domain.utils.UploadDocumentUtil;
import com.searchservice.app.rest.errors.BadRequestOccurredException;

@Service
public class InputDocumentService implements InputDocumentServicePort {
	private final Logger log = LoggerFactory.getLogger(InputDocumentService.class);

	@Value("${base-search-url}")
	private String baseSearchUrl;

	private String servicename = "Input_Document_Service";

	private String username = "Username";

	@Autowired
	public final ManageTableServicePort manageTableServicePort;

	@Autowired
	LoggerUtils loggerUtils;

	public InputDocumentService(ManageTableServicePort manageTableServicePort,LoggerUtils loggerUtils) {
		this.manageTableServicePort = manageTableServicePort;
		this.loggerUtils = loggerUtils;

	}

	private void requestMethod(LoggersDTO loggersDTO, String nameofCurrMethod) {

		String timestamp = LoggerUtils.utcTime().toString();
		loggersDTO.setNameofmethod(nameofCurrMethod);
		loggersDTO.setTimestamp(timestamp);
		loggersDTO.setServicename(servicename);
		loggersDTO.setUsername(username);
	}

	private void extracted(ThrottlerResponse responseDTO, UploadDocumentUtil.UploadDocumentSearchUtilRespnse response) {

		if (response.isDocumentUploaded()) {
			responseDTO.setMessage("Successfully Added!");
			responseDTO.setStatusCode(200);
		} else {
			responseDTO.setMessage(response.getMessage());
			responseDTO.setStatusCode(400);
		}
	}

	private UploadDocumentUtil extracted(String tableName, String payload) {
		UploadDocumentUtil uploadDocumentUtil = new UploadDocumentUtil();

		uploadDocumentUtil.setBaseSearchUrl(baseSearchUrl);
		uploadDocumentUtil.setTableName(tableName);
		uploadDocumentUtil.setContent(payload);
		return uploadDocumentUtil;
	}

	@Override
	public ThrottlerResponse addDocuments(String tableName, String payload, LoggersDTO loggersDTO) {

		if (!manageTableServicePort.isTableExists(tableName))
			throw new BadRequestOccurredException(400, tableName.split("_")[0] + " table doesn't exist");

		String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
		requestMethod(loggersDTO, nameofCurrMethod);
		loggerUtils.printlogger(loggersDTO, true, false);

		ThrottlerResponse responseDTO = new ThrottlerResponse();

		// CODE COMES HERE ONLY AFTER IT'S VERIFIED THAT THE PAYLOAD AND THE SCHEMAARE
		// STRUCTURALLY CORRECT

		UploadDocumentUtil uploadDocumentUtil = extracted(tableName, payload);

		UploadDocumentUtil.UploadDocumentSearchUtilRespnse response = uploadDocumentUtil.commit();

		extracted(responseDTO, response);
		String timestamp = LoggerUtils.utcTime().toString();
		loggersDTO.setTimestamp(timestamp);
		loggerUtils.printlogger(loggersDTO, false, false);

		return responseDTO;

	}

	@Override
	public ThrottlerResponse addDocument(String tableName, String payload, LoggersDTO loggersDTO) {

		if (!manageTableServicePort.isTableExists(tableName))
			throw new BadRequestOccurredException(400, tableName.split("_")[0] + " table doesn't exist");

		String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
		requestMethod(loggersDTO, nameofCurrMethod);
		loggerUtils.printlogger(loggersDTO, true, false);

		ThrottlerResponse responseDTO = new ThrottlerResponse();

		// CODE COMES HERE ONLY AFTER IT'S VERIFIED THAT THE PAYLOAD AND THE SCHEMAARE
		// STRUCTURALLY CORRECT

		UploadDocumentUtil uploadDocumentUtil = extracted(tableName, payload);

		UploadDocumentUtil.UploadDocumentSearchUtilRespnse response = uploadDocumentUtil.softcommit();

		extracted(responseDTO, response);
		String timestamp = LoggerUtils.utcTime().toString();
		loggersDTO.setTimestamp(timestamp);
		loggerUtils.printlogger(loggersDTO, false, false);

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
			// JsonMapper.builder().enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
			objectMapper.readTree(jsonString);
		} catch (JsonProcessingException ex) {
			valid = false;
		}
		return valid;

	}

}
