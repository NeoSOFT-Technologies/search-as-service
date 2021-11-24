package com.solr.clientwrapper.domain.service;

import com.solr.clientwrapper.domain.dto.solr.SolrResponseDTO;
import com.solr.clientwrapper.domain.port.api.SolrCoreServicePort;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.request.CoreAdminRequest;
import org.apache.solr.client.solrj.response.CoreAdminResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Service
@Transactional
public class SolrCoreService implements SolrCoreServicePort {

    HttpSolrClient solrClient = new HttpSolrClient.Builder("http://localhost:8983/solr").build();

    @Override
    public SolrResponseDTO createCore(String coreName) {
        //HttpSolrClient solrClient = new HttpSolrClient.Builder("http://localhost:8983/solr").build();
        CoreAdminRequest.Create request = new CoreAdminRequest.Create();
        request.setCoreName(coreName);
        request.setInstanceDir(coreName);
        request.setConfigSet("_default");
        request.setDataDir("data");

        SolrResponseDTO solrResponseDTO=new SolrResponseDTO();
        solrResponseDTO.setCoreName(coreName);

        try {
            CoreAdminResponse coreAdminResponse =request.process(solrClient);

            solrResponseDTO.setStatusCode(200);
            solrResponseDTO.setMessage("Successfully created Solr Core: "+coreName);

        } catch (SolrServerException e) {
            e.printStackTrace();

            solrResponseDTO.setStatusCode(400);
            solrResponseDTO.setMessage("Unable to create Solr Core: "+coreName+". SolrServerException.");

        } catch (IOException e) {
            e.printStackTrace();

            solrResponseDTO.setStatusCode(400);
            solrResponseDTO.setMessage("Unable to create Solr Core: "+coreName+". IOException.");

        }catch (Exception e){
            e.printStackTrace();

            solrResponseDTO.setStatusCode(400);
            solrResponseDTO.setMessage("Unable to create Solr Core: "+coreName+". Exception.");

        }

        return solrResponseDTO;
    }

    @Override
    public SolrResponseDTO renameCore(String coreName, String newName) {

        SolrResponseDTO solrResponseDTO=new SolrResponseDTO();
        solrResponseDTO.setCoreName(coreName);

        try {
            CoreAdminResponse coreAdminResponse=CoreAdminRequest.renameCore(coreName,newName,solrClient);

            solrResponseDTO.setStatusCode(200);
            solrResponseDTO.setMessage("Successfully renamed Solr Core: "+coreName+" to "+newName);

        } catch (SolrServerException e) {
            e.printStackTrace();

            solrResponseDTO.setStatusCode(400);
            solrResponseDTO.setMessage("Unable to rename Solr Core: "+coreName+" to "+newName+". SolrServerException.");

        } catch (IOException e) {
            e.printStackTrace();

            solrResponseDTO.setStatusCode(400);
            solrResponseDTO.setMessage("Unable to rename Solr Core: "+coreName+" to "+newName+". IOException.");

        }catch (Exception e){
            e.printStackTrace();

            solrResponseDTO.setStatusCode(400);
            solrResponseDTO.setMessage("Unable to rename Solr Core: "+coreName+" to "+newName+". Exception.");

        }

        return solrResponseDTO;
    }

    @Override
    public SolrResponseDTO deleteCore(String coreName, boolean deleteIndex, boolean deleteDataDir, boolean deleteInstanceDir) {
        CoreAdminRequest.Unload request=new CoreAdminRequest.Unload(true);
        request.setCoreName(coreName);
        request.setDeleteIndex(deleteIndex);
        request.setDeleteDataDir(deleteDataDir);
        request.setDeleteInstanceDir(deleteInstanceDir);

        SolrResponseDTO solrResponseDTO=new SolrResponseDTO();
        solrResponseDTO.setCoreName(coreName);

        try {
            request.process(solrClient);

            solrResponseDTO.setStatusCode(200);
            solrResponseDTO.setMessage("Successfully deleted Solr Core: "+coreName);

        } catch (SolrServerException e) {
            e.printStackTrace();

            solrResponseDTO.setStatusCode(400);
            solrResponseDTO.setMessage("Unable to delete Solr Core: "+coreName+". SolrServerException.");

        } catch (IOException e) {
            e.printStackTrace();

            solrResponseDTO.setStatusCode(400);
            solrResponseDTO.setMessage("Unable to delete Solr Core: "+coreName+". IOException.");

        }catch (Exception e){
            e.printStackTrace();

            solrResponseDTO.setStatusCode(400);
            solrResponseDTO.setMessage("Unable to delete Solr Core: "+coreName+". Exception.");

        }

        return solrResponseDTO;

    }

    @Override
    public SolrResponseDTO swapCore(String coreOne, String coreTwo) {

        SolrResponseDTO solrResponseDTO=new SolrResponseDTO();
        solrResponseDTO.setCoreName(coreOne);

        try {
            CoreAdminRequest.swapCore(coreOne,coreTwo,solrClient);

            solrResponseDTO.setStatusCode(200);
            solrResponseDTO.setMessage("Successfully swapped Solr Core: "+coreOne+" to "+coreTwo);

        } catch (SolrServerException e) {
            e.printStackTrace();

            solrResponseDTO.setStatusCode(400);
            solrResponseDTO.setMessage("Unable to swap Solr Core: "+coreOne+" to "+coreTwo+". SolrServerException.");

        } catch (IOException e) {
            e.printStackTrace();

            solrResponseDTO.setStatusCode(400);
            solrResponseDTO.setMessage("Unable to swap Solr Core: "+coreOne+" to "+coreTwo+". IOException.");

        }catch (Exception e){
            e.printStackTrace();

            solrResponseDTO.setStatusCode(400);
            solrResponseDTO.setMessage("Unable to swap Solr Core: "+coreOne+" to "+coreTwo+". Exception.");

        }

        return solrResponseDTO;
    }

    @Override
    public SolrResponseDTO reloadCore(String coreName) {


        SolrResponseDTO solrResponseDTO=new SolrResponseDTO();
        solrResponseDTO.setCoreName(coreName);

        try {
            CoreAdminResponse coreAdminResponse=CoreAdminRequest.reloadCore(coreName,solrClient);

            solrResponseDTO.setStatusCode(200);
            solrResponseDTO.setMessage("Successfully reloaded Solr Core: "+coreName);

        } catch (SolrServerException e) {
            e.printStackTrace();

            solrResponseDTO.setStatusCode(400);
            solrResponseDTO.setMessage("Unable to reload Solr Core: "+coreName+". SolrServerException.");

        } catch (IOException e) {
            e.printStackTrace();

            solrResponseDTO.setStatusCode(400);
            solrResponseDTO.setMessage("Unable to reload Solr Core: "+coreName+". IOException.");

        }catch (Exception e){
            e.printStackTrace();

            solrResponseDTO.setStatusCode(400);
            solrResponseDTO.setMessage("Unable to reload Solr Core: "+coreName+". Exception.");

        }

        return solrResponseDTO;
    }

    @Override
    public String statusCore(String coreName) {

        CoreAdminResponse coreAdminResponse= null;
        try {
            coreAdminResponse = CoreAdminRequest.getStatus(coreName,solrClient);
            return coreAdminResponse.toString();
        } catch (SolrServerException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }


}
