package com.solr.clientwrapper.solrsearch;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.SortClause;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.beans.DocumentObjectBinder;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.solr.clientwrapper.domain.service.OrderPojoSolrService;
import com.solr.clientwrapper.infrastructure.entity.SolrOrderPojo;
import com.solr.clientwrapper.infrastructure.solrbean.SolrCollectionIndex;
import com.solr.clientwrapper.infrastructure.solrbean.SolrSearchResult;

public class SolrSearchOperationTest {
	/*
	 * Sample inputs for testing
	 */
	private static String SEARCH_TERM_1;
	private static int PAGE_1;
	private static int PAGE_SIZE_1;
	
	private static Long ORDER_ID;
	private static String ORDER_NAME;
	private static String ORDER_DESC;
	private static String PRODUCT_NAME;
	private static String CUSTOMER_NAME;
	private static String CUSTOMER_MOBILE;
	private static List<SolrOrderPojo> TEST_PRODUCTS;
	
	// Solr Search query sample inputs
    private final static String solrDataNameDefault = "techproducts";
    private final static String solrDataName = "techproducts";
	private static String COLLECTION;
	private static String QUERY_FIELD;
	private static String SEARCH_TERM;
	private static String START_RECORD;
	private static String PAGE_SIZE;
	private static String TAG;
	private static String ORDER;
	
	private SolrOrderPojo solrOrderPojo;
	private OrderPojoSolrService orderPojoSolrService;
	
	@Autowired
	SolrSearchResult solrSearchResult;
	@Autowired
	SolrCollectionIndex solrCollectionIndex;
    @Autowired
    private SolrClient solrClient;
	
	@BeforeEach
    public void init() {

		SEARCH_TERM_1 = "*";
		PAGE_1 = 0;
		PAGE_SIZE_1 = 1;
		
	    ORDER_ID = 1L;
	    ORDER_NAME = "Lambo";
	    ORDER_DESC = "Elite Car";
	    PRODUCT_NAME = "Avandator";
	    CUSTOMER_NAME = "SKD";
	    CUSTOMER_MOBILE = "102030";
		
		solrOrderPojo = new SolrOrderPojo();
		solrOrderPojo.setOrderid(ORDER_ID);
		solrOrderPojo.setCustomerName(CUSTOMER_NAME);
		solrOrderPojo.setCustomerMobile(CUSTOMER_MOBILE);
		solrOrderPojo.setOrderName(ORDER_NAME);
		solrOrderPojo.setOrderDescription(ORDER_DESC);
		solrOrderPojo.setProductName(PRODUCT_NAME);
		
		TEST_PRODUCTS = new ArrayList<>(); 
		TEST_PRODUCTS.add(solrOrderPojo);
		
		// Solr Search query sample data
		COLLECTION = solrDataName;
		QUERY_FIELD = "name";
		SEARCH_TERM = "*";
		START_RECORD = "0";
		PAGE_SIZE = "5";
		TAG = "id";
		ORDER = "asc";
		
		solrSearchResult = new SolrSearchResult();
//		orderPojoSolrService = new OrderPojoSolrService(solrOrderPersistencePort);
    }
	
	@Test
	void contextLoads() {
		assertThat(orderPojoSolrService).isNull();	
	}
	
	@Test
	void searchRecordsInGivenCollectionTest() throws SolrServerException, IOException {
		SolrClient client = new HttpSolrClient.Builder("http://localhost:8983/solr/"+COLLECTION).build();
		SolrQuery query = new SolrQuery();
		query.set("q", QUERY_FIELD + ":" + SEARCH_TERM);
		query.set("start", START_RECORD);
		query.set("rows", PAGE_SIZE);
		SortClause sortClause = new SortClause(TAG, ORDER);
		query.setSort(sortClause);

		QueryResponse response = client.query(query);
		SolrDocumentList numdocs = response.getResults();
		DocumentObjectBinder binder = new DocumentObjectBinder();
		List<SolrCollectionIndex> docs = binder.getBeans(SolrCollectionIndex.class, numdocs);
		response.getDebugMap();
		long numDocs = numdocs.getNumFound();
		
		// System.out.println("####### @@@@ ######### : "+numDocs);
		
		solrSearchResult.setNumDocs(numDocs);
		solrSearchResult.setSolrCollectionIndex(docs);
		
		assertNotNull(solrSearchResult);
		 
	}
	
	@Test
	void searchRecordsInGivenCollectionNullWhenProvidedWrongCollectionForTheIndexedCollectionTest() throws SolrServerException, IOException {
		
		COLLECTION = "employee1";
		
		SolrClient client = new HttpSolrClient.Builder("http://localhost:8983/solr/"+COLLECTION).build();
		SolrQuery query = new SolrQuery();
		query.set("q", QUERY_FIELD + ":" + SEARCH_TERM);
		query.set("start", START_RECORD);
		query.set("rows", PAGE_SIZE);
		SortClause sortClause = new SortClause(TAG, ORDER);
		query.setSort(sortClause);

	    Exception exception = assertThrows(HttpSolrClient.RemoteSolrException.class, 
	    		() -> {
	    			QueryResponse response = client.query(query);
	    			SolrDocumentList numdocs = response.getResults();
	    		});
		 
	}
}
