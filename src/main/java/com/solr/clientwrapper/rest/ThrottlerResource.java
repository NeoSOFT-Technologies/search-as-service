package com.solr.clientwrapper.rest;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.retry.annotation.Retry;
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
import org.springframework.web.client.RestTemplate;

import com.solr.clientwrapper.domain.dto.throttler.ThrottlerRateLimitResponseDTO;
import com.solr.clientwrapper.domain.service.throttler.ThrottlerService;

import java.time.LocalTime;

@RestController
@RequestMapping("/throttle")
public class ThrottlerResource {
	private static final Logger logger = LoggerFactory.getLogger(ThrottlerResource.class);
	
    @Value("${base-solr-url}")
	String baseSolrUrl;
    @Value("${resilience4j.ratelimiter.instances.testThrottleService.limitForPeriod}")
    int maxRequestAllowedForCurrentWindow;
    @Value("${resilience4j.ratelimiter.instances.testThrottleService.limitRefreshPeriod}")
    String currentRefreshWindow;
    
    private static final String TEST_THROTTLE_SERVICE = "testThrottleService";
    private static final String SOLR_DATA_INJECTION_THROTTLE_SERVICE = "solrDataInjectionThrottleService";    
    
	@Bean
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }
    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/health")
    @RateLimiter(name=TEST_THROTTLE_SERVICE, fallbackMethod = "rateLimiter")
    public ResponseEntity<String> checkHealth() {
        String response = restTemplate.getForObject("http://localhost:8080/management/actuator/health", String.class);
        logger.info("{} Health Call processing finished = {}", LocalTime.now(), Thread.currentThread().getName());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/test")
    @RateLimiter(name=TEST_THROTTLE_SERVICE, fallbackMethod = "rateLimiterFallback")
    public ResponseEntity<String> createThrottler() {    	
        String response = restTemplate.getForObject(
        		"http://localhost:8081/test/throttle", String.class);
        logger.info("{} | Call processing finished = {}", 
        		LocalTime.now(), 
        		Thread.currentThread().getName());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    @GetMapping("/collections")
    @RateLimiter(name=TEST_THROTTLE_SERVICE, fallbackMethod = "rateLimiterFallback")
    public ResponseEntity<String> throttleCollectionResource() {
        String response = restTemplate.getForObject(
        		"http://localhost:8080/searchservice/table/collections", String.class);
        logger.info("{} | REST Call processing finished = {}", 
        		LocalTime.now(), 
        		Thread.currentThread().getName());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    public ResponseEntity<ThrottlerRateLimitResponseDTO> rateLimiterFallback(RequestNotPermitted exception) {
        logger.error("Max request limit is applied, no further calls are accepted", exception);

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
