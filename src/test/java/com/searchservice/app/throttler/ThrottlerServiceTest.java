package com.searchservice.app.throttler;

import com.searchservice.app.domain.dto.throttler.ThrottlerResponseDTO;
import com.searchservice.app.domain.port.api.ThrottlerServicePort;
import com.searchservice.app.domain.service.ThrottlerService;
import com.searchservice.app.domain.utils.ThrottlerUtils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
//@ActiveProfiles("test")
@TestPropertySource(
        properties = {
                "throttler.maxRequestSizeLimiter.maxAllowedRequestSize: 0.06kB",
                "throttler.maxRequestSizeLimiter.maxAllowedRequestSize202: 10kB",
                "throttler.maxRequestSizeLimiter.maxAllowedRequestSize405: -1kB"
        }
)
class ThrottlerServiceTest {
	/*
	 * This is JUnit test case class
	 * 1. Rate Limiter Tests:
	 * >> Only one case considered, since the tested method's a simple service method 
	 * 	  with some independent logic inside
	 * 2. Max Request Size Limiter Tests:
	 * >> Two cases are considered here
	 * 		a. When Request Data size is accepted- 202
	 * 		b. When Request Data size is Not Allowed- 405
	 */
	Logger logger = LoggerFactory.getLogger(ThrottlerServiceTest.class);
    
	// Rate Limiter configuration values
	@Value("${resilience4j.ratelimiter.instances.documentInjectionRateLimitThrottler.limitForPeriod}")
    String maxRequestAllowedForCurrentWindow;
    @Value("${resilience4j.ratelimiter.instances.documentInjectionRateLimitThrottler.limitRefreshPeriod}")
    String currentRefreshWindow;
    private ThrottlerResponseDTO rateLimitResponseDTO;
    
    // Max request size limiter configuration values
    @Value("${throttler.maxRequestSizeLimiter.maxAllowedRequestSize202}")
    String maxAllowedRequestSize202;
    @Value("${throttler.maxRequestSizeLimiter.maxAllowedRequestSize405}")
    String maxAllowedRequestSize405;
    @Value("${throttler.maxRequestSizeLimiter.maxAllowedRequestSize}")
    String maxAllowedRequestSize;
    private String stringWithinMaxSizeLimit = "IAmIronMan";
    private String stringNotWithinMaxSizeLimit = "IAmNotIronMan";
    private ThrottlerResponseDTO maxReqSizeResponseDTO;
    private ThrottlerResponseDTO throttlerMaxReqSizeResponseDTO;
    
	@MockBean
	private ThrottlerServicePort throttlerServicePort;
	@MockBean
    ThrottlerUtils throttlerUtils;
	@InjectMocks
	private ThrottlerService throttlerService;
	
	@BeforeEach
	void setUp() throws Exception {
		// Set up Mockito response for Rate Limiter Tester
		rateLimitResponseDTO = new ThrottlerResponseDTO();
	    rateLimitResponseDTO.setResponseMessage(
		"Too many requests made! "
		+ "No further calls are accepted right now");
	    rateLimitResponseDTO.setStatusCode(429);
	    rateLimitResponseDTO.setMaxRequestsAllowed(maxRequestAllowedForCurrentWindow);
	    rateLimitResponseDTO.setCurrentRefreshWindow(currentRefreshWindow);
	}
	
	private void setUpMockitoAcceptanceResponseForReqSizeLimiter() {
	    // Set up Mockito Acceptance response for Max Request Size Limiter Tester
	    maxReqSizeResponseDTO = new ThrottlerResponseDTO();
	    maxReqSizeResponseDTO.setMaxAllowedRequestSize(maxAllowedRequestSize202);
	    maxReqSizeResponseDTO.setIncomingRequestSize("0.06kB");
	    maxReqSizeResponseDTO.setStatusCode(202);
	    maxReqSizeResponseDTO.setResponseMessage(
				"Incoming request size is under the limit, can be processed");
	    
	    throttlerMaxReqSizeResponseDTO = new ThrottlerResponseDTO();	    
    	double incomingRequestSizeInKBs = ThrottlerUtils.getSizeInkBs(stringWithinMaxSizeLimit);  	
    	throttlerMaxReqSizeResponseDTO.setIncomingRequestSize(""+incomingRequestSizeInKBs+"kB");  	
	    throttlerMaxReqSizeResponseDTO.setMaxAllowedRequestSize(maxAllowedRequestSize202);
	}
	
