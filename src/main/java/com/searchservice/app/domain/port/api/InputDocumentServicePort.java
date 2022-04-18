package com.searchservice.app.domain.port.api;

import com.searchservice.app.domain.dto.throttler.ThrottlerResponse;

public interface InputDocumentServicePort {

	ThrottlerResponse addDocuments(boolean isNRT,String collectionName, String payload);


}
