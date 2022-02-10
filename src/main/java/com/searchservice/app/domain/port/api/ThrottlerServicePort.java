package com.searchservice.app.domain.port.api;


import com.searchservice.app.domain.dto.throttler.ThrottlerResponseDTO;

public interface ThrottlerServicePort {
	// Rate limiter- throttling ports
	public ThrottlerResponseDTO documentInjectionRateLimiter();
	
	// Max Request Size- throttling ports
	public ThrottlerResponseDTO applyDocumentInjectionRequestSizeLimiter(
			ThrottlerResponseDTO throttlerMaxRequestSizeResponseDTO);
	public ThrottlerResponseDTO documentInjectionRequestSizeLimiter(
			String incomingData, 
			boolean isNRT);
	public boolean isRequestSizeExceedingLimit(
			ThrottlerResponseDTO throttlerMaxRequestSizeResponseDTO);
}
