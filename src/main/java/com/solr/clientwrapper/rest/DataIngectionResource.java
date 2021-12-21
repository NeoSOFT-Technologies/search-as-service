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

import com.solr.clientwrapper.domain.dto.throttler.ThrottlerMaxRequestSizeResponseDTO;
import com.solr.clientwrapper.domain.dto.throttler.ThrottlerRateLimitResponseDTO;
import com.solr.clientwrapper.domain.service.DataIngectionService;
import com.solr.clientwrapper.usecase.throttler.LimitRateThrottler;
import com.solr.clientwrapper.usecase.throttler.LimitRequestSizeThrottler;

import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;

@RestController
@RequestMapping("/injection")
public class DataIngectionResource {
	private final Logger logger = LoggerFactory.getLogger(DataIngectionResource.class);
    @Value("${base-solr-url}")
	String baseSolrUrl;
    
    private static final String DEFAULT_THROTTLE_SERVICE = "defaultRateLimitThrottler";
    private static final String SOLR_DATA_INJECTION_THROTTLE_SERVICE = "solrDataInjectionRateLimitThrottler";
	
	@Autowired
	DataIngectionService dataIngectionService;
	@Autowired
	LimitRateThrottler limitRateThrottler;
	@Autowired
	LimitRequestSizeThrottler limitRequestSizeThrottler;

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

	@RateLimiter(name = SOLR_DATA_INJECTION_THROTTLE_SERVICE, fallbackMethod = "dataInjectionRateLimiter")
	@PostMapping(path = "/batch")
	public ResponseEntity<?> parseBatch(@RequestBody String data) {
		logger.debug("json array injection : {}; Class: {}", data, data.getClass());
		
		// Apply MRS Limiter onto the incoming data
		ThrottlerMaxRequestSizeResponseDTO throttlerMaxRequestSizeResponseDTO = 
				limitRequestSizeThrottler.dataInjectionRequestSizeLimiter(data);
		
		if(data.equals("data=just-testing")) {
			logger.info("Unexpected data detected in the request body!!");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body("Unexpected data detected in the request body");
		}
		logger.info("Valid data found. Injection starts..");
		String objectBatch = dataIngectionService.parseSolrSchemaBtch(data);
		logger.debug("controller :- {}", objectBatch);
		
		if(throttlerMaxRequestSizeResponseDTO.getStatusCode() == 405) {
			return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(throttlerMaxRequestSizeResponseDTO);
		}
		else if (!objectBatch.isEmpty()) {
			// File EXISTS
			// Set API response data in throttlerResponseDTO
			throttlerMaxRequestSizeResponseDTO.setApiResponseData(objectBatch);
			return ResponseEntity.status(HttpStatus.OK).body(throttlerMaxRequestSizeResponseDTO);
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Something is wrong");
		}

	}

    public ResponseEntity<ThrottlerRateLimitResponseDTO> dataInjectionRateLimiter(
    		String data, 
    		RequestNotPermitted exception) {
        logger.info("Max request limit is applied, no further calls are accepted");
        
        // prepare Rate Limiting Response DTO
        ThrottlerRateLimitResponseDTO rateLimitResponseDTO = limitRateThrottler.dataInjectionRateLimiter();
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Retry-after:", rateLimitResponseDTO.getRequestTimeoutDuration()); // retry the request after given timeoutDuration
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .headers(responseHeaders) // attach retry-info header
                .body(rateLimitResponseDTO);
    }
}
