package com.neosoft.app.domain.port.api;

import com.neosoft.app.domain.dto.throttler.ThrottlerResponse;

public interface ThrottlerServicePort {
	// Rate limiter- throttling ports
	public ThrottlerResponse defaultRateLimiter();

	// Max Request Size- throttling ports
	public ThrottlerResponse applyDefaultRequestSizeLimiter(
			ThrottlerResponse throttlerMaxRequestSizeResponseDTO);

	public ThrottlerResponse defaultRequestSizeLimiter(String incomingData, boolean isNRT);

	public boolean isRequestSizeExceedingLimit(ThrottlerResponse throttlerMaxRequestSizeResponseDTO);
}
