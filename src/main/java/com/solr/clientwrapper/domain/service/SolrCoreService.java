package com.solr.clientwrapper.domain.service;

import com.solr.clientwrapper.domain.dto.solr.SolrResponseDTO;
import com.solr.clientwrapper.domain.port.api.SolrCoreServicePort;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.request.CoreAdminRequest;
import org.apache.solr.client.solrj.response.CoreAdminResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SolrCoreService implements SolrCoreServicePort {

    private final Logger log = LoggerFactory.getLogger(SolrCoreService.class);

    //http://localhost:8983/solr
    @Value("${base-solr-url}")
    private String baseSolrUrl;

    @Override
    public SolrResponseDTO create(String coreName) {

        log.debug("create");

        CoreAdminRequest.Create request = new CoreAdminRequest.Create();
        request.setCoreName(coreName);
        request.setInstanceDir(coreName);
        request.setConfigSet("_default");
        request.setDataDir("data");

        SolrResponseDTO solrResponseDTO=new SolrResponseDTO(coreName);
        HttpSolrClient solrClient = new HttpSolrClient.Builder(baseSolrUrl).build();
        try {
            CoreAdminResponse coreAdminResponse =request.process(solrClient);

            solrResponseDTO.setStatusCode(200);
            solrResponseDTO.setMessage("Successfully created Solr Core: "+coreName);

        } catch (Exception e){
            log.error(e.toString());

            solrResponseDTO.setStatusCode(400);
            solrResponseDTO.setMessage("Unable to create Solr Core: "+coreName+". Exception.");

        }

        return solrResponseDTO;
    }

    @Override
    public SolrResponseDTO rename(String coreName, String newName) {

        log.debug("rename");

        SolrResponseDTO solrResponseDTO=new SolrResponseDTO(coreName);
        HttpSolrClient solrClient = new HttpSolrClient.Builder(baseSolrUrl).build();
        try {
            CoreAdminResponse coreAdminResponse=CoreAdminRequest.renameCore(coreName,newName,solrClient);

            solrResponseDTO.setStatusCode(200);
            solrResponseDTO.setMessage("Successfully renamed Solr Core: "+coreName+" to "+newName);

        } catch (Exception e){
            log.error(e.toString());

            solrResponseDTO.setStatusCode(400);
            solrResponseDTO.setMessage("Unable to rename Solr Core: "+coreName+" to "+newName+". Exception.");

        }

        return solrResponseDTO;
    }

    @Override
    public SolrResponseDTO delete(String coreName) {

        log.debug("delete");


        CoreAdminRequest.Unload request=new CoreAdminRequest.Unload(true);
        request.setCoreName(coreName);
        request.setDeleteIndex(true);
        request.setDeleteDataDir(true);
        request.setDeleteInstanceDir(true);

        SolrResponseDTO solrResponseDTO=new SolrResponseDTO(coreName);
        HttpSolrClient solrClient = new HttpSolrClient.Builder(baseSolrUrl).build();
        try {
            request.process(solrClient);

            solrResponseDTO.setStatusCode(200);
            solrResponseDTO.setMessage("Successfully deleted Solr Core: "+coreName);

        } catch (Exception e){
            log.error(e.toString());

            solrResponseDTO.setStatusCode(400);
            solrResponseDTO.setMessage("Unable to delete Solr Core: "+coreName+". Exception.");

        }

        return solrResponseDTO;

    }

    @Override
    public SolrResponseDTO swap(String coreOne, String coreTwo) {

        log.debug("swap");

        SolrResponseDTO solrResponseDTO=new SolrResponseDTO(coreOne);
        HttpSolrClient solrClient = new HttpSolrClient.Builder(baseSolrUrl).build();
        try {
            CoreAdminRequest.swapCore(coreOne,coreTwo,solrClient);

            solrResponseDTO.setStatusCode(200);
            solrResponseDTO.setMessage("Successfully swapped Solr Core: "+coreOne+" to "+coreTwo);

        } catch (Exception e){
            log.error(e.toString());

            solrResponseDTO.setStatusCode(400);
            solrResponseDTO.setMessage("Unable to swap Solr Core: "+coreOne+" to "+coreTwo+". Exception.");

        }

        return solrResponseDTO;
    }

    @Override
    public SolrResponseDTO reload(String coreName) {

        log.debug("reload");

        SolrResponseDTO solrResponseDTO=new SolrResponseDTO(coreName);
        HttpSolrClient solrClient = new HttpSolrClient.Builder(baseSolrUrl).build();
        try {
            CoreAdminResponse coreAdminResponse=CoreAdminRequest.reloadCore(coreName,solrClient);

            solrResponseDTO.setStatusCode(200);
            solrResponseDTO.setMessage("Successfully reloaded Solr Core: "+coreName);

        } catch (Exception e){
            log.error(e.toString());

            solrResponseDTO.setStatusCode(400);
            solrResponseDTO.setMessage("Unable to reload Solr Core: "+coreName+". Exception.");

        }

        return solrResponseDTO;
    }

    @Override
    public String status(String coreName) {

        log.debug("status");

        CoreAdminResponse coreAdminResponse= null;
        HttpSolrClient solrClient = new HttpSolrClient.Builder(baseSolrUrl).build();
        try {
            coreAdminResponse = CoreAdminRequest.getStatus(coreName,solrClient);
            return coreAdminResponse.toString();
        } catch (Exception e) {
            log.error(e.toString());
            return null;
        }

    }


}