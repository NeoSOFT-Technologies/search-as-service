package com.searchservice.app.domain.port.api;

import com.searchservice.app.domain.dto.throttler.ThrottlerResponse;

public interface ThrottlerServicePort {
	// Rate limiter- throttling ports
	public ThrottlerResponse documentInjectionRateLimiter();

	// Max Request Size- throttling ports
	public ThrottlerResponse applyDocumentInjectionRequestSizeLimiter(
			ThrottlerResponse throttlerMaxRequestSizeResponseDTO);

	public ThrottlerResponse documentInjectionRequestSizeLimiter(String incomingData, boolean isNRT);

	public boolean isRequestSizeExceedingLimit(ThrottlerResponse throttlerMaxRequestSizeResponseDTO);
}
