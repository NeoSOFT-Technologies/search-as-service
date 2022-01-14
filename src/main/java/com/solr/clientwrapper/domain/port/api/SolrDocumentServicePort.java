package com.solr.clientwrapper.domain.port.api;

import com.solr.clientwrapper.domain.dto.solr.SolrResponseDTO;

public interface SolrDocumentServicePort {

	SolrResponseDTO addDocuments(String collectionName, String payload, boolean isNRT);

}
