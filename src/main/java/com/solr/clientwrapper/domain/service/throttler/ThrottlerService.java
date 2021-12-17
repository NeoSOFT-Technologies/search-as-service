package com.solr.clientwrapper.domain.service.throttler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Retry-after:", requestRetryWindow+"s"); // retry the request after one second

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

}
