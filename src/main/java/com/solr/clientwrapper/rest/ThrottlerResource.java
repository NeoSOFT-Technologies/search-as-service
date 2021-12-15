package com.solr.clientwrapper.rest;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.solr.clientwrapper.domain.service.throttler.ThrottlerService;

import java.time.LocalTime;

@RestController
public class ThrottlerResource {
    private static final Logger logger = LoggerFactory.getLogger(ThrottlerResource.class);
    private static final String THROTTLE_SERVICE ="throttleService" ;

    @Bean
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private ThrottlerService throttlerService;

    @GetMapping("/throttle")
    @RateLimiter(name=THROTTLE_SERVICE, fallbackMethod = "rateLimiterFallback")
    public ResponseEntity<String> createThrottler() {
        String response = restTemplate.getForObject(
        		"http://localhost:8081/test/throttle", String.class);
        logger.info("{} Call processing finished = {}", 
        		LocalTime.now(), 
        		Thread.currentThread().getName());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    
    @GetMapping("/health")
    @RateLimiter(name=THROTTLE_SERVICE, fallbackMethod = "rateLimiterFallback")
    public ResponseEntity<String> checkHealth() {
        String response = restTemplate.getForObject("http://localhost:8080/management/actuator/health", String.class);
        logger.info("{} Health Call processing finished = {}", LocalTime.now(), Thread.currentThread().getName());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public ResponseEntity<String> rateLimiterFallback(Exception e){
        return new ResponseEntity<>("Throttle service restricts further calls", HttpStatus.TOO_MANY_REQUESTS);
    }





}
