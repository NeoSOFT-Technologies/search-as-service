package com.solr.clientwrapper.domain.port.api;

public interface SolrCoreServicePort {

    boolean createCore(String coreName);

    boolean renameCore(String coreName, String newName);

    boolean deleteCore(String coreName, boolean deleteIndex, boolean deleteDataDir, boolean deleteInstanceDir);

    boolean swapCore(String coreOne, String coreTwo);

    boolean reloadCore(String coreName);

    String statusCore(String coreName);

}
