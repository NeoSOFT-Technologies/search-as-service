package com.solr.clientwrapper.domain.port.api;

import java.io.IOException;

import org.apache.solr.client.solrj.SolrServerException;

import com.solr.clientwrapper.domain.dto.solr.SolrResponseDTO;

public interface SolrDocumentServicePort {

    SolrResponseDTO addDocument(String collectionName, String payload) throws NullPointerException, SolrServerException, IOException;

    SolrResponseDTO addDocuments(String collectionName, String payload) throws NullPointerException, SolrServerException, IOException;

}
