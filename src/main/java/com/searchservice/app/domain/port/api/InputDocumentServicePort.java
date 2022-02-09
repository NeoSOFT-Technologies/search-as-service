package com.searchservice.app.domain.port.api;

import com.searchservice.app.domain.dto.throttler.ThrottlerResponseDTO;

public interface InputDocumentServicePort {

	ThrottlerResponseDTO addDocuments(String collectionName, String payload);
	ThrottlerResponseDTO addDocument(String collectionName, String payload);

}
