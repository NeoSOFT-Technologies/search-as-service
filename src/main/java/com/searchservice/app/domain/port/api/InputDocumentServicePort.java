package com.searchservice.app.domain.port.api;

import com.searchservice.app.domain.dto.throttler.ThrottlerResponse;

public interface InputDocumentServicePort {

	ThrottlerResponse addDocuments(boolean isNrt,String collectionName, String payload);
}
