package com.solr.clientwrapper.rest;


import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestingController {


//    @GetMapping("/addDocument")
//    public SolrGetCapacityPlanDTO addDocument() throws SolrServerException, IOException, URISyntaxException, ParserConfigurationException, InterruptedException, TransformerException, org.xml.sax.SAXException {
//
//        String SOLR_URL="http://localhost:8983/solr/S3Collection";
//        SolrClient solrClient = new HttpSolrClient.Builder(SOLR_URL).build();
//
//        SolrInputDocument document = new SolrInputDocument();
//        document.addField("item_id",102);
//        document.addField("item_name","Spring not in action");
//        document.addField("item_category","Weird Books");
//
//        solrClient.add(document);
//        solrClient.commit();
//
//        return null;
//
//    }

}
