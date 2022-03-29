package com.searchservice.app.rest;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.searchservice.app.domain.dto.logger.LoggersDTO;
import com.searchservice.app.domain.dto.throttler.ThrottlerResponse;
import com.searchservice.app.domain.port.api.InputDocumentServicePort;
import com.searchservice.app.domain.port.api.ManageTableServicePort;
import com.searchservice.app.domain.port.api.ThrottlerServicePort;
import com.searchservice.app.domain.service.InputDocumentService;
import com.searchservice.app.domain.utils.LoggerUtils;
import com.searchservice.app.rest.errors.HttpStatusCode;
import com.searchservice.app.rest.errors.InvalidJsonInputOccurredException;

import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("${base-url.api-endpoint.home}")
public class InputDocumentResource {
	private String servicename = "Input_Document_Resource";

	private String username = "Username";
	private final Logger log = LoggerFactory.getLogger(InputDocumentResource.class);
	private List<Object> listOfParameters;
	
	@Autowired
	InputDocumentService inputDocumentService;

	@Autowired
	LoggerUtils loggerUtils;
	
	private static final String DOCUMENT_INJECTION_THROTTLER_SERVICE = "documentInjectionRateLimitThrottler";

	public final InputDocumentServicePort inputDocumentServicePort;
	public final ThrottlerServicePort throttlerServicePort;
	public final ManageTableServicePort manageTableServicePort;

	public InputDocumentResource(InputDocumentServicePort inputDocumentServicePort,
			ThrottlerServicePort throttlerServicePort, ManageTableServicePort manageTableServicePort,LoggerUtils loggerUtils) {
		this.inputDocumentServicePort = inputDocumentServicePort;
		this.throttlerServicePort = throttlerServicePort;
		this.manageTableServicePort = manageTableServicePort;
		this.loggerUtils = loggerUtils;
	}

	private void successMethod(String nameofCurrMethod, LoggersDTO loggersDTO) {
		String timestamp;
		loggersDTO.setServicename(servicename);
		loggersDTO.setUsername(username);
		loggersDTO.setNameofmethod(nameofCurrMethod);
		timestamp = LoggerUtils.utcTime().toString();
		loggersDTO.setTimestamp(timestamp);
	}


@RateLimiter(name = DOCUMENT_INJECTION_THROTTLER_SERVICE, fallbackMethod = "documentInjectionRateLimiterFallback")
@PostMapping("/ingest-nrt/{tenantId}/{tableName}")
@Operation(summary = "ADD DOCUMENTS IN THE TABLE OF THE GIVEN TENANT ID. INPUT SHOULD BE A LIST OF DOCUMENTS SATISFYING THE TABLE SCHEMA. NEAR REAL-TIME API.", security = @SecurityRequirement(name = "bearerAuth"))
public ResponseEntity<ThrottlerResponse> documents(@PathVariable int tenantId, @PathVariable String tableName,
		@RequestBody String payload) {
	listOfParameters = new ArrayList<Object>();
	log.debug("Solr documents add");
	listOfParameters.add(tenantId);
	listOfParameters.add(tableName);
	listOfParameters.add(payload);
        log.debug("Search documents add");
        if(!inputDocumentService.isValidJsonArray(payload))
        	throw new InvalidJsonInputOccurredException(HttpStatusCode.INVALID_JSON_INPUT.getCode(),HttpStatusCode.INVALID_JSON_INPUT.getMessage());
        String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
		String timestamp = LoggerUtils.utcTime().toString();
		LoggersDTO loggersDTO = LoggerUtils.getRequestLoggingInfo(servicename, username,nameofCurrMethod,timestamp,listOfParameters);
		loggerUtils.printlogger(loggersDTO,true,false);
		loggersDTO.setCorrelationid(loggersDTO.getCorrelationid());
		loggersDTO.setIpaddress(loggersDTO.getIpaddress());
		loggersDTO.setListOfParameters(loggersDTO.getListOfParameters());

		// Apply RequestSizeLimiting Throttler on payload before service the request
		ThrottlerResponse documentInjectionThrottlerResponse = throttlerServicePort
				.documentInjectionRequestSizeLimiter(payload, true);

		successMethod(nameofCurrMethod, loggersDTO);
		if (documentInjectionThrottlerResponse.getStatusCode() == 406)
			return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(documentInjectionThrottlerResponse);

		// Control will reach here ONLY IF REQUESTBODY SIZE IS UNDER THE SPECIFIED LIMIT
		tableName = tableName + "_" + tenantId;
		if (manageTableServicePort.isTableExists(tableName)) {
			successMethod(nameofCurrMethod, loggersDTO);
			return performDocumentInjection(tableName, payload, documentInjectionThrottlerResponse, loggersDTO);
		} else {
			return documentInjectWithInvalidTableName(tenantId, tableName.split("_")[0]);
		}
	}

