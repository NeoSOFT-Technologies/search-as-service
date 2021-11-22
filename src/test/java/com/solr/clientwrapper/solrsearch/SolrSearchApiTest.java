package com.solr.clientwrapper.solrsearch;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.util.NamedList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.solr.clientwrapper.IntegrationTest;
import com.solr.clientwrapper.infrastructure.entity.Employee;
import com.solr.clientwrapper.infrastructure.entity.SampleEntity;
import com.solr.clientwrapper.infrastructure.repository.EmployeeRepository;


//@ExtendWith(MockitoExtension.class)
//@ExtendWith(SpringExtension.class)
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class SolrSearchApiTest {
    
	private static final Integer DEAFULT_ID = 374;
	private static final String [] DEFAULT_NAME = {"Karthik"};
	private static final String [] DEFAULT_ADDRESS = {"Mumbai", "BOM"};
	
	String searchTerm = "Karthik";
	String fieldToSearchIn = "name";
	String defaultSearchField = "name";
	String sortStrategy = "id asc";
	int startIndex = 0;
	int numberOfRows = 10;
	String fieldsToDisplay = "id,name,address";
	String responseFormat = "json";
	
	private final static String solrDataName = "employee";
	
    @MockBean
    private EmployeeRepository employeeRepository;
    @MockBean
    private SolrClient solrClient;
//    @MockBean
    private SolrQuery query;

    @Autowired
    private MockMvc restSolrMockMvc;
    
    @Autowired
    private EntityManager em;
    
    private Employee employee;
    
    public static Employee createEntity(EntityManager em) {
    	Employee employee = new Employee(DEAFULT_ID, DEFAULT_NAME[0], DEFAULT_ADDRESS);
        return employee;
    }
    
    @BeforeEach
    public void initTest() {
        employee = createEntity(em);
    }
    
	@BeforeEach
    public void init() {

		query = new SolrQuery();
		
		searchTerm = "Karthik";
		fieldToSearchIn = "name";
		defaultSearchField = "name";
		sortStrategy = "id asc";
		startIndex = 0;
		numberOfRows = 10;
		fieldsToDisplay = "id,name,address";
		responseFormat = "json";
		
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

    }
    
	@Test
	void contextLoads() {
		assertThat(employeeRepository).isNotNull();
	}
	
	// Test cases for Solr search APIs
	@Test
	@Transactional
	void performSolrSearchTest() throws Exception {
		
		// testing
		System.out.println("query #########  11111  ######### : "+query.get("q"));
		
//		QueryResponse response = solrClient.query(solrDataName, query);
//		System.out.println("Resp &&&&& ::::: "+response);
		
//		SolrDocumentList solrDocumentList = response.getResults();
//        System.out.println("Doc List &&&&&&& ::::: "+solrDocumentList);
		
		
//		Mockito.when(solrClient
//				.query(solrDataName, query))
//				.thenReturn(null);
//		SolrDocumentList solrDocumentList = solrClient
//											.query(solrDataName, query)
//											.getResults();
//		System.out.println("Doc List &&&&&&& ::::: "+solrDocumentList);
//		
//		assertNotNull(solrDocumentList);
		
		
		
		////////////////// Using MockMvc //////////////
		
		restSolrMockMvc
			.perform(get("/search").accept(MediaType.APPLICATION_PROBLEM_JSON))
//			.andExpect(status().isOk())
			.andExpect(status().isInternalServerError())
			.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE))
//			.andExpect(MockMvcResultMatchers.jsonPath("$[*].id").value(hasItem(employee.getId())))
			.andExpect(MockMvcResultMatchers.jsonPath("$[*]['id']").value(hasItem(employee.getId())))
			.andExpect(MockMvcResultMatchers.jsonPath("$[*].name").value(hasItem(DEFAULT_NAME)))
			.andExpect(MockMvcResultMatchers.jsonPath("$[*].address").value(hasItem(DEFAULT_ADDRESS)));
		
		
		
		
	}
 
}