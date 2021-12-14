package com.solr.clientwrapper.domain.port.api;

import com.solr.clientwrapper.domain.dto.solr.SolrResponseDTO;

public interface SolrDocumentServicePort {

    SolrResponseDTO create(String collectionName, String payload);

}