	@RateLimiter(name = DOCUMENT_INJECTION_THROTTLER_SERVICE, fallbackMethod = "documentInjectionRateLimiterFallback")
	@PostMapping("/ingest/{tenantId}/{tableName}")
    @Operation(summary = "ADD DOCUMENTS IN THE TABLE OF THE GIVEN TENANT ID. INPUT SHOULD BE A LIST OF DOCUMENTS SATISFYING THE TABLE SCHEMA.", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ThrottlerResponse> document(
										@PathVariable int tenantId,
							    		@PathVariable String tableName,
							    		@RequestBody String payload) {

        log.debug("Search document add");
        listOfParameters = new ArrayList<Object>();
		listOfParameters.add(tenantId);
		listOfParameters.add(tableName);
		listOfParameters.add(payload);
        if(!inputDocumentService.isValidJsonArray(payload))
        	throw new InvalidJsonInputOccurredException(HttpStatusCode.INVALID_JSON_INPUT.getCode(),HttpStatusCode.INVALID_JSON_INPUT.getMessage());
        String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
		String timestamp = LoggerUtils.utcTime().toString();
		LoggersDTO loggersDTO = LoggerUtils.getRequestLoggingInfo(servicename, username, nameofCurrMethod, timestamp,listOfParameters);
		loggerUtils.printlogger(loggersDTO, true, false);
		loggersDTO.setCorrelationid(loggersDTO.getCorrelationid());
		loggersDTO.setIpaddress(loggersDTO.getIpaddress());

		// Apply RequestSizeLimiting Throttler on payload before service the request
		ThrottlerResponse documentInjectionThrottlerResponse = throttlerServicePort
				.documentInjectionRequestSizeLimiter(payload, false);
		if (documentInjectionThrottlerResponse.getStatusCode() == 406)
			return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(documentInjectionThrottlerResponse);

		// Control will reach here ONLY IF REQUESTBODY SIZE IS UNDER THE SPECIFIED LIMIT

		tableName = tableName + "_" + tenantId;
		if (documentInjectionThrottlerResponse.getStatusCode() == 406)
			return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(documentInjectionThrottlerResponse);

		// Control will reach here ONLY IF REQUESTBODY SIZE IS UNDER THE SPECIFIED LIMIT
		if (manageTableServicePort.isTableExists(tableName)) {
			return performDocumentInjection(tableName, payload, documentInjectionThrottlerResponse, loggersDTO);
		} else {
			return documentInjectWithInvalidTableName(tenantId, tableName.split("_")[0]);
		}
	}

	// Rate Limiter(Throttler) FALLBACK method
	public ResponseEntity<ThrottlerResponse> documentInjectionRateLimiterFallback(int tenantId, String tableName,
			String payload, RequestNotPermitted exception) {
		log.error("Max request rate limit fallback triggered. Exception: ", exception);

		// prepare Rate Limiting Response DTO
		ThrottlerResponse rateLimitResponseDTO = throttlerServicePort.documentInjectionRateLimiter();
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.set("Retry-after:", rateLimitResponseDTO.getRequestTimeoutDuration());
		// retry the request after given timeoutDuration

		return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).headers(responseHeaders) // attach retry-info header
				.body(rateLimitResponseDTO);
	}

	public ResponseEntity<ThrottlerResponse> performDocumentInjection(String tableName, String payload,
			ThrottlerResponse documentInjectionThrottlerResponse, LoggersDTO loggersDTO) {
		Instant start = Instant.now();
		ThrottlerResponse documentInjectionResponse = inputDocumentServicePort.addDocuments(tableName, payload,
				loggersDTO);
		Instant end = Instant.now();
		Duration timeElapsed = Duration.between(start, end);
		String result = "Time taken: " + timeElapsed.toMillis() + " milliseconds";

		documentInjectionThrottlerResponse.setMessage(documentInjectionResponse.getMessage());
		documentInjectionThrottlerResponse.setStatusCode(documentInjectionResponse.getStatusCode());
		if (documentInjectionThrottlerResponse.getStatusCode() == 200) {
			loggerUtils.printlogger(loggersDTO, false, false);
			return ResponseEntity.status(HttpStatus.OK).body(documentInjectionThrottlerResponse);
		} else {
			loggerUtils.printlogger(loggersDTO, false, true);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(documentInjectionThrottlerResponse);
		}
	}

	public ResponseEntity<ThrottlerResponse> documentInjectWithInvalidTableName(int tenantId, String tableName) {
		ThrottlerResponse documentInjectionThrottlerResponse = new ThrottlerResponse();
		documentInjectionThrottlerResponse.setStatusCode(400);
		documentInjectionThrottlerResponse
				.setMessage("Table " + tableName + " For Client ID: " + tenantId + " Does Not Exist");
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(documentInjectionThrottlerResponse);
	}
}
