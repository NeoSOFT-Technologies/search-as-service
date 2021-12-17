package com.solr.clientwrapper.domain.service.throttler;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.solr.clientwrapper.domain.dto.throttler.ThrottlerRateLimitResponseDTO;
import com.solr.clientwrapper.domain.port.api.ThrottlerServicePort;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
class ThrottlerServiceTest {
	/*
	 * This is JUnit test case class
	 * >> Only one case considered, since the tested method's a simple service method 
	 * 	  with some independent logic inside
	 */
	Logger logger = LoggerFactory.getLogger(ThrottlerServiceTest.class);
    @Value("${resilience4j.ratelimiter.instances.solrDataInjectionRateLimitThrottler.limitForPeriod}")
    String maxRequestAllowedForCurrentWindow;
    @Value("${resilience4j.ratelimiter.instances.solrDataInjectionRateLimitThrottler.limitRefreshPeriod}")
    String currentRefreshWindow;
	
    private ThrottlerRateLimitResponseDTO rateLimitResponseDTO;
	@MockBean
	private ThrottlerServicePort throttlerServicePort;
	@InjectMocks
	private ThrottlerService throttlerService;
	
	@BeforeEach
	void setUp() throws Exception {
		rateLimitResponseDTO = new ThrottlerRateLimitResponseDTO();
	    rateLimitResponseDTO.setResponseMsg(
		"Too many requests made! "
		+ "No further calls are accepted right now");
	    rateLimitResponseDTO.setStatusCode(429);
	    rateLimitResponseDTO.setMaxRequestsAllowed(maxRequestAllowedForCurrentWindow);
	    rateLimitResponseDTO.setCurrentRefreshWindow(currentRefreshWindow);
	}
	
	@Test
	@DisplayName("Testing Rate Limiter Throttler Service")
	void testDataInjectionRateLimiter() {
		logger.info("Solr data injection rate limiter test case getting executed..");
		ThrottlerRateLimitResponseDTO receivedResponse;
		
		logger.debug("Expecting status code: {}", 429);
		when(throttlerServicePort.dataInjectionRateLimiter())
			.thenReturn(rateLimitResponseDTO);
		receivedResponse = throttlerService.dataInjectionRateLimiter();
		assertEquals(
				rateLimitResponseDTO.getStatusCode(), 
				receivedResponse.getStatusCode());
	}
}
