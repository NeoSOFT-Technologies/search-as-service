package com.solr.clientwrapper.domain.service;

import com.solr.clientwrapper.domain.dto.solr.SolrResponseDTO;
import com.solr.clientwrapper.domain.port.api.SolrDocumentServicePort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SolrDocumentService implements SolrDocumentServicePort {

    @Value("${base-solr-url}")
    private String baseSolrUrl;

    @Override
    public SolrResponseDTO create(String collectionName, String payload) {
        return null;
    }

}
