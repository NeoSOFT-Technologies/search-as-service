package com.solr.clientwrapper.domain.service;

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
    public boolean createCore(String coreName) {
        //HttpSolrClient solrClient = new HttpSolrClient.Builder("http://localhost:8983/solr").build();
        CoreAdminRequest.Create request = new CoreAdminRequest.Create();
        request.setCoreName(coreName);
        request.setInstanceDir(coreName);
        request.setConfigSet("_default");
        request.setDataDir("data");

        try {
            CoreAdminResponse coreAdminResponse =request.process(solrClient);
        } catch (SolrServerException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    @Override
    public boolean renameCore(String coreName, String newName) {

        try {
            CoreAdminResponse coreAdminResponse=CoreAdminRequest.renameCore(coreName,newName,solrClient);
        } catch (SolrServerException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    @Override
    public boolean deleteCore(String coreName, boolean deleteIndex, boolean deleteDataDir, boolean deleteInstanceDir) {
        CoreAdminRequest.Unload request=new CoreAdminRequest.Unload(true);
        request.setCoreName(coreName);
        request.setDeleteIndex(deleteIndex);
        request.setDeleteDataDir(deleteDataDir);
        request.setDeleteInstanceDir(deleteInstanceDir);


        try {
            request.process(solrClient);
        } catch (SolrServerException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;

    }

    @Override
    public boolean swapCore(String coreOne, String coreTwo) {
        try {
            CoreAdminRequest.swapCore(coreOne,coreTwo,solrClient);
        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public boolean reloadCore(String coreName) {

        try {
            CoreAdminResponse coreAdminResponse=CoreAdminRequest.reloadCore(coreName,solrClient);
//            System.out.println(coreAdminResponse);
        } catch (SolrServerException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
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
