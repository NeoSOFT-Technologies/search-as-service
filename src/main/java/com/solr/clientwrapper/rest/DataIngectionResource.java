package com.solr.clientwrapper.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.solr.clientwrapper.domain.dto.throttler.ThrottlerRateLimitResponseDTO;
import com.solr.clientwrapper.domain.service.DataIngectionService;

import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;

@RestController
@RequestMapping("/ingection")
public class DataIngectionResource {

	private final Logger logger = LoggerFactory.getLogger(DataIngectionResource.class);
    @Value("${base-solr-url}")
	String baseSolrUrl;
    @Value("${resilience4j.ratelimiter.instances.testThrottleService.limitForPeriod}")
    int maxRequestAllowedForCurrentWindow;
    @Value("${resilience4j.ratelimiter.instances.testThrottleService.limitRefreshPeriod}")
    String currentRefreshWindow;
    
    private static final String TEST_THROTTLE_SERVICE = "testThrottleService";
    private static final String SOLR_DATA_INJECTION_THROTTLE_SERVICE = "solrDataInjectionThrottleService";

	
	@Autowired
	DataIngectionService dataIngectionService;

	@RateLimiter(name = SOLR_DATA_INJECTION_THROTTLE_SERVICE, fallbackMethod = "dataInjectionRateLimiter")
	@PostMapping(path = "/data")
	public ResponseEntity<String> parseArray(@RequestBody String data) {
		logger.info("json array ingection ");
		String objectdata = dataIngectionService.parseSolrSchemaArray(data);
		logger.debug("controller :-{}", objectdata.length());
		if (!objectdata.isEmpty()) {
			// File is EXISTS
			return ResponseEntity.status(HttpStatus.OK).body(objectdata);
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Something is Wrong");
		}

	}

	@RateLimiter(name = TEST_THROTTLE_SERVICE, fallbackMethod = "dataInjectionRateLimiter")
	@PostMapping(path = "/batch")
	public ResponseEntity<String> parseBatch(@RequestBody String data) {
		logger.debug("json array injection : {}; Class: {}", data, data.getClass());
		if(data.equals("data=just-testing")) {
			logger.info("Unexpected data detected in the request body!!");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body("Unexpected data detected in the request body");
		}
		logger.info("Valid data found. Injection starts..");
		String objectBatch = dataIngectionService.parseSolrSchemaBtch(data);
		logger.debug("controller :- {}", objectBatch);
		if (!objectBatch.isEmpty()) {
			// File is EXISTS
			return ResponseEntity.status(HttpStatus.OK).body(objectBatch);
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Something is Wrong");
		}

	}

    public ResponseEntity<ThrottlerRateLimitResponseDTO> dataInjectionRateLimiter(
    		String data, 
    		RequestNotPermitted exception) {
        logger.info("Max request limit is applied, no further calls are accepted");

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Retry-after:", "1s"); // retry the request after one second

        // prepare Rate Limiting Response DTO
        ThrottlerRateLimitResponseDTO rateLimitResponseDTO = new ThrottlerRateLimitResponseDTO();
        rateLimitResponseDTO.setResponseMsg(
        		"Too many requests made! "
        		+ "No further calls are accepted right now");
        rateLimitResponseDTO.setStatusCode(429);
        rateLimitResponseDTO.setMaxRequestsAllowed(maxRequestAllowedForCurrentWindow);
        rateLimitResponseDTO.setCurrentRefreshWindow(currentRefreshWindow);
        
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .headers(responseHeaders) // attach retry-info header
                .body(rateLimitResponseDTO);
    }
}
