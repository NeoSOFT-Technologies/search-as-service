package com.searchservice.app.domain.port.api;

import com.searchservice.app.domain.dto.ResponseDTO;

public interface SolrDocumentServicePort {

	ResponseDTO addDocuments(String collectionName, String payload, boolean isNRT);

}
