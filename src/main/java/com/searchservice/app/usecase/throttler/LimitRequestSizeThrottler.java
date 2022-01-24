package com.searchservice.app.usecase.throttler;


import com.searchservice.app.domain.dto.throttler.ThrottlerMaxRequestSizeResponseDTO;
import com.searchservice.app.domain.port.api.ThrottlerServicePort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LimitRequestSizeThrottler {
	private final Logger logger = LoggerFactory.getLogger(LimitRequestSizeThrottler.class);
	private final ThrottlerServicePort throttlerServicePort;
	public LimitRequestSizeThrottler(ThrottlerServicePort throttlerServicePort) {
		this.throttlerServicePort = throttlerServicePort;
	}
	
	public ThrottlerMaxRequestSizeResponseDTO applyDataInjectionRequestSizeLimiter(
			ThrottlerMaxRequestSizeResponseDTO throttlerMaxRequestSizeResponseDTO) {
		logger.info("apply max request size limit");

		return throttlerServicePort.applyDataInjectionRequestSizeLimiter(
				throttlerMaxRequestSizeResponseDTO);
	}
	
	public ThrottlerMaxRequestSizeResponseDTO dataInjectionRequestSizeLimiter(
			String incomingRequestData) {
		logger.info("apply max request size limit");

		return throttlerServicePort.dataInjectionRequestSizeLimiter(
				incomingRequestData);
	}
}
