package com.searchservice.app.domain.port.api;

import org.springframework.http.ResponseEntity;

import com.searchservice.app.domain.dto.throttler.ThrottlerResponse;

public interface InputDocumentServicePort {

	ThrottlerResponse addDocuments(boolean isNRT,String collectionName, String payload);

	ResponseEntity<ThrottlerResponse> performDocumentInjection(boolean isNrt, String tableName, String payload,
			ThrottlerResponse documentInjectionThrottlerResponse);
}
