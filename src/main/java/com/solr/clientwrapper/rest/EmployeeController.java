package com.solr.clientwrapper.rest;


import com.solr.clientwrapper.infrastructure.entity.Employee;
import com.solr.clientwrapper.infrastructure.repository.EmployeeRepository;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.request.CollectionAdminRequest;
import org.apache.solr.client.solrj.request.CoreAdminRequest;
import org.apache.solr.client.solrj.response.CoreAdminResponse;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

@RestController
public class EmployeeController {

    @Autowired
    private EmployeeRepository repository;

    @RequestMapping("/save")
    public void addEmployees() {
        List<Employee> employees = new ArrayList<>();
        employees.add(new Employee(374, "Karthik", new String[] { "Mumbai", "BOM" }));
        employees.add(new Employee(909, "Kirti", new String[] { "Pune", "PQR" }));
        employees.add(new Employee(322, "Piyush", new String[] { "Nagpur", "NGP" }));
        employees.add(new Employee(373, "Basant", new String[] { "Bangalore", "BTM" }));
        employees.add(new Employee(908, "Santosh", new String[] { "Hyderbad", "XYZ" }));
        employees.add(new Employee(321, "Sagar", new String[] { "Pune", "PQR" }));
        repository.saveAll(employees);
    }

    @GetMapping("/getALL")
    public Iterable<Employee> getEmployees() {
        return repository.findAll();
    }

    @GetMapping("/getEmployeeById/{id}")
    public List<Employee> getEmployeeById(@PathVariable int id) {
        return repository.findById(id);
    }

    @GetMapping("/getEmployeeByName/{name}")
    public Employee getEmployeeByName(@PathVariable String name) {
        return repository.findByName(name);
    }

    @GetMapping("/getEmployeeByNameLike/{name}")
    public List<Employee> getEmployeeByNameLike(@PathVariable String name) {
        return repository.findByNameLike(name);
    }

    @GetMapping("/getEmployeeByAddress/{address}")
    public Employee getEmployeeByAddress(@PathVariable String address) {
        return repository.findByAddress(address);
    }

    @Autowired
    private SolrClient solrClient;
    private final static String solrDataName = "employee";

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

    @GetMapping("/createCollection/{name}")
    public boolean createCollection(@PathVariable String name) throws SolrServerException, IOException, URISyntaxException, ParserConfigurationException, InterruptedException, TransformerException, org.xml.sax.SAXException {

        HttpSolrClient solrClient = new HttpSolrClient.Builder("http://localhost:8983/solr").build();
        CollectionAdminRequest request = CollectionAdminRequest.createCollection("karthikCollection", "config",
                1, 1);
        request.process(solrClient);

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
