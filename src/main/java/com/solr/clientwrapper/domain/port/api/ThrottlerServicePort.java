package com.solr.clientwrapper.domain.port.api;

import com.solr.clientwrapper.domain.dto.throttler.ThrottlerMaxRequestSizeResponseDTO;
import com.solr.clientwrapper.domain.dto.throttler.ThrottlerRateLimitResponseDTO;

public interface ThrottlerServicePort {
	// Rate limiter- throttling ports
	public ThrottlerRateLimitResponseDTO dataInjectionRateLimiter();
	// Max Request Size- throttling ports
	public ThrottlerMaxRequestSizeResponseDTO dataInjectionRequestSizeLimiter(
			ThrottlerMaxRequestSizeResponseDTO throttlerMaxRequestSizeResponseDTO);
}
