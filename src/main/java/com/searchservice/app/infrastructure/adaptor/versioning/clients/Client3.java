package com.searchservice.app.infrastructure.adaptor.versioning.clients;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.searchservice.app.domain.dto.MyApiResponse3;
import com.searchservice.app.rest.VersioningTestResource;

public class Client3 extends BaseClient {
	private final Logger log = LoggerFactory.getLogger(VersioningTestResource.class);
	
    public Client3() {
        super(3);
    }

    public void doWork() throws IOException {
    	log.info("Client 3 executing........");
        MyApiResponse3 resp = super.getAs(
        		"http://localhost:8080/test-versioning/3", 
        		MyApiResponse3.class);
        log.info("Version {} ApiResponse: {}", 3, resp);
    }
}
