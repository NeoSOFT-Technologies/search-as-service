package com.solr.clientwrapper.domain.port.api;

import com.solr.clientwrapper.domain.dto.solr.SolrResponseDTO;

public interface SolrCoreServicePort {

    SolrResponseDTO createCore(String coreName);

    SolrResponseDTO renameCore(String coreName, String newName);

    SolrResponseDTO deleteCore(String coreName, boolean deleteIndex, boolean deleteDataDir, boolean deleteInstanceDir);

    SolrResponseDTO swapCore(String coreOne, String coreTwo);

    SolrResponseDTO reloadCore(String coreName);

    String statusCore(String coreName);

}
