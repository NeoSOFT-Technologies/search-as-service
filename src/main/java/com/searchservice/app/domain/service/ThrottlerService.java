package com.searchservice.app.domain.service;


import com.searchservice.app.domain.dto.throttler.ThrottlerMaxRequestSizeResponseDTO;
import com.searchservice.app.domain.dto.throttler.ThrottlerRateLimitResponseDTO;
import com.searchservice.app.domain.port.api.ThrottlerServicePort;
import com.searchservice.app.domain.utils.ThrottlerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ThrottlerService implements ThrottlerServicePort {
	private final Logger logger = LoggerFactory.getLogger(ThrottlerService.class);
    @Value("${base-solr-url}")
	String baseSolrUrl;
    
    // Rate Limiter configuration values
    @Value("${resilience4j.ratelimiter.instances.solrDataInjectionRateLimitThrottler.limitForPeriod}")
    String maxRequestAllowedForCurrentWindow;
    @Value("${resilience4j.ratelimiter.instances.solrDataInjectionRateLimitThrottler.limitRefreshPeriod}")
    String currentRefreshWindow;
    @Value("${resilience4j.ratelimiter.instances.solrDataInjectionRateLimitThrottler.timeoutDuration}")
    String requestRetryWindow;
    // Max request size configuration values
    @Value("${resilience4j.maxRequestSize.maxAllowedRequestSize}")
    String maxAllowedRequestSize;
    
	@Override
	public ThrottlerRateLimitResponseDTO dataInjectionRateLimiter() {
        logger.info("Max request limit is applied, no further calls are accepted");

        // prepare Rate Limiting Response DTO
        ThrottlerRateLimitResponseDTO rateLimitResponseDTO = new ThrottlerRateLimitResponseDTO();
        rateLimitResponseDTO.setResponseMsg(
        		"Too many requests made! "
        		+ "No further calls are accepted right now");
        rateLimitResponseDTO.setStatusCode(429);
        rateLimitResponseDTO.setMaxRequestsAllowed(maxRequestAllowedForCurrentWindow);
        rateLimitResponseDTO.setCurrentRefreshWindow(currentRefreshWindow);
        rateLimitResponseDTO.setRequestTimeoutDuration(requestRetryWindow);
        
        return rateLimitResponseDTO;
	}

	@Override
	public ThrottlerMaxRequestSizeResponseDTO applyDataInjectionRequestSizeLimiter(
			ThrottlerMaxRequestSizeResponseDTO throttlerMaxRequestSizeResponseDTO) {
		/*
		 * This method can apply Request Size Limiter Filter
		 * accepting the ThrottlerMaxRequestSizeResponseDTO as argument  
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
	public ThrottlerMaxRequestSizeResponseDTO dataInjectionRequestSizeLimiter(String incomingData) {
		/*
		 * This method can apply Request Size Limiter Filter
		 * accepting the raw incoming data in form of string as argument  
		 */
		logger.info("Max request size limiter is under process...");
		
    	double incomingRequestSizeInKBs = ThrottlerUtils.getSizeInkBs(incomingData);
    	ThrottlerMaxRequestSizeResponseDTO throttlerMaxRequestSizeResponseDTO
    		= new ThrottlerMaxRequestSizeResponseDTO();
    	throttlerMaxRequestSizeResponseDTO.setIncomingRequestSize(incomingRequestSizeInKBs+"kB");
    	
    	// Max Request Size Limiter Logic
		throttlerMaxRequestSizeResponseDTO.setMaxAllowedRequestSize(maxAllowedRequestSize);
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
	public boolean isRequestSizeExceedingLimit(ThrottlerMaxRequestSizeResponseDTO throttlerMaxRequestSizeResponseDTO) {
		return (ThrottlerUtils.formatRequestSizeStringToDouble(throttlerMaxRequestSizeResponseDTO.getIncomingRequestSize())
				> ThrottlerUtils.formatRequestSizeStringToDouble(throttlerMaxRequestSizeResponseDTO.getMaxAllowedRequestSize()));
	}
}
