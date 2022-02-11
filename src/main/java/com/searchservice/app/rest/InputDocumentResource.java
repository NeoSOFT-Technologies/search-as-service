package com.searchservice.app.rest;


import java.time.Duration;
import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.searchservice.app.domain.dto.logger.LoggersDTO;
import com.searchservice.app.domain.dto.throttler.ThrottlerResponseDTO;
import com.searchservice.app.domain.port.api.InputDocumentServicePort;
import com.searchservice.app.domain.port.api.ThrottlerServicePort;
import com.searchservice.app.domain.utils.LoggerUtils;

import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("${base-url.api-endpoint.home}")
public class InputDocumentResource {


	private String servicename = "Manage_Table_Resource";

	private String username = "Username";
	
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
    public ResponseEntity<ThrottlerResponseDTO> documents(
							    		@PathVariable String tableName, 
							    		@PathVariable int clientid, 
							    		@RequestBody String payload){

        log.debug("Solr documents add");

		String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
		String timestamp = LoggerUtils.utcTime().toString();
		LoggersDTO loggersDTO = LoggerUtils.getRequestLoggingInfo(servicename, username,nameofCurrMethod,timestamp);
		LoggerUtils.printlogger(loggersDTO,true,false);
		loggersDTO.setCorrelationid(loggersDTO.getCorrelationid());
		loggersDTO.setIpaddress(loggersDTO.getIpaddress());
		
       
        // Apply RequestSizeLimiting Throttler on payload before service the request
    	ThrottlerResponseDTO documentInjectionThrottlerResponse
    		= throttlerServicePort.documentInjectionRequestSizeLimiter(payload, true);
        if(documentInjectionThrottlerResponse.getStatusCode() == 406)
        	return ResponseEntity
        			.status(HttpStatus.NOT_ACCEPTABLE)
        			.body(documentInjectionThrottlerResponse);
    	
        // Control will reach here ONLY IF REQUESTBODY SIZE IS UNDER THE SPECIFIED LIMIT
        
        tableName = tableName+"_"+clientid;
        Instant start = Instant.now();
        ThrottlerResponseDTO documentInjectionResponse = inputDocumentServicePort.addDocuments(tableName, payload,loggersDTO);
        Instant end = Instant.now();

        Duration timeElapsed = Duration.between(start, end);
        String result="Time taken: "+timeElapsed.toMillis()+" milliseconds";
        log.info(result);

        documentInjectionThrottlerResponse.setResponseMessage(documentInjectionResponse.getResponseMessage());
        documentInjectionThrottlerResponse.setStatusCode(documentInjectionResponse.getStatusCode());
        
        loggersDTO.setServicename(servicename);
		loggersDTO.setUsername(username);
		loggersDTO.setNameofmethod(nameofCurrMethod);
		timestamp = LoggerUtils.utcTime().toString();
		loggersDTO.setTimestamp(timestamp);
        if(documentInjectionThrottlerResponse.getStatusCode()==200){
        	LoggerUtils.printlogger(loggersDTO,false,false);
        	return ResponseEntity.status(HttpStatus.OK).body(documentInjectionThrottlerResponse);
        }else{
        	LoggerUtils.printlogger(loggersDTO,false,true);
        	return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(documentInjectionThrottlerResponse);
        }
    }
    

    @RateLimiter(name=DOCUMENT_INJECTION_THROTTLER_SERVICE, fallbackMethod = "documentInjectionRateLimiterFallback")
	@PostMapping("/ingest/{clientid}/{tableName}")
    @Operation(summary = "/ For add documents we have to pass the tableName and isNRT and it will return statusCode and message.", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ThrottlerResponseDTO> document(
							    		@PathVariable String tableName, 
							    		@PathVariable int clientid, 
							    		@RequestBody String payload) {

        log.debug("Solr document add");
        
        String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
		String timestamp = LoggerUtils.utcTime().toString();
		LoggersDTO loggersDTO = LoggerUtils.getRequestLoggingInfo(servicename, username,nameofCurrMethod,timestamp);
		LoggerUtils.printlogger(loggersDTO,true,false);
		loggersDTO.setCorrelationid(loggersDTO.getCorrelationid());
		loggersDTO.setIpaddress(loggersDTO.getIpaddress());
		
        // Apply RequestSizeLimiting Throttler on payload before service the request
		ThrottlerResponseDTO documentInjectionThrottlerResponse = throttlerServicePort
				.documentInjectionRequestSizeLimiter(payload, false);
		if (documentInjectionThrottlerResponse.getStatusCode() == 406)
			return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(documentInjectionThrottlerResponse);

		// Control will reach here ONLY IF REQUESTBODY SIZE IS UNDER THE SPECIFIED LIMIT

		tableName = tableName + "_" + clientid;
		Instant start = Instant.now();
		ThrottlerResponseDTO documentInjectionResponse = inputDocumentServicePort.addDocument(tableName, payload,loggersDTO);
		Instant end = Instant.now();
		Duration timeElapsed = Duration.between(start, end);
		String result = "Time taken: " + timeElapsed.toMillis() + " milliseconds";
		log.info(result);

		documentInjectionThrottlerResponse.setResponseMessage(documentInjectionResponse.getResponseMessage());
		documentInjectionThrottlerResponse.setStatusCode(documentInjectionResponse.getStatusCode());

		loggersDTO.setServicename(servicename);
		loggersDTO.setUsername(username);
		loggersDTO.setNameofmethod(nameofCurrMethod);
		timestamp = LoggerUtils.utcTime().toString();
		loggersDTO.setTimestamp(timestamp);

		if (documentInjectionThrottlerResponse.getStatusCode() == 200) {
			LoggerUtils.printlogger(loggersDTO,false,false);
			return ResponseEntity.status(HttpStatus.OK).body(documentInjectionThrottlerResponse);
		} else {
			LoggerUtils.printlogger(loggersDTO,false,true);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(documentInjectionThrottlerResponse);
		}
    }


    // Rate Limiter(Throttler) FALLBACK method
	public ResponseEntity<ThrottlerResponseDTO> documentInjectionRateLimiterFallback(
			
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
	
}
