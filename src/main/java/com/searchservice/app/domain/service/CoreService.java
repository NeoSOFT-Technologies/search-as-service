package com.searchservice.app.domain.service;


import com.searchservice.app.domain.dto.ResponseDTO;
import com.searchservice.app.domain.port.api.CoreServicePort;
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
public class CoreService implements CoreServicePort {

    private final Logger log = LoggerFactory.getLogger(CoreService.class);

    //http://localhost:8983/solr
    @Value("${base-solr-url}")
    private String baseSolrUrl;
    
    String constantstring="Exception";

    @Override
    public ResponseDTO create(String coreName) {

        log.debug("create");

        CoreAdminRequest.Create request = new CoreAdminRequest.Create();
        request.setCoreName(coreName);
        request.setInstanceDir(coreName);
        request.setConfigSet("_default");
        request.setDataDir("data");

        ResponseDTO solrResponseDTO=new ResponseDTO(coreName);
        
        try {
            solrResponseDTO.setStatusCode(200);
            solrResponseDTO.setMessage("Successfully created Solr Core: "+coreName);

        } catch (Exception e){
            log.error(e.toString());

            solrResponseDTO.setStatusCode(400);
            solrResponseDTO.setMessage("Unable to create Solr Core: "+coreName+constantstring);

        }

        return solrResponseDTO;
    }

    @Override
    public ResponseDTO rename(String coreName, String newName) {

        log.debug("rename");

        ResponseDTO solrResponseDTO=new ResponseDTO(coreName);
        
        try {
            solrResponseDTO.setStatusCode(200);
            solrResponseDTO.setMessage("Successfully renamed Solr Core: "+coreName+" to "+newName);

        } catch (Exception e){
            log.error(e.toString());

            solrResponseDTO.setStatusCode(400);
            solrResponseDTO.setMessage("Unable to rename Solr Core: "+coreName+" to "+newName+constantstring);

        }

        return solrResponseDTO;
    }

    @Override
    public ResponseDTO delete(String coreName) {

        log.debug("delete");


        CoreAdminRequest.Unload request=new CoreAdminRequest.Unload(true);
        request.setCoreName(coreName);
        request.setDeleteIndex(true);
        request.setDeleteDataDir(true);
        request.setDeleteInstanceDir(true);

        ResponseDTO solrResponseDTO=new ResponseDTO(coreName);
        HttpSolrClient solrClient = new HttpSolrClient.Builder(baseSolrUrl).build();
        try {
            request.process(solrClient);

            solrResponseDTO.setStatusCode(200);
            solrResponseDTO.setMessage("Successfully deleted Solr Core: "+coreName);

        } catch (Exception e){
            log.error(e.toString());

            solrResponseDTO.setStatusCode(400);
            solrResponseDTO.setMessage("Unable to delete Solr Core: "+coreName+constantstring);

        }

        return solrResponseDTO;

    }

    @Override
    public ResponseDTO swap(String coreOne, String coreTwo) {

        log.debug("swap");

        ResponseDTO solrResponseDTO=new ResponseDTO(coreOne);
        HttpSolrClient solrClient = new HttpSolrClient.Builder(baseSolrUrl).build();
        try {
            CoreAdminRequest.swapCore(coreOne,coreTwo,solrClient);

            solrResponseDTO.setStatusCode(200);
            solrResponseDTO.setMessage("Successfully swapped Solr Core: "+coreOne+" to "+coreTwo);

        } catch (Exception e){
            log.error(e.toString());

            solrResponseDTO.setStatusCode(400);
            solrResponseDTO.setMessage("Unable to swap Solr Core: "+coreOne+" to "+coreTwo+constantstring);

        }

        return solrResponseDTO;
    }

    @Override
    public ResponseDTO reload(String coreName) {

        log.debug("reload");

        ResponseDTO solrResponseDTO=new ResponseDTO(coreName);
        try {
            solrResponseDTO.setStatusCode(200);
            solrResponseDTO.setMessage("Successfully reloaded Solr Core: "+coreName);

        } catch (Exception e){
            log.error(e.toString());

            solrResponseDTO.setStatusCode(400);
            solrResponseDTO.setMessage("Unable to reload Solr Core: "+coreName+constantstring);

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
