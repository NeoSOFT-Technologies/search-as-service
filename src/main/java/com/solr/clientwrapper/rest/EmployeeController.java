package com.solr.clientwrapper.rest;


import com.solr.clientwrapper.infrastructure.entity.Employee;
import com.solr.clientwrapper.infrastructure.repository.EmployeeRepository;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
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
