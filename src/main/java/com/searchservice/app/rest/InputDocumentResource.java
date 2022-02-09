package com.searchservice.app.rest;


import com.searchservice.app.domain.dto.ResponseDTO;
import com.searchservice.app.domain.dto.table.TableSchemaDTO;
import com.searchservice.app.domain.dto.throttler.ThrottlerResponseDTO;
import com.searchservice.app.domain.port.api.InputDocumentServicePort;
import com.searchservice.app.domain.port.api.ThrottlerServicePort;
import com.searchservice.app.rest.errors.InputDocumentException;

import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.Instant;

@RestController
@RequestMapping("${base-url.api-endpoint.home}")
public class InputDocumentResource {

    private final Logger log = LoggerFactory.getLogger(InputDocumentResource.class);

    private static final String DATA_INJECTION_THROTTLER_SERVICE = "solrDataInjectionRateLimitThrottler";
    
    public final InputDocumentServicePort inputDocumentServicePort;
    public final ThrottlerServicePort throttlerServicePort;
    public InputDocumentResource(
    		InputDocumentServicePort inputDocumentServicePort, 
    		ThrottlerServicePort throttlerServicePort) {
        this.inputDocumentServicePort = inputDocumentServicePort;
        this.throttlerServicePort = throttlerServicePort;
    }


    @RateLimiter(name=DATA_INJECTION_THROTTLER_SERVICE, fallbackMethod = "dataInjectionRateLimiterFallback")
    @PostMapping("/ingest-nrt/{clientid}/{tableName}")
    @Operation(summary = "/ For add documents we have to pass the tableName and isNRT and it will return statusCode and message.", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Object> documents(
							    		@PathVariable String tableName, 
							    		@PathVariable int clientid, 
							    		@RequestBody String payload){

        log.debug("Solr documents add");
        
        // Apply RequestSizeLimiting Throttler on payload before service the request
    	ThrottlerResponseDTO throttlerMaxRequestSizeResponse
    		= throttlerServicePort.documentInjectionRequestSizeLimiter(payload, true);
        if(throttlerMaxRequestSizeResponse.getStatusCode() == 406)
        	return ResponseEntity
        			.status(HttpStatus.NOT_ACCEPTABLE)
        			.body(throttlerMaxRequestSizeResponse);
    	
        // Control will reach here ONLY IF REQUESTBODY SIZE IS UNDER THE SPECIFIED LIMIT
        
        tableName = tableName+"_"+clientid;
        Instant start = Instant.now();
        ResponseDTO solrResponseDTO = inputDocumentServicePort.addDocuments(tableName, payload);
        Instant end = Instant.now();      
        Duration timeElapsed = Duration.between(start, end);
        String result="Time taken: "+timeElapsed.toMillis()+" milliseconds";
        log.info(result);

        if(solrResponseDTO.getResponseStatusCode()==200){
            return ResponseEntity.status(HttpStatus.OK).body(solrResponseDTO);
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(solrResponseDTO);
        }
    }
    

    @RateLimiter(name=DATA_INJECTION_THROTTLER_SERVICE, fallbackMethod = "dataInjectionRateLimiterFallback")
	@PostMapping("/ingest/{clientid}/{tableName}")
    @Operation(summary = "/ For add documents we have to pass the tableName and isNRT and it will return statusCode and message.", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Object> document(
							    		@PathVariable String tableName, 
							    		@PathVariable int clientid, 
							    		@RequestBody String payload) {

        log.debug("Solr document add");

        // Apply RequestSizeLimiting Throttler on payload before service the request
    	ThrottlerResponseDTO throttlerMaxRequestSizeResponse
			= throttlerServicePort.documentInjectionRequestSizeLimiter(payload, true);
	    if(throttlerMaxRequestSizeResponse.getStatusCode() == 406)
	    	return ResponseEntity
	    			.status(HttpStatus.NOT_ACCEPTABLE)
	    			.body(throttlerMaxRequestSizeResponse);
	    
	    // Control will reach here ONLY IF REQUESTBODY SIZE IS UNDER THE SPECIFIED LIMIT
	    
        tableName = tableName+"_"+clientid;
        Instant start = Instant.now();
        ResponseDTO solrResponseDTO= inputDocumentServicePort.addDocument(tableName, payload);
        Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start, end);
        String result="Time taken: "+timeElapsed.toMillis()+" milliseconds";
        log.info(result);

        if(solrResponseDTO.getResponseStatusCode()==200){
            return ResponseEntity.status(HttpStatus.OK).body(solrResponseDTO);
        }else{
        	throw new InputDocumentException(solrResponseDTO.getResponseStatusCode(),solrResponseDTO.getResponseMessage());
        }
    }


    // Rate Limiter(Throttler) FALLBACK method
	public ResponseEntity<ThrottlerResponseDTO> dataInjectionRateLimiterFallback(
			RequestNotPermitted exception) {
		log.error("Max request rate limit fallback triggered. Exception: ", exception);

		// prepare Rate Limiting Response DTO
		ThrottlerResponseDTO rateLimitResponseDTO = throttlerServicePort.documentInjectionRateLimiter();
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.set("Retry-after:", rateLimitResponseDTO.getRequestTimeoutDuration());
		//retry the request after given timeoutDuration
		return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).headers(responseHeaders) // attach retry-info header
				.body(rateLimitResponseDTO);
	}

    @GetMapping("/testMRS")
    @Operation(summary = "/ Applying max request size throttling  on data injection.", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Object> demoMRSThrottler(@RequestParam String data) {
    	/*
    	 * MRS stands for- Max RequestBody Size
    	 */
    	ThrottlerResponseDTO throttlerMaxRequestSizeResponseDTO
    		= throttlerServicePort.documentInjectionRequestSizeLimiter(data, true);
    	if(throttlerMaxRequestSizeResponseDTO.getStatusCode() == 406)
    		return ResponseEntity
    				.status(HttpStatus.OK)
    				.body(new TableSchemaDTO(1000, "Testing version Mapper with Object!"));
    	return ResponseEntity.status(HttpStatus.OK).body(throttlerMaxRequestSizeResponseDTO);
    }
	
}
