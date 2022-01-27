package com.searchservice.app.rest;

import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestingController {

//    @GetMapping("/addDocument")
//    public SolrGetCapacityPlanDTO addDocument() throws SolrServerException, IOException {
//
//        String solrurl="http://localhost:8983/solr/S3Collection";
//        SolrClient solrclient = new HttpSolrClient.Builder(solrurl).build();
//
//        SolrInputDocument document = new SolrInputDocument();
//        document.addField("item_id",102);
//        document.addField("item_name","Spring not in action");
//        document.addField("item_category","Weird Books");
//
//        solrclient.add(document);
//        solrclient.commit();
//
//        return null;
//
//    }
}
