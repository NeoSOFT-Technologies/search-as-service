package com.searchservice.app.domain.port.api;

import com.searchservice.app.domain.dto.throttler.ThrottlerResponse;

public interface InputDocumentServicePort {

	ThrottlerResponse addDocuments(int commitType,String collectionName, String payload);
	//ThrottlerResponse addDocument(String collectionName, String payload);

}
