package com.solr.clientwrapper.usecase.throttler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.solr.clientwrapper.domain.dto.throttler.ThrottlerRateLimitResponseDTO;
import com.solr.clientwrapper.domain.port.api.ThrottlerServicePort;

@Service
@Transactional
public class LimitRateThrottler {
	private final Logger logger = LoggerFactory.getLogger(LimitRateThrottler.class);
	private final ThrottlerServicePort throttlerServicePort;
	public LimitRateThrottler(ThrottlerServicePort throttlerServicePort) {
		this.throttlerServicePort = throttlerServicePort;
	}
	
	public ThrottlerRateLimitResponseDTO dataInjectionRateLimiter() {
		logger.info("apply rate limit");
		return throttlerServicePort.dataInjectionRateLimiter();
	}
}
