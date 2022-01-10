package com.solr.clientwrapper.usecase.solr.document;

import com.solr.clientwrapper.domain.dto.solr.SolrResponseDTO;
import com.solr.clientwrapper.domain.port.api.SolrDocumentServicePort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CreateSolrDocument {

    private final Logger log = LoggerFactory.getLogger(CreateSolrDocument.class);

    private final SolrDocumentServicePort solrDocumentServicePort;

    public CreateSolrDocument(SolrDocumentServicePort solrDocumentServicePort) {
        this.solrDocumentServicePort = solrDocumentServicePort;
    }

    public SolrResponseDTO addDocuments(String collectionName, String payload, boolean isNRT) {
        log.debug("create");
        return solrDocumentServicePort.addDocuments(collectionName, payload, isNRT);
    }
}