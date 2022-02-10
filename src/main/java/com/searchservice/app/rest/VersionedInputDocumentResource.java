package com.searchservice.app.rest;

import com.searchservice.app.domain.dto.ResponseDTO;
import com.searchservice.app.domain.dto.logger.CorrelationID;
import com.searchservice.app.domain.dto.logger.LoggersDTO;
import com.searchservice.app.domain.port.api.InputDocumentServicePort;
import com.searchservice.app.domain.utils.LoggerUtils;
import com.searchservice.app.rest.errors.InputDocumentException;

import com.searchservice.app.domain.dto.ResponseMessages;
import com.searchservice.app.domain.dto.throttler.ThrottlerResponseDTO;
import com.searchservice.app.domain.port.api.InputDocumentServicePort;
import com.searchservice.app.domain.port.api.ThrottlerServicePort;
import com.searchservice.app.rest.errors.BadRequestOccurredException;

import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("${base-url.api-endpoint.versioned-home}")
public class VersionedInputDocumentResource {

	private final Logger log = LoggerFactory.getLogger(VersionedInputDocumentResource.class);

	private static final String DOCUMENT_INJECTION_THROTTLER_SERVICE = "documentInjectionRateLimitThrottler";

	public final InputDocumentServicePort inputDocumentServicePort;
	public final ThrottlerServicePort throttlerServicePort;

	ZonedDateTime utc = ZonedDateTime.now(ZoneOffset.UTC);

	private String servicename = "Manage_Table_Resource";

	private String username = "Username";

	public VersionedInputDocumentResource(InputDocumentServicePort inputDocumentServicePort,
			ThrottlerServicePort throttlerServicePort) {
		this.inputDocumentServicePort = inputDocumentServicePort;
		this.throttlerServicePort = throttlerServicePort;
	}

	@RateLimiter(name = DOCUMENT_INJECTION_THROTTLER_SERVICE, fallbackMethod = "documentInjectionRateLimiterFallback")
	@PostMapping("/ingest-nrt/{clientid}/{tableName}")
	@Operation(summary = "/ For add documents we have to pass the tableName and isNRT and it will return statusCode and message.", security = @SecurityRequirement(name = "bearerAuth"))
	public ThrottlerResponseDTO documents(@PathVariable String tableName, @PathVariable int clientid,
			@RequestBody String payload) {

		log.debug("Solr documents add");

		String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
		String timestamp = utc.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		LoggersDTO loggersDTO = LoggerUtils.getRequestLoggingInfo(servicename, username, nameofCurrMethod, timestamp);
		LoggerUtils.Printlogger(loggersDTO, true, false);
		loggersDTO.setCorrelationid(loggersDTO.getCorrelationid());
		loggersDTO.setIpaddress(loggersDTO.getIpaddress());

		// Apply RequestSizeLimiting Throttler on payload before service the request
		ThrottlerResponseDTO documentInjectionThrottlerResponse = throttlerServicePort
				.documentInjectionRequestSizeLimiter(payload, true);
		if (documentInjectionThrottlerResponse.getStatusCode() == 406)
			return documentInjectionThrottlerResponse;

		// Control will reach here ONLY IF REQUESTBODY SIZE IS UNDER THE SPECIFIED LIMIT

		tableName = tableName + "_" + clientid;
		Instant start = Instant.now();
		ThrottlerResponseDTO documentInjectionResponse = inputDocumentServicePort.addDocuments(tableName, payload,
				loggersDTO);
		Instant end = Instant.now();
		Duration timeElapsed = Duration.between(start, end);
		String result = "Time taken: " + timeElapsed.toMillis() + " milliseconds";
		log.info(result);

		loggersDTO.setServicename(servicename);
		loggersDTO.setUsername(username);
		loggersDTO.setNameofmethod(nameofCurrMethod);
		loggersDTO.setTimestamp(utc.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

		documentInjectionThrottlerResponse.setResponseMessage(documentInjectionResponse.getResponseMessage());
		documentInjectionThrottlerResponse.setStatusCode(documentInjectionResponse.getStatusCode());

		if (documentInjectionThrottlerResponse.getStatusCode() == 200) {
			LoggerUtils.Printlogger(loggersDTO, false, false);
			return documentInjectionThrottlerResponse;
		} else {
			LoggerUtils.Printlogger(loggersDTO, false, true);
			throw new BadRequestOccurredException(400, ResponseMessages.BAD_REQUEST_MSG);
		}

	}

	@RateLimiter(name = DOCUMENT_INJECTION_THROTTLER_SERVICE, fallbackMethod = "documentInjectionRateLimiterFallback")
	@PostMapping("/ingest/{clientid}/{tableName}")
	@Operation(summary = "/ For add documents we have to pass the tableName and isNRT and it will return statusCode and message.", security = @SecurityRequirement(name = "bearerAuth"))
	public ThrottlerResponseDTO document(@PathVariable String tableName, @PathVariable int clientid,
			@RequestBody String payload) {

		log.info("Solr documents add");

		String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
		String timestamp = utc.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		LoggersDTO loggersDTO = LoggerUtils.getRequestLoggingInfo(servicename, username, nameofCurrMethod, timestamp);
		LoggerUtils.Printlogger(loggersDTO, true, false);
		loggersDTO.setCorrelationid(loggersDTO.getCorrelationid());
		loggersDTO.setIpaddress(loggersDTO.getIpaddress());

		// Apply RequestSizeLimiting Throttler on payload before service the request
		ThrottlerResponseDTO documentInjectionThrottlerResponse = throttlerServicePort
				.documentInjectionRequestSizeLimiter(payload, false);

		if (documentInjectionThrottlerResponse.getStatusCode() == 406)
			return documentInjectionThrottlerResponse;

		// Control will reach here ONLY IF REQUESTBODY SIZE IS UNDER THE SPECIFIED LIMIT

		tableName = tableName + "_" + clientid;
		Instant start = Instant.now();
		ThrottlerResponseDTO documentInjectionResponse = inputDocumentServicePort.addDocument(tableName, payload,
				loggersDTO);
		Instant end = Instant.now();
		Duration timeElapsed = Duration.between(start, end);
		String result = "Time taken: " + timeElapsed.toMillis() + " milliseconds";
		log.info(result);

		loggersDTO.setServicename(servicename);
		loggersDTO.setUsername(username);
		loggersDTO.setNameofmethod(nameofCurrMethod);
		loggersDTO.setTimestamp(utc.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

		documentInjectionThrottlerResponse.setResponseMessage(documentInjectionResponse.getResponseMessage());
		documentInjectionThrottlerResponse.setStatusCode(documentInjectionResponse.getStatusCode());

		if (documentInjectionThrottlerResponse.getStatusCode() == 200) {
			LoggerUtils.Printlogger(loggersDTO, false, false);
			return documentInjectionThrottlerResponse;
		} else {
			LoggerUtils.Printlogger(loggersDTO, false, false);
			throw new BadRequestOccurredException(400, ResponseMessages.BAD_REQUEST_MSG);
		}
	}

	// Rate Limiter(Throttler) FALLBACK method
	public ThrottlerResponseDTO documentInjectionRateLimiterFallback(String tableName, int clientid, String payload,
			RequestNotPermitted exception) {
		log.error("Max request rate limit fallback triggered. Exception: ", exception);

		// prepare Rate Limiting Response DTO
		ThrottlerResponseDTO rateLimitResponseDTO = throttlerServicePort.documentInjectionRateLimiter();
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.set("Retry-after:", rateLimitResponseDTO.getRequestTimeoutDuration());
		// retry the request after given timeoutDuration
		return rateLimitResponseDTO;
	}

}
