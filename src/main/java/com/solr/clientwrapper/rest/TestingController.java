package com.solr.clientwrapper.rest;


import com.solr.clientwrapper.config.CapacityPlanProperties;
import com.solr.clientwrapper.domain.dto.solr.collection.SolrGetCapacityPlanDTO;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.request.CollectionAdminRequest;
import org.apache.solr.client.solrj.request.CoreAdminRequest;
import org.apache.solr.client.solrj.response.CollectionAdminResponse;
import org.apache.solr.client.solrj.response.CoreAdminResponse;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

@RestController
public class TestingController {

    @Autowired
    private SolrClient solrClient;
    private final static String solrDataName = "employee";

    @GetMapping("/testCollection/{name}")
    public String testCollection(@PathVariable String name) throws SolrServerException, IOException, URISyntaxException, ParserConfigurationException, InterruptedException, TransformerException, org.xml.sax.SAXException {

//        CollectionAdminRequest.Reload request=CollectionAdminRequest.reloadCollection(name);
//        CollectionAdminResponse response = request.process(solrClient);

        CollectionAdminRequest.SplitShard request=CollectionAdminRequest.splitShard(name);
        CollectionAdminResponse response = request.process(solrClient);

        System.out.println(response);

        return response.toString();
    }

    @Autowired
    CapacityPlanProperties capacityPlanProperties;

    @GetMapping("/getCapacityPlans")
    public SolrGetCapacityPlanDTO getCapacityPlans() throws SolrServerException, IOException, URISyntaxException, ParserConfigurationException, InterruptedException, TransformerException, org.xml.sax.SAXException {

        List<CapacityPlanProperties.Plan> capacityPlans = capacityPlanProperties.getPlans();

        SolrGetCapacityPlanDTO solrGetCapacityPlanDTO=new SolrGetCapacityPlanDTO(capacityPlans);
        return solrGetCapacityPlanDTO;

    }

    @GetMapping("/createCollection/{name}")
    public String createCollection(@PathVariable String name) throws SolrServerException, IOException, URISyntaxException, ParserConfigurationException, InterruptedException, TransformerException, org.xml.sax.SAXException {

        CollectionAdminRequest.Create request = CollectionAdminRequest.createCollection(name, 1, 1);
        CollectionAdminResponse response = request.process(solrClient);

        System.out.println(response);

        return response.toString();
    }


    @GetMapping("/deleteCollection/{name}")
    public String deleteCollection(@PathVariable String name) throws SolrServerException, IOException, URISyntaxException, ParserConfigurationException, InterruptedException, TransformerException, org.xml.sax.SAXException {

        CollectionAdminRequest.Delete request = CollectionAdminRequest.deleteCollection(name);
        CollectionAdminResponse response = request.process(solrClient);

        System.out.println(response);

        return response.toString();
    }

    @GetMapping("/renameCollection/{name}")
    public String renameCollection(@PathVariable String name) throws SolrServerException, IOException, URISyntaxException, ParserConfigurationException, InterruptedException, TransformerException, org.xml.sax.SAXException {

        CollectionAdminRequest.Rename request = CollectionAdminRequest.renameCollection(name,name+"renamed");
        CollectionAdminResponse response = request.process(solrClient);

        System.out.println(response);

        return response.toString();
    }

    @GetMapping("/listCollections")
    public String listCollections() throws SolrServerException, IOException, URISyntaxException, ParserConfigurationException, InterruptedException, TransformerException, org.xml.sax.SAXException {

        CollectionAdminRequest.List request = new CollectionAdminRequest.List();
        CollectionAdminResponse response = request.process(solrClient);

        List<String> collection= (List<String>) response.getResponse().get("collections");

        System.out.println(collection.get(0));

//        System.out.println(response.getResponse().get("collections"));

        return response.toString();
    }


    @GetMapping("/doesCollectionsExists/{name}")
    public Boolean doesCollectionsExists(@PathVariable String name) throws SolrServerException, IOException, URISyntaxException, ParserConfigurationException, InterruptedException, TransformerException, org.xml.sax.SAXException {

        CollectionAdminRequest.List request = new CollectionAdminRequest.List();
        CollectionAdminResponse response = request.process(solrClient);
        List<String> existingCollections = (List<String>) response.getResponse().get("collections");

        return existingCollections != null && existingCollections.contains(name);


    }



