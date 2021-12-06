package com.solr.clientwrapper.rest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = { "base-solr-url=http://localhost:8983/solr" })
public class UserResourceSmokeTest {

	@Autowired
	private UserResource uRes;
	

	@Test
	void contextLoads() {
		assertThat(uRes).isNotNull();
	}
	
	
}
