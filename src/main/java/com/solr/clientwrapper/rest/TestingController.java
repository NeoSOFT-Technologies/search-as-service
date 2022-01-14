package com.solr.clientwrapper.rest;

import java.io.IOException;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RestController;

import com.solr.clientwrapper.domain.dto.solr.collection.SolrGetCapacityPlanDTO;

@RestController
public class TestingController {

    @Autowired
    private SolrClient solrClient;

    @GetMapping("/addDocument")
    public SolrGetCapacityPlanDTO addDocument() throws SolrServerException, IOException {

        String solrurl="http://localhost:8983/solr/S3Collection";
        SolrClient solrclient = new HttpSolrClient.Builder(solrurl).build();

        SolrInputDocument document = new SolrInputDocument();
        document.addField("item_id",102);
        document.addField("item_name","Spring not in action");
        document.addField("item_category","Weird Books");

        solrclient.add(document);
        solrclient.commit();

        return null;

    }
}
