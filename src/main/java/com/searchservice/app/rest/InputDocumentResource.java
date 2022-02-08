package com.searchservice.app.rest;


import com.searchservice.app.domain.dto.ResponseDTO;
import com.searchservice.app.domain.dto.throttler.ThrottlerRateLimitResponseDTO;
import com.searchservice.app.domain.port.api.InputDocumentServicePort;
import com.searchservice.app.domain.port.api.ThrottlerServicePort;
import com.searchservice.app.rest.errors.InputDocumentException;

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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpServerErrorException.InternalServerError;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;

@RestController
@RequestMapping("${base-url.api-endpoint.home}")
public class InputDocumentResource {

    private final Logger log = LoggerFactory.getLogger(InputDocumentResource.class);

    @Value("${throttler.test-app-url}")
    String throttlerTestAppUrl;
    private static final String DATA_INJECTION_THROTTLE_SERVICE = "solrDataInjectionRateLimitThrottler";
    private static final String DEFAULT_THROTTLE_SERVICE = "defaultRateLimitThrottler";
    
    @Autowired
    private RestTemplate restTemplate;
    
    public final InputDocumentServicePort inputDocumentServicePort;
    public final ThrottlerServicePort throttlerServicePort;
    public InputDocumentResource(
    		InputDocumentServicePort inputDocumentServicePort, 
    		ThrottlerServicePort throttlerServicePort) {
        this.inputDocumentServicePort = inputDocumentServicePort;
        this.throttlerServicePort = throttlerServicePort;
    }


    @RateLimiter(name=DATA_INJECTION_THROTTLE_SERVICE, fallbackMethod = "dataInjectionRateLimiterFallback")
    @PostMapping("/ingest-nrt/{clientid}/{tableName}")
    @Operation(summary = "/ For add documents we have to pass the tableName and isNRT and it will return statusCode and message.", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ResponseDTO> documents(@PathVariable String tableName,@PathVariable int clientid, @RequestBody String payload){

        log.debug("Solr documents add");
        tableName = tableName+"_"+clientid;
        Instant start = Instant.now();
        ResponseDTO solrResponseDTO = inputDocumentServicePort.addDocuments(tableName, payload);
        Instant end = Instant.now();      
        Duration timeElapsed = Duration.between(start, end);
        String result="Time taken: "+timeElapsed.toMillis()+" milliseconds";
       log.debug(result);

        if(solrResponseDTO.getResponseStatusCode()==200){
            return ResponseEntity.status(HttpStatus.OK).body(solrResponseDTO);
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(solrResponseDTO);
        }

    }
    

    @RateLimiter(name=DATA_INJECTION_THROTTLE_SERVICE, fallbackMethod = "dataInjectionRateLimiterFallback")
	@PostMapping("/ingest/{clientid}/{tableName}")
    @Operation(summary = "/ For add documents we have to pass the tableName and isNRT and it will return statusCode and message.", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ResponseDTO> document(
    		@PathVariable String tableName, 
    		@PathVariable int clientid, 
    		@RequestBody String payload) {

        log.debug("Solr documents add");

        tableName = tableName+"_"+clientid;
        Instant start = Instant.now();
        ResponseDTO solrResponseDTO= inputDocumentServicePort.addDocument(tableName, payload);
        Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start, end);
        String result="Time taken: "+timeElapsed.toMillis()+" milliseconds";
       log.debug(result);

        if(solrResponseDTO.getResponseStatusCode()==200){
            return ResponseEntity.status(HttpStatus.OK).body(solrResponseDTO);
        }else{
        	throw new InputDocumentException(solrResponseDTO.getResponseStatusCode(),solrResponseDTO.getResponseMessage());
        }
    }
	
	
	// test throttler
    @GetMapping("/test")
    @RateLimiter(name=DEFAULT_THROTTLE_SERVICE, fallbackMethod = "dataInjectionRateLimiterFallback")
    @Operation(summary = "/ Applying rate limiter sample web app.", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<String> demoRateLimiterThrottler() {
    	log.info("/test api got a hit..");
    	String response = null;
    	try {
    		// GET employees <- from test-app running on port 2020
    		log.info("Processing GET request from uri: {}", String.format("%s/employees", throttlerTestAppUrl));
    		response = restTemplate.getForObject(
        			throttlerTestAppUrl+"/employees", String.class);
        } catch(ResourceAccessException | InternalServerError e) {
        	log.error("Probably target server is not up!", e);
        	return new ResponseEntity<>("Target Server is down", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        log.info("{} | Call processing finished = {}", 
        		LocalTime.now(), 
        		Thread.currentThread().getName());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    
	public ResponseEntity<ThrottlerRateLimitResponseDTO> dataInjectionRateLimiterFallback(RequestNotPermitted exception) {
		log.info("Max request rate limit is being applied");

		/** prepare Rate Limiting Response DTO */
		ThrottlerRateLimitResponseDTO rateLimitResponseDTO = throttlerServicePort.dataInjectionRateLimiter();
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.set("Retry-after:", rateLimitResponseDTO.getRequestTimeoutDuration());
		/** retry the request after given timeoutDuration */
		return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).headers(responseHeaders) // attach retry-info header
				.body(rateLimitResponseDTO);
	}

}