	private void setUpMockitoRejectionResponseForReqSizeLimiter() {
	    // Set up Mockito rejection response for Max Request Size Limiter Tester
	    maxReqSizeResponseDTO = new ThrottlerResponseDTO();
	    maxReqSizeResponseDTO.setMaxAllowedRequestSize(maxAllowedRequestSize405);
	    maxReqSizeResponseDTO.setIncomingRequestSize("0.066kB");
	    maxReqSizeResponseDTO.setStatusCode(405);
	    maxReqSizeResponseDTO.setResponseMessage(
				"Incoming request size exceeded the limit! "
				+ "This request can't be processed");
	    
	    throttlerMaxReqSizeResponseDTO = new ThrottlerResponseDTO();	
    	double incomingRequestSizeInKBs = ThrottlerUtils.getSizeInkBs(stringNotWithinMaxSizeLimit);
    	throttlerMaxReqSizeResponseDTO.setIncomingRequestSize(incomingRequestSizeInKBs+"kB");
	    throttlerMaxReqSizeResponseDTO.setMaxAllowedRequestSize(maxAllowedRequestSize405);
	}
	
	@Test
	@DisplayName("Testing Rate Limiter Throttler Service")
	void testDataInjectionRateLimiter() {
		logger.info("Solr data injection rate limiter test case getting executed..");
		ThrottlerResponseDTO receivedResponse;
		
		logger.debug("Expecting status code: {}", 429);
		when(throttlerServicePort.documentInjectionRateLimiter())
			.thenReturn(rateLimitResponseDTO);
		receivedResponse = throttlerService.documentInjectionRateLimiter();
		assertEquals(
				rateLimitResponseDTO.getStatusCode(), 
				receivedResponse.getStatusCode());
	}

	@Test
	@DisplayName("Testing Max Request Size Limiter Throttler Service")
	void testDataInjectionRequestSizeLimiter() {	
		logger.info("Solr data injection max request size limiter test case getting executed..");
		ThrottlerResponseDTO receivedMRSResponse;
		
		// When Request data size limit is not exceeded
		logger.debug("Expecting status code: {}", 202);
		setUpMockitoAcceptanceResponseForReqSizeLimiter();	
		when(throttlerServicePort.isRequestSizeExceedingLimit(Mockito.any()))
			.thenReturn(false);
		when(throttlerServicePort.documentInjectionRequestSizeLimiter("IAmIronMan", false))
			.thenReturn(maxReqSizeResponseDTO);		// isNRT >> false
		receivedMRSResponse = throttlerService.applyDocumentInjectionRequestSizeLimiter(throttlerMaxReqSizeResponseDTO);
		logger.debug("\nRec response: {}", receivedMRSResponse);
		assertEquals(
				maxReqSizeResponseDTO.getStatusCode(), 
				receivedMRSResponse.getStatusCode());
		
		// When Request data size limit exceeds
		logger.debug("Expecting status code: {}", 405);
		setUpMockitoRejectionResponseForReqSizeLimiter();
		when(throttlerServicePort.isRequestSizeExceedingLimit(Mockito.any()))
		.thenReturn(true);
		when(throttlerServicePort.documentInjectionRequestSizeLimiter("IAmNotIronMan", false))
			.thenReturn(maxReqSizeResponseDTO);		// isNRT >> false
		receivedMRSResponse = throttlerService.applyDocumentInjectionRequestSizeLimiter(throttlerMaxReqSizeResponseDTO);
		logger.debug("\nRec response: {}", receivedMRSResponse);
		assertEquals(
				maxReqSizeResponseDTO.getStatusCode(), 
				receivedMRSResponse.getStatusCode());
	}
}
