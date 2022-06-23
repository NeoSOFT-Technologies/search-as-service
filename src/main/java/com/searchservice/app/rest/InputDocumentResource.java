package com.searchservice.app.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.searchservice.app.domain.dto.throttler.ThrottlerResponse;
import com.searchservice.app.domain.port.api.InputDocumentServicePort;
import com.searchservice.app.domain.port.api.ThrottlerServicePort;
import com.searchservice.app.domain.service.InputDocumentService;
import com.searchservice.app.rest.errors.HttpStatusCode;

import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("${base-url.api-endpoint.home}")
public class InputDocumentResource {

	private final Logger log = LoggerFactory.getLogger(InputDocumentResource.class);

	@Autowired
	InputDocumentService inputDocumentService;

	private static final String DOCUMENT_INJECTION_THROTTLER_SERVICE = "documentInjectionRateLimitThrottler";

	public final InputDocumentServicePort inputDocumentServicePort;
	public final ThrottlerServicePort throttlerServicePort;

	public InputDocumentResource(InputDocumentServicePort inputDocumentServicePort,
			ThrottlerServicePort throttlerServicePort) {
		this.inputDocumentServicePort = inputDocumentServicePort;
		this.throttlerServicePort = throttlerServicePort;

	}

	
	@RateLimiter(name = DOCUMENT_INJECTION_THROTTLER_SERVICE, fallbackMethod = "documentInjectionRateLimiterFallback")
	@PostMapping("/ingest-nrt/{tableName}")
	@Operation(summary = "ADD DOCUMENTS IN THE TABLE OF THE GIVEN TENANT ID. INPUT SHOULD BE A LIST OF DOCUMENTS SATISFYING THE TABLE SCHEMA. NEAR REAL-TIME API.", security = @SecurityRequirement(name = "bearerAuth"))
	@PreAuthorize(value = "@keycloakUserPermission.isCreatePermissionEnabled()")
	public ResponseEntity<ThrottlerResponse> addDocumentsNRT(@RequestParam int tenantId, @PathVariable String tableName,
			@RequestBody String payload) {
		// Apply RequestSizeLimiting Throttler on payload before service the request
		ThrottlerResponse documentInjectionThrottlerResponse = throttlerServicePort
				.documentInjectionRequestSizeLimiter(payload, true);

		if (documentInjectionThrottlerResponse.getStatusCode() == HttpStatusCode.NOT_ACCEPTABLE_ERROR.getCode())
			return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(documentInjectionThrottlerResponse);

		// Control will reach here ONLY IF REQUESTBODY SIZE IS UNDER THE SPECIFIED LIMIT
		tableName = tableName + "_" + tenantId;
		return inputDocumentServicePort.performDocumentInjection(true,tableName, payload, documentInjectionThrottlerResponse);
		
	}

	@RateLimiter(name = DOCUMENT_INJECTION_THROTTLER_SERVICE, fallbackMethod = "documentInjectionRateLimiterFallback")
	@PostMapping("/ingest/{tableName}")
	@Operation(summary = "ADD DOCUMENTS IN THE TABLE OF THE GIVEN TENANT ID. INPUT SHOULD BE A LIST OF DOCUMENTS SATISFYING THE TABLE SCHEMA.", security = @SecurityRequirement(name = "bearerAuth"))
	@PreAuthorize(value = "@keycloakUserPermission.isCreatePermissionEnabled()")
	public ResponseEntity<ThrottlerResponse> addDocumentsBatch(@RequestParam int tenantId, @PathVariable String tableName,
			@RequestBody String payload) {
		// Apply RequestSizeLimiting Throttler on payload before service the request
		ThrottlerResponse documentInjectionThrottlerResponse = throttlerServicePort
				.documentInjectionRequestSizeLimiter(payload, false);
		if (documentInjectionThrottlerResponse.getStatusCode() == HttpStatusCode.NOT_ACCEPTABLE_ERROR.getCode())
			return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(documentInjectionThrottlerResponse);

		// Control will reach here ONLY IF REQUESTBODY SIZE IS UNDER THE SPECIFIED LIMIT

		tableName = tableName + "_" + tenantId;
		if (documentInjectionThrottlerResponse.getStatusCode() == HttpStatusCode.NOT_ACCEPTABLE_ERROR.getCode())
			return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(documentInjectionThrottlerResponse);
		// Control will reach here ONLY IF REQUESTBODY SIZE IS UNDER THE SPECIFIED LIMIT
		return inputDocumentServicePort.performDocumentInjection(false, tableName, payload, documentInjectionThrottlerResponse);
	}

	// Rate Limiter(Throttler) FALLBACK method
	public ResponseEntity<ThrottlerResponse> documentInjectionRateLimiterFallback(RequestNotPermitted exception) {
		log.error("Max request rate limit fallback triggered. Exception: ", exception);

		// prepare Rate Limiting Response DTO
		ThrottlerResponse rateLimitResponseDTO = throttlerServicePort.documentInjectionRateLimiter();
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.set("Retry-after:", rateLimitResponseDTO.getRequestTimeoutDuration());
		// retry the request after given timeoutDuration

		return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).headers(responseHeaders) // attach retry-info header
				.body(rateLimitResponseDTO);
	}

}