package com.searchservice.app.domain.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.searchservice.app.domain.dto.throttler.ThrottlerResponseDTO;
import com.searchservice.app.domain.port.api.ThrottlerServicePort;
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
	public ThrottlerResponseDTO documentInjectionRateLimiter() {
        logger.info("Max request rate limit is applied, no further calls are accepted");

        // prepare Rate Limiting Response DTO
        ThrottlerResponseDTO rateLimitResponseDTO = new ThrottlerResponseDTO();
        rateLimitResponseDTO.setResponseMessage(
        		"Too many requests made! "
        		+ "No further calls are accepted right now");
        rateLimitResponseDTO.setStatusCode(429);
        rateLimitResponseDTO.setMaxRequestsAllowed(maxRequestAllowedForCurrentWindow);
        rateLimitResponseDTO.setCurrentRefreshWindow(currentRefreshWindow);
        rateLimitResponseDTO.setRequestTimeoutDuration(requestRetryWindow);
        
        return rateLimitResponseDTO;
	}

	@Override
	public ThrottlerResponseDTO applyDocumentInjectionRequestSizeLimiter(
			ThrottlerResponseDTO throttlerMaxRequestSizeResponseDTO) {
		/*
		 * This method can apply Request Size Limiter Filter
		 * accepting the ThrottlerResponseDTO as argument  
		 */
		logger.info("Max request size limiter is under process...");
		
		if(isRequestSizeExceedingLimit(throttlerMaxRequestSizeResponseDTO)) {
			throttlerMaxRequestSizeResponseDTO.setStatusCode(405);
			throttlerMaxRequestSizeResponseDTO.setResponseMessage(
					"Incoming request size exceeded the limit! "
					+ "This request can't be processed");
		} else {
			throttlerMaxRequestSizeResponseDTO.setStatusCode(202);
			throttlerMaxRequestSizeResponseDTO.setResponseMessage(
					"Incoming request size is under the limit, can be processed");
		}
		logger.info("Max request size limiting has been applied");
		return throttlerMaxRequestSizeResponseDTO;
	}
	
	@Override
	public ThrottlerResponseDTO documentInjectionRequestSizeLimiter(
			String incomingData, 
			boolean isNRT) {
		/*
		 * This method can apply Request Size Limiter Filter
		 * accepting the raw incoming data in form of string as argument  
		 */
		logger.info("Max request size limiter is under process...");
		
    	double incomingRequestSizeInKBs = ThrottlerUtils.getSizeInkBs(incomingData);
    	
    	ThrottlerResponseDTO throttlerMaxRequestSizeResponseDTO
    		= new ThrottlerResponseDTO();
    	throttlerMaxRequestSizeResponseDTO.setIncomingRequestSize(incomingRequestSizeInKBs+"kB");
    	
    	// Max Request Size Limiter Logic
    	if(isNRT)
    		throttlerMaxRequestSizeResponseDTO.setMaxAllowedRequestSize(maxAllowedRequestSizeNRT);
    	else
    		throttlerMaxRequestSizeResponseDTO.setMaxAllowedRequestSize(maxAllowedRequestSizeBatch);
		if(isRequestSizeExceedingLimit(throttlerMaxRequestSizeResponseDTO)) {
			throttlerMaxRequestSizeResponseDTO.setStatusCode(406);
			throttlerMaxRequestSizeResponseDTO.setResponseMessage(
					"Incoming request size exceeded the limit! "
					+ "This request can't be processed");
		} else {
			throttlerMaxRequestSizeResponseDTO.setStatusCode(202);
			throttlerMaxRequestSizeResponseDTO.setResponseMessage(
					"Incoming request size is under the limit, can be processed");
		}
		logger.info("Max request size limiting has been applied");
		return throttlerMaxRequestSizeResponseDTO;
	}

	@Override
	public boolean isRequestSizeExceedingLimit(ThrottlerResponseDTO throttlerMaxRequestSizeResponseDTO) {
		return (ThrottlerUtils.formatRequestSizeStringToDouble(throttlerMaxRequestSizeResponseDTO.getIncomingRequestSize())
				> ThrottlerUtils.formatRequestSizeStringToDouble(throttlerMaxRequestSizeResponseDTO.getMaxAllowedRequestSize()));
	}
}
