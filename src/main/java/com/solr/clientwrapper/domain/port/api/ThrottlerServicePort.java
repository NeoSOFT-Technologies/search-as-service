package com.solr.clientwrapper.domain.port.api;

import com.solr.clientwrapper.domain.dto.throttler.ThrottlerRateLimitResponseDTO;

public interface ThrottlerServicePort {
	// Rate limiter ports
	public ThrottlerRateLimitResponseDTO dataInjectionRateLimiter();
}