    @GetMapping("/coreStatus/{name}")
    public String coreStatus(@PathVariable String name) throws SolrServerException, IOException, URISyntaxException, ParserConfigurationException, InterruptedException, TransformerException, org.xml.sax.SAXException {

        HttpSolrClient solrClient = new HttpSolrClient.Builder("http://localhost:8983/solr").build();
        CoreAdminResponse coreAdminResponse=CoreAdminRequest.getStatus(name,solrClient);

        return coreAdminResponse.toString();
    }


    @GetMapping("/reloadCore/{name}")
    public boolean reloadCore(@PathVariable String name) throws SolrServerException, IOException, URISyntaxException, ParserConfigurationException, InterruptedException, TransformerException, org.xml.sax.SAXException {

        HttpSolrClient solrClient = new HttpSolrClient.Builder("http://localhost:8983/solr").build();

        CoreAdminResponse coreAdminResponse=CoreAdminRequest.reloadCore(name,solrClient);

        System.out.println(coreAdminResponse);

        return true;
    }

    @GetMapping("/mergeIndexes/{name}")
    public CoreAdminResponse mergeIndexes(@PathVariable String name) throws SolrServerException, IOException, URISyntaxException, ParserConfigurationException, InterruptedException, TransformerException, org.xml.sax.SAXException {

        HttpSolrClient solrClient = new HttpSolrClient.Builder("http://localhost:8983/solr").build();
        CoreAdminResponse coreAdminResponse=CoreAdminRequest.getStatus(name,solrClient);

        CoreAdminRequest.getCoreStatus(name,solrClient).getDataDirectory();

//        CoreAdminRequest.mergeIndexes(name,)

        return coreAdminResponse;
    }

    @GetMapping("/deleteCore/{name}")
    public boolean createCore(@PathVariable String name) throws SolrServerException, IOException, URISyntaxException, ParserConfigurationException, InterruptedException, TransformerException, org.xml.sax.SAXException {

        HttpSolrClient solrClient = new HttpSolrClient.Builder("http://localhost:8983/solr").build();
        CoreAdminRequest.Unload request1=new CoreAdminRequest.Unload(true);
        request1.setCoreName(name);
        request1.isDeleteDataDir();

        request1.process(solrClient);

        return true;
    }

    @GetMapping("/renameCore/{name}")
    public boolean renameCore(@PathVariable String name) throws SolrServerException, IOException, URISyntaxException, ParserConfigurationException, InterruptedException, TransformerException, org.xml.sax.SAXException {

        HttpSolrClient solrClient = new HttpSolrClient.Builder("http://localhost:8983/solr").build();
        //CoreAdminRequest.Create request = new CoreAdminRequest.Create();

        CoreAdminResponse coreAdminResponse=CoreAdminRequest.renameCore(name,name+"New",solrClient);

        System.out.println(coreAdminResponse);

        return true;
    }



    @GetMapping("/getEmployeeByNameCustom/{name}")
    public SolrDocumentList getEmployeeByNameCustom(@PathVariable String name) {

        SolrQuery query = new SolrQuery();
        query.set("q","name:"+name);
        query.set("fl","id,address");

        query.setFacet(true);
        query.addFacetField("id");
        query.setFacetMinCount(1);
        query.setFacetLimit(10);

        //query.

        try {
            QueryResponse response = solrClient.query(solrDataName, query);
            //System.out.print(response);


            SolrDocumentList solrDocumentList = response.getResults();
            //System.out.print(solrDocumentList);


            List<FacetField> facetFields =response.getFacetFields();
            for (int i = 0; i < facetFields.size(); i++) {
                FacetField facetField = facetFields.get(i);
                List<FacetField.Count> facetInfo = facetField.getValues();

                for (FacetField.Count facetInstance : facetInfo) {
                    System.out.println(facetInstance.getName() + " : " +
                            facetInstance.getCount() + " [drilldown qry:" +
                            facetInstance.getAsFilterQuery());
                }
            }

            return solrDocumentList;
        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


}
