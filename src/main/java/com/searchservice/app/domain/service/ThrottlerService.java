package com.searchservice.app.domain.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.searchservice.app.domain.dto.throttler.ThrottlerResponse;
import com.searchservice.app.domain.port.api.ThrottlerServicePort;
import com.searchservice.app.domain.utils.HttpStatusCode;
import com.searchservice.app.domain.utils.ThrottlerUtils;

@Service
public class ThrottlerService implements ThrottlerServicePort {
	private final Logger logger = LoggerFactory.getLogger(ThrottlerService.class);

	// Rate Limiter configuration values
	@Value("${resilience4j.ratelimiter.instances.documentInjectionRateLimitThrottler.limitForPeriod}")
	String maxRequestAllowedForCurrentWindow;
	@Value("${resilience4j.ratelimiter.instances.documentInjectionRateLimitThrottler.limitRefreshPeriod}")
	String currentRefreshWindow;
	@Value("${resilience4j.ratelimiter.instances.documentInjectionRateLimitThrottler.timeoutDuration}")
	String requestRetryWindow;

	// Max request size configuration values
	@Value("${throttler.maxRequestSizeLimiter.maxAllowedRequestSizeNRT}")
	String maxAllowedRequestSizeNRT;
	@Value("${throttler.maxRequestSizeLimiter.maxAllowedRequestSizeBatch}")
	String maxAllowedRequestSizeBatch;

	@Override
	public ThrottlerResponse documentInjectionRateLimiter() {

		// prepare Rate Limiting Response DTO
		ThrottlerResponse rateLimitResponseDTO = new ThrottlerResponse();
		rateLimitResponseDTO.setMessage("Too many requests made! " + "No further calls are accepted right now");
		rateLimitResponseDTO.setStatusCode(429);
		rateLimitResponseDTO.setMaxRequestsAllowed(maxRequestAllowedForCurrentWindow);
		rateLimitResponseDTO.setCurrentRefreshWindow(currentRefreshWindow);
		rateLimitResponseDTO.setRequestTimeoutDuration(requestRetryWindow);

		return rateLimitResponseDTO;
	}

	@Override
	public ThrottlerResponse applyDocumentInjectionRequestSizeLimiter(
			ThrottlerResponse throttlerMaxRequestSizeResponseDTO) {
		/*
		 * This method can apply Request Size Limiter Filter accepting the
		 * ThrottlerResponseDTO as argument
		 */

		if (isRequestSizeExceedingLimit(throttlerMaxRequestSizeResponseDTO)) {
			throttlerMaxRequestSizeResponseDTO.setStatusCode(HttpStatusCode.OPERATION_NOT_ALLOWED.getCode());
			throttlerMaxRequestSizeResponseDTO
					.setMessage("Incoming request size exceeded the limit! " + "This request can't be processed");
		} else {
			throttlerMaxRequestSizeResponseDTO.setStatusCode(HttpStatusCode.PROCESSING_NOT_COMPLETED.getCode());
			throttlerMaxRequestSizeResponseDTO.setMessage("Incoming request size is under the limit, can be processed");
		}
		logger.info("Max request size limiting has been applied");
		return throttlerMaxRequestSizeResponseDTO;
	}

	@Override
	public ThrottlerResponse documentInjectionRequestSizeLimiter(String incomingData, boolean isNRT) {
		/*
		 * This method can apply Request Size Limiter Filter accepting the raw incoming
		 * data in form of string as argument
		 */
		logger.info("Max request size limiter is under process...");

		double incomingRequestSizeInKBs = ThrottlerUtils.getSizeInkBs(incomingData);

		ThrottlerResponse throttlerMaxRequestSizeResponseDTO = new ThrottlerResponse();
		throttlerMaxRequestSizeResponseDTO.setIncomingRequestSize(incomingRequestSizeInKBs + "kB");

		// Max Request Size Limiter Logic
		if (isNRT)
			throttlerMaxRequestSizeResponseDTO.setMaxAllowedRequestSize(maxAllowedRequestSizeNRT);
		else
			throttlerMaxRequestSizeResponseDTO.setMaxAllowedRequestSize(maxAllowedRequestSizeBatch);

		if (isRequestSizeExceedingLimit(throttlerMaxRequestSizeResponseDTO)) {
			throttlerMaxRequestSizeResponseDTO.setStatusCode(HttpStatusCode.NOT_ACCEPTABLE_ERROR.getCode());
			throttlerMaxRequestSizeResponseDTO
					.setMessage("Incoming request size exceeded the limit! " + "This request can't be processed");
		} else {
			throttlerMaxRequestSizeResponseDTO.setStatusCode(HttpStatusCode.PROCESSING_NOT_COMPLETED.getCode());
			throttlerMaxRequestSizeResponseDTO.setMessage("Incoming request size is under the limit, can be processed");
		}
		logger.info("Max request size limiting has been applied");
		return throttlerMaxRequestSizeResponseDTO;
	}

	@Override
	public boolean isRequestSizeExceedingLimit(ThrottlerResponse throttlerMaxRequestSizeResponseDTO) {
		return (ThrottlerUtils.formatRequestSizeStringToDouble(
				throttlerMaxRequestSizeResponseDTO.getIncomingRequestSize()) > ThrottlerUtils
						.formatRequestSizeStringToDouble(
								throttlerMaxRequestSizeResponseDTO.getMaxAllowedRequestSize()));
	}
}
