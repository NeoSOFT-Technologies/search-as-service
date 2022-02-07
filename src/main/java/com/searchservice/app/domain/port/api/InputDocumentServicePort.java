package com.searchservice.app.domain.port.api;

import com.searchservice.app.domain.dto.ResponseDTO;

public interface InputDocumentServicePort {

	ResponseDTO addDocuments(String collectionName, String payload);
	ResponseDTO addDocument(String collectionName, String payload);

}
