package com.solr.clientwrapper.rest;


import com.solr.clientwrapper.infrastructure.entity.Employee;
import com.solr.clientwrapper.infrastructure.entity.SolrOrderPojo;
import com.solr.clientwrapper.infrastructure.repository.EmployeeRepository;
import com.solr.clientwrapper.infrastructure.solrbean.SolrCollectionIndex;
import com.solr.clientwrapper.infrastructure.solrbean.SolrSearchResult;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.beans.DocumentObjectBinder;
import org.apache.solr.client.solrj.SolrQuery.SortClause;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeRepository repository;

    
	/*
	 * // Solr CRUD operations for 'employee' collection
	 */    
    @RequestMapping("/save")
    @PostMapping("/createEmps")
    @PostConstruct
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

    @GetMapping("/getAll")
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

	@GetMapping("/getAllEmps/paginated")
	public List<Employee> findDesiredEmployees(
			@RequestParam(defaultValue = "*") String searchTerm, 
			@RequestParam(defaultValue = "0") int page, 
			@RequestParam(defaultValue = "3") int pagesize) {
		return repository.findByCustomerQuery(searchTerm, PageRequest.of(page, pagesize)).getContent();
	}
    
	
	/*
	 * /////// ############## Solr Search Operation ################ /////////
	 */    
    @Autowired
    private SolrClient solrClient;
    private final static String SOLR_DATA_NAME_DEFAULT = "techproducts";
    private final static String SOLR_DATA_NAME = "employee";

    
    @GetMapping("/mysearch")
    public SolrDocumentList searchSpecific1() {

    	String searchTerm = "Karthik";
    	String fieldToSearchIn = "name";
    	String defaultSearchField = "name";
    	String sortStrategy = "id asc";
    	int startIndex = 0;
    	int numberOfRows = 10;
    	String fieldsToDisplay = "id,name,address";
    	String responseFormat = "json";
    	
        SolrQuery query = new SolrQuery();
        query.set("q", fieldToSearchIn+":"+searchTerm);		// search term
        query.set("df", defaultSearchField);				// default search field
        query.set("sort", sortStrategy);					// sorting strategy
        query.set("start", startIndex);						// start index
        query.set("rows", numberOfRows);					// number of rows
        query.set("fl", fieldsToDisplay);					// fields to display
		query.set("wt", responseFormat);					// response format

        query.setFacet(true);
        query.addFacetField("id");
        query.setFacetMinCount(1);
        query.setFacetLimit(10);

        //query.

        try {
            QueryResponse response = solrClient.query(SOLR_DATA_NAME, query);
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
                            facetInstance.getAsFilterQuery() + "]");
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
            QueryResponse response = solrClient.query(SOLR_DATA_NAME, query);
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

    
    @GetMapping("/getEmployeeByAddressCustom/{address1}/{address2}")
    public SolrDocumentList getEmployeeByAddressCustom(@PathVariable String address1, @PathVariable String address2) {

    	String [] inpAddress = {address1, address2};
    	
    	System.out.println("add @@@@@@@@@@@@@@@ : "+inpAddress[1]);
    	
        SolrQuery query = new SolrQuery();
        query.set("q","address:"+inpAddress);
        query.set("fl","id,name,address");

        query.setFacet(true);
        query.addFacetField("id");
        query.setFacetMinCount(1);
        query.setFacetLimit(10);

        //query.

        try {
            QueryResponse response = solrClient.query(SOLR_DATA_NAME, query);
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
                            facetInstance.getAsFilterQuery() + "]");
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
