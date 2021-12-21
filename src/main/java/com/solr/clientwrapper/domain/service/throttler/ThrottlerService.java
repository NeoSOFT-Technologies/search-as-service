package com.solr.clientwrapper.domain.service.throttler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.solr.clientwrapper.domain.dto.throttler.ThrottlerMaxRequestSizeResponseDTO;
import com.solr.clientwrapper.domain.dto.throttler.ThrottlerRateLimitResponseDTO;
import com.solr.clientwrapper.domain.port.api.ThrottlerServicePort;

@Service
@Transactional
public class ThrottlerService implements ThrottlerServicePort {
	private final Logger logger = LoggerFactory.getLogger(ThrottlerService.class);
    @Value("${base-solr-url}")
	String baseSolrUrl;
    @Value("${resilience4j.ratelimiter.instances.solrDataInjectionRateLimitThrottler.limitForPeriod}")
    String maxRequestAllowedForCurrentWindow;
    @Value("${resilience4j.ratelimiter.instances.solrDataInjectionRateLimitThrottler.limitRefreshPeriod}")
    String currentRefreshWindow;
    @Value("${resilience4j.ratelimiter.instances.solrDataInjectionRateLimitThrottler.timeoutDuration}")
    String requestRetryWindow;
    
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
	public ThrottlerMaxRequestSizeResponseDTO dataInjectionRequestSizeLimiter(
			ThrottlerMaxRequestSizeResponseDTO throttlerMaxRequestSizeResponseDTO) {
		logger.info("Max request size limiter is under process...");
		
		if(throttlerMaxRequestSizeResponseDTO.getIncomingRequestSize()
				> throttlerMaxRequestSizeResponseDTO.getMaxAllowedRequestSize()) {
			throttlerMaxRequestSizeResponseDTO.setStatusCode(429);
			throttlerMaxRequestSizeResponseDTO.setResponseMessage(
					"Incoming request size exceeded the limit! "
					+ "This request can't be processed");
		} else {
			throttlerMaxRequestSizeResponseDTO.setResponseMessage(
					"Incoming request size is under the limit, can be processed");
		}
		logger.info("Max request size limiting has been applied");
		return throttlerMaxRequestSizeResponseDTO;
	}

}
