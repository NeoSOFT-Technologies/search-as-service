package com.solr.clientwrapper.solrsearch;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.solr.clientwrapper.domain.port.api.SolrOrderServicePort;
import com.solr.clientwrapper.domain.port.spi.SolrOrderPersistencePort;
import com.solr.clientwrapper.domain.service.SolrOrderService;
import com.solr.clientwrapper.infrastructure.entity.SolrOrderPojo;
import com.solr.clientwrapper.infrastructure.repository.SolrOrderRepository;
import com.solr.clientwrapper.rest.SolrOrderResource;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
public class SolrSearchOrderTest {
	/*
	 * Sample inputs for testing
	 */
	private static String SEARCH_TERM;
	private static int PAGE;
	private static int PAGE_SIZE;
	
	private static Long ORDER_ID;
	private static String ORDER_NAME;
	private static String ORDER_DESC;
	private static String PRODUCT_NAME;
	private static String CUSTOMER_NAME;
	private static String CUSTOMER_MOBILE;
	private static List<SolrOrderPojo> TEST_PRODUCTS;
	
	
	private SolrOrderPojo solrOrderPojo;
	
	@MockBean
	private SolrOrderRepository solrOrderRepository;
	@MockBean
	private SolrOrderServicePort solrOrderServicePort;
	@MockBean
	private SolrOrderPersistencePort solrOrderPersistencePort;
	
	@InjectMocks
	private SolrOrderService orderPojoSolrService;
//	@InjectMocks
//	private SolrOrderResource solrOrderResource;
//	
	
	@BeforeEach
    public void init() {

		SEARCH_TERM = "*";
		PAGE = 0;
		PAGE_SIZE = 1;
		
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
		
		orderPojoSolrService = new SolrOrderService(solrOrderPersistencePort);
    }
	
	@Test
	void contextLoads() {
		assertThat(solrOrderRepository).isNotNull();	
	}
	
    @Test
    void findOrdersTest() {	
    	// Page<SolrOrderPojo> orderPage = Mockito.mock(Page.class);
		List<SolrOrderPojo> orders = new ArrayList<SolrOrderPojo>();
		
		/*
		 * // convert LIST sample o/p into Page<> final int start =
		 * (int)pageable.getOffset(); final int end = Math.min((start +
		 * pageable.getPageSize()), TEST_PRODUCTS.size()); final Page<User> page = new
		 * PageImpl<>(users.subList(start, end), pageable, users.size());
		 */
		
		Mockito.when(solrOrderServicePort.findByCustomerQuery2(Mockito.any(), Mockito.any()))
		.thenReturn(TEST_PRODUCTS);
		
		orders = orderPojoSolrService.findByCustomerQuery2(SEARCH_TERM, PageRequest.of(PAGE, PAGE_SIZE));
		
		
    	// testing
    	System.out.println("BP 5: "+"########################");
		System.out.println("\tOrders: "+TEST_PRODUCTS.size());
		
		assertNotNull(orders);
    }
	
	
}
