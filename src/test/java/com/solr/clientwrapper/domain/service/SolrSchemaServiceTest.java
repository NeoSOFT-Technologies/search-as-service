package com.solr.clientwrapper.domain.service;

import static org.junit.jupiter.api.Assertions.*;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.solr.clientwrapper.domain.dto.solr.SolrFieldDTO;
import com.solr.clientwrapper.domain.dto.solr.SolrSchemaDTO;
import com.solr.clientwrapper.domain.port.api.SolrSchemaServicePort;
import com.solr.clientwrapper.infrastructure.Enum.SolrFieldType;
import com.solr.clientwrapper.infrastructure.adaptor.SolrSchemaAPIAdapter;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
class SolrSchemaServiceTest {
	private final Logger log = LoggerFactory.getLogger(SolrSchemaServiceTest.class);
	
	@Value("gettingstarted1")
	private static String TEST1;
	private static String DEFAULT_SOLR_COLLECTION = "gettingstarted1";
	@Value("${solr-client-base-url-static}")
	private static String TEST2;
	private static String URL_STRING = "http://localhost:8985/solr/";
	@Value("${solr-client-base-url-cloud}")
	private static String TEST3;
	private static String URL_STRING_SOLR_CLOUD = "http://localhost:8983/solr/";
	
	
	//dummy for solr client
	@SuppressWarnings("deprecation")
	private CloudSolrClient cloudSolrClient = new CloudSolrClient.Builder()
			.withSolrUrl(URL_STRING_SOLR_CLOUD).build();
	private SolrClient solrClient = new HttpSolrClient.Builder(URL_STRING+DEFAULT_SOLR_COLLECTION).build();
	
	/*
	 * Sample inputs for testing
	 */	
	private static String TABLE_NAME = DEFAULT_SOLR_COLLECTION;
	private static String SCHEMA_NAME = "default-config";
	private static SolrFieldDTO solrFieldDTO = new SolrFieldDTO(
			"testField2",
			SolrFieldType.fromObject("_nest_path_"),
			"mydefault", 
			true, 
			false, 
			false, 
			true, 
			true
			);
	private static SolrFieldDTO[] ATTRIBUTES = {solrFieldDTO};
	private SolrSchemaDTO solrSchemaDTO = new SolrSchemaDTO(
			TABLE_NAME, 
			SCHEMA_NAME, 
			ATTRIBUTES);
	
	@MockBean
	private SolrSchemaServicePort solrSchemaServicePort;
	@MockBean
	private SolrSchemaAPIAdapter solrSchemaAPIAdapter;
	@InjectMocks
	private SolrSchemaService solrSchemaService;

	@BeforeEach
	void setUp() throws Exception {
		Mockito.when(solrSchemaAPIAdapter.getCloudSolrClient(Mockito.any(), Mockito.any()))
			.thenReturn(cloudSolrClient);
	}

	@AfterEach
	void tearDown() throws Exception {
		solrClient.close();
		cloudSolrClient.close();
	}
	
	@Disabled
	@Test
	void testGet() {
		Mockito.when(solrSchemaServicePort.get(Mockito.any(), Mockito.any()))
				.thenReturn(solrSchemaDTO);
		SolrSchemaDTO getSchemaResponse = solrSchemaService.get(DEFAULT_SOLR_COLLECTION, SCHEMA_NAME);
		assertNotNull(getSchemaResponse);
		log.debug("Get Business Logic tested successfully");
	}

	@Disabled
	@Test
	void testUpdate() {
		Mockito.when(solrSchemaServicePort.update(Mockito.any(), Mockito.any(), Mockito.any()))
				.thenReturn(solrSchemaDTO);
		SolrSchemaDTO updateSchemaResponse = solrSchemaService.update(DEFAULT_SOLR_COLLECTION, SCHEMA_NAME, solrSchemaDTO);
		assertNotNull(updateSchemaResponse);
		log.debug("Update Business Logic tested successfully");
	}

	@Disabled
	@Test
	void testCreate() {
		Mockito.when(solrSchemaServicePort.create(Mockito.any(), Mockito.any(), Mockito.any()))
			.thenReturn(solrSchemaDTO);
		SolrSchemaDTO createSchemaResponse = solrSchemaService.create(DEFAULT_SOLR_COLLECTION, SCHEMA_NAME, solrSchemaDTO);
		assertNotNull(createSchemaResponse);
		log.debug("Create Business Logic tested successfully");
	}

	@Disabled
	@Test
	void testDelete() {
		Mockito.doNothing()
				.when(solrSchemaServicePort).delete(Mockito.any(), Mockito.any());
		solrSchemaService.delete(DEFAULT_SOLR_COLLECTION, SCHEMA_NAME);
		log.debug("Delete Business Logic tested successfully");
	}
	
	@Test
	void testValidateSchemaField() {
		boolean validation = solrSchemaService.validateSchemaField(solrFieldDTO);
		assertTrue(validation);
	}

	@Disabled
	@Test
	void testGetSolrClient() {
		Mockito.when(solrSchemaAPIAdapter.getCloudSolrClient(Mockito.any(), Mockito.any()))
				.thenReturn(cloudSolrClient);
		Mockito.when(solrSchemaAPIAdapter.getSolrClient(Mockito.any(), Mockito.any()))
				.thenReturn(solrClient);
		String getSolrClientResponse = solrSchemaService.getSolrClient(TABLE_NAME);
		assertNotNull(getSolrClientResponse);
	}
}
