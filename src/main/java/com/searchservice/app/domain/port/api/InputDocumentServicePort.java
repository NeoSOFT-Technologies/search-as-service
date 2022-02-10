package com.searchservice.app.domain.port.api;

import com.searchservice.app.domain.dto.ResponseDTO;
import com.searchservice.app.domain.dto.logger.LoggersDTO;

public interface InputDocumentServicePort {

	ResponseDTO addDocuments(String collectionName, String payload,LoggersDTO loggersDTO);
	ResponseDTO addDocument(String collectionName, String payload,LoggersDTO loggersDTO);

}
