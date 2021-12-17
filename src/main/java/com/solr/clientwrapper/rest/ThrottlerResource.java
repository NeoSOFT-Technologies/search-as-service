package com.solr.clientwrapper.rest;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpServerErrorException.InternalServerError;

import com.solr.clientwrapper.domain.dto.throttler.ThrottlerRateLimitResponseDTO;
import com.solr.clientwrapper.domain.service.throttler.ThrottlerService;
import com.solr.clientwrapper.usecase.throttler.LimitRateThrottler;

import java.net.ConnectException;
import java.time.LocalTime;

@RestController
@RequestMapping("/throttle")
public class ThrottlerResource {
	/*
	 * This Controller is created for implementing & testing
	 * throttler features:
	 * 	1. Rate Limiting
	 */
	private static final Logger logger = LoggerFactory.getLogger(ThrottlerResource.class);
    @Value("${base-solr-url}")
	String baseSolrUrl;

    private static final String DEFAULT_THROTTLE_SERVICE = "defaultRateLimitThrottler";
   
	@Bean
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }
    @Autowired
    private RestTemplate restTemplate;
	@Autowired
	LimitRateThrottler limitRateThrottler;

    @GetMapping("/health")
    @RateLimiter(name=DEFAULT_THROTTLE_SERVICE, fallbackMethod = "rateLimiterFallback")
    public ResponseEntity<String> checkHealth() {
        String response = restTemplate.getForObject("http://localhost:8080/management/actuator/health", String.class);
        logger.info("{} Health Call processing finished = {}", LocalTime.now(), Thread.currentThread().getName());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/test")
    @RateLimiter(name=DEFAULT_THROTTLE_SERVICE, fallbackMethod = "rateLimiterFallback")
    public ResponseEntity<String> demoRateLimiterThrottler() {    	
    	String response = null;
    	try {
        	response = restTemplate.getForObject(
            		"http://localhost:8081/test/throttle", String.class);
        } catch(ResourceAccessException | InternalServerError e) {
        	logger.error("Probably target server is not up!", e);
        	return new ResponseEntity<>("Target Server is down", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        logger.info("{} | Call processing finished = {}", 
        		LocalTime.now(), 
        		Thread.currentThread().getName());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    @GetMapping("/collections")
    @RateLimiter(name=DEFAULT_THROTTLE_SERVICE, fallbackMethod = "solrCollectionsRateLimiter")
    public ResponseEntity<String> throttleCollectionResource() {
        String response = restTemplate.getForObject(
        		"http://localhost:8080/searchservice/table/collections", String.class);
        logger.info("{} | REST Call processing finished = {}", 
        		LocalTime.now(), 
        		Thread.currentThread().getName());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    public ResponseEntity<String> rateLimiterFallback(Exception e){
    	logger.error("Rate Limiter fallback executed", e);
        return ResponseEntity
        		.status(HttpStatus.TOO_MANY_REQUESTS)
        		.body("order service does not permit further calls");
    }
    
    public ResponseEntity<ThrottlerRateLimitResponseDTO> solrCollectionsRateLimiter(
    				RequestNotPermitted exception) {
        logger.info("Max request limit is being applied");

        // prepare Rate Limiting Response DTO
        ThrottlerRateLimitResponseDTO rateLimitResponseDTO = limitRateThrottler.dataInjectionRateLimiter();
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Retry-after:", rateLimitResponseDTO.getRequestTimeoutDuration()); // retry the request after given timeoutDuration
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .headers(responseHeaders) // attach retry-info header
                .body(rateLimitResponseDTO);
    }
}