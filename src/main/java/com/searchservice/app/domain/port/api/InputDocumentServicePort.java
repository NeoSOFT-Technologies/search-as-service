package com.searchservice.app.domain.port.api;

import com.searchservice.app.domain.dto.ResponseDTO;

public interface InputDocumentServicePort {

	ResponseDTO addDocuments(String collectionName, String payload,String correlationid, String ipaddress);
	ResponseDTO addDocument(String collectionName, String payload,String correlationid, String ipaddress);

}
