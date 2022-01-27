package com.solr.clientwrapper.domain.port.api;

import com.solr.clientwrapper.domain.dto.ResponseDTO;

public interface InputDocumentServicePort {

	ResponseDTO addDocuments(String collectionName, String payload, boolean isNRT);

}
