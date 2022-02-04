package com.searchservice.app.rest;


import com.searchservice.app.domain.dto.throttler.ThrottlerMaxRequestSizeResponseDTO;
import com.searchservice.app.domain.dto.throttler.ThrottlerRateLimitResponseDTO;
import com.searchservice.app.domain.port.api.ThrottlerServicePort;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpServerErrorException.InternalServerError;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalTime;

//@RestController
//@RequestMapping("/throttle")
public class ThrottlerTestResource {
	/*
	 * This Controller is created for implementing & testing
	 * throttler features:
	 * 	1. Rate Limiting
	 */
	private static final Logger logger = LoggerFactory.getLogger(ThrottlerTestResource.class);
    @Value("${base-solr-url}")
	String baseSolrUrl;
    @Value("${base-app-url}")
    String baseAppUrl;
    @Value("${throttler-test-app-url}")
    String throttlerTestAppUrl;

    private static final String DEFAULT_THROTTLE_SERVICE = "defaultRateLimitThrottler";
   
//	@Bean
//    public RestTemplate getRestTemplate() {
//        return new RestTemplate();
//    }

    @Autowired
    private RestTemplate restTemplate;

    public final ThrottlerServicePort throttlerServicePort;

    public ThrottlerTestResource(ThrottlerServicePort throttlerServicePort) {
        this.throttlerServicePort = throttlerServicePort;
    }

    @GetMapping("/health")
    @RateLimiter(name=DEFAULT_THROTTLE_SERVICE, fallbackMethod = "rateLimiterFallback")
    @Operation(summary = "/ Health rate limiter.", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<String> checkHealth() {
        String response = restTemplate.getForObject(baseAppUrl+"/management/actuator/health", String.class);
        logger.info("{} Health Call processing finished = {}", LocalTime.now(), Thread.currentThread().getName());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/test")
    @RateLimiter(name=DEFAULT_THROTTLE_SERVICE, fallbackMethod = "rateLimiterFallback")
    @Operation(summary = "/ Applying rate limiter sample web app.", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<String> demoRateLimiterThrottler() {    	
    	String response = null;
    	try {
        	response = restTemplate.getForObject(
        			throttlerTestAppUrl+"/test/throttle", String.class);
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
    @Operation(summary = "/Applying rate limiter on Tables.", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<String> throttleCollectionResource() {
        String response = restTemplate.getForObject(
        		baseAppUrl+"/searchservice/table/collections", String.class);
        logger.info("{} | REST Call processing finished = {}", 
        		LocalTime.now(), 
        		Thread.currentThread().getName());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    ////////// Testing Max Request Size Limiter /////////////
    @GetMapping("/testMRS")
    @Operation(summary = "/ Applying max request size throttling  on data injection.", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<?> demoMRSThrottler(@RequestParam String data) {
    	/*
    	 * MRS stands for- Max RequestBody Size
    	 */
    	ThrottlerMaxRequestSizeResponseDTO throttlerMaxRequestSizeResponseDTO
    		= throttlerServicePort.dataInjectionRequestSizeLimiter(data);
    	return ResponseEntity.status(HttpStatus.OK).body(throttlerMaxRequestSizeResponseDTO);
    }
    
    ////////// Rate Limiter fallbacks ///////////
    public ResponseEntity<String> rateLimiterFallback(Exception e){
    	logger.error("Rate Limiter fallback executed", e);
        return ResponseEntity
        		.status(HttpStatus.TOO_MANY_REQUESTS)
        		.body("order service does not permit further calls");
    }
    
    public ResponseEntity<ThrottlerRateLimitResponseDTO> solrCollectionsRateLimiter(
    				RequestNotPermitted exception) {
        logger.info("Max request rate limit is being applied");

        // prepare Rate Limiting Response DTO
        ThrottlerRateLimitResponseDTO rateLimitResponseDTO = throttlerServicePort.dataInjectionRateLimiter();
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Retry-after:", rateLimitResponseDTO.getRequestTimeoutDuration()); // retry the request after given timeoutDuration
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .headers(responseHeaders) // attach retry-info header
                .body(rateLimitResponseDTO);
    }
}
