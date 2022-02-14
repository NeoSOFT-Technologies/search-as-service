package com.searchservice.app.rest;


import com.searchservice.app.domain.dto.throttler.ThrottlerResponse;
import com.searchservice.app.domain.port.api.InputDocumentServicePort;
import com.searchservice.app.domain.port.api.ThrottlerServicePort;

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

    private static final String DOCUMENT_INJECTION_THROTTLER_SERVICE = "documentInjectionRateLimitThrottler";
    
    public final InputDocumentServicePort inputDocumentServicePort;
    public final ThrottlerServicePort throttlerServicePort;
    public InputDocumentResource(
    		InputDocumentServicePort inputDocumentServicePort, 
    		ThrottlerServicePort throttlerServicePort) {
        this.inputDocumentServicePort = inputDocumentServicePort;
        this.throttlerServicePort = throttlerServicePort;
    }


    @RateLimiter(name=DOCUMENT_INJECTION_THROTTLER_SERVICE, fallbackMethod = "documentInjectionRateLimiterFallback")
    @PostMapping("/ingest-nrt/{clientid}/{tableName}")
    @Operation(summary = "/ For add documents we have to pass the tableName and isNRT and it will return statusCode and message.", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ThrottlerResponse> documents(
							    		@PathVariable String tableName, 
							    		@PathVariable int clientid, 
							    		@RequestBody String payload){

        log.debug("Solr documents add");
        
        // Apply RequestSizeLimiting Throttler on payload before service the request
    	ThrottlerResponse documentInjectionThrottlerResponse
    		= throttlerServicePort.documentInjectionRequestSizeLimiter(payload, true);
        if(documentInjectionThrottlerResponse.getStatusCode() == 406)
        	return ResponseEntity
        			.status(HttpStatus.NOT_ACCEPTABLE)
        			.body(documentInjectionThrottlerResponse);
    	
        // Control will reach here ONLY IF REQUESTBODY SIZE IS UNDER THE SPECIFIED LIMIT
        
        tableName = tableName+"_"+clientid;
        Instant start = Instant.now();
        ThrottlerResponse documentInjectionResponse = inputDocumentServicePort.addDocuments(tableName, payload);
        Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start, end);
        String result="Time taken: "+timeElapsed.toMillis()+" milliseconds";
        log.info(result);

        documentInjectionThrottlerResponse.setResponseMessage(documentInjectionResponse.getResponseMessage());
        documentInjectionThrottlerResponse.setStatusCode(documentInjectionResponse.getStatusCode());
      
        if(documentInjectionThrottlerResponse.getStatusCode()==200){
            return ResponseEntity.status(HttpStatus.OK).body(documentInjectionThrottlerResponse);
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(documentInjectionThrottlerResponse);
        }
    }
    

    @RateLimiter(name=DOCUMENT_INJECTION_THROTTLER_SERVICE, fallbackMethod = "documentInjectionRateLimiterFallback")
	@PostMapping("/ingest/{clientid}/{tableName}")
    @Operation(summary = "/ For add documents we have to pass the tableName and isNRT and it will return statusCode and message.", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ThrottlerResponse> document(
							    		@PathVariable String tableName, 
							    		@PathVariable int clientid, 
							    		@RequestBody String payload) {

        log.debug("Solr document add");

        // Apply RequestSizeLimiting Throttler on payload before service the request
		ThrottlerResponse documentInjectionThrottlerResponse = throttlerServicePort
				.documentInjectionRequestSizeLimiter(payload, false);
		if (documentInjectionThrottlerResponse.getStatusCode() == 406)
			return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(documentInjectionThrottlerResponse);

		// Control will reach here ONLY IF REQUESTBODY SIZE IS UNDER THE SPECIFIED LIMIT

		tableName = tableName + "_" + clientid;
		Instant start = Instant.now();
		ThrottlerResponse documentInjectionResponse = inputDocumentServicePort.addDocument(tableName, payload);
		Instant end = Instant.now();
		Duration timeElapsed = Duration.between(start, end);
		String result = "Time taken: " + timeElapsed.toMillis() + " milliseconds";
		log.info(result);

		documentInjectionThrottlerResponse.setResponseMessage(documentInjectionResponse.getResponseMessage());
		documentInjectionThrottlerResponse.setStatusCode(documentInjectionResponse.getStatusCode());

		if (documentInjectionThrottlerResponse.getStatusCode() == 200) {
			return ResponseEntity.status(HttpStatus.OK).body(documentInjectionThrottlerResponse);
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(documentInjectionThrottlerResponse);
		}
    }


    // Rate Limiter(Throttler) FALLBACK method
	public ResponseEntity<ThrottlerResponse> documentInjectionRateLimiterFallback(
			String tableName, 
			int clientid, 
			String payload, 
			RequestNotPermitted exception) {
		log.error("Max request rate limit fallback triggered. Exception: ", exception);

		// prepare Rate Limiting Response DTO
		ThrottlerResponse rateLimitResponseDTO = throttlerServicePort.documentInjectionRateLimiter();
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.set("Retry-after:", rateLimitResponseDTO.getRequestTimeoutDuration());
		//retry the request after given timeoutDuration
		
		return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).headers(responseHeaders) // attach retry-info header
				.body(rateLimitResponseDTO);
	}
	
}
