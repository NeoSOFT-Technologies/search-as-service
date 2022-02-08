package com.searchservice.app.domain.dto.logger;

import java.util.UUID;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Before;
import org.slf4j.MDC;

public class CorrelationID {
	
	public static final String CORRELATION_ID_HEADER_NAME = "X-Correlation-Id";
    private static final String CORRELATION_ID_LOG_VAR_NAME = "correlationId";

    @Before(value = "execution")
    public void before(JoinPoint joinPoint) {
        final String correlationId = generateUniqueCorrelationId();
        MDC.put(CORRELATION_ID_LOG_VAR_NAME, correlationId);
    }

    @After(value = "execution")
    public void afterReturning(JoinPoint joinPoint) {
        MDC.remove(CORRELATION_ID_LOG_VAR_NAME);
    }
    
    public String generateUniqueCorrelationId() {
        return UUID.randomUUID().toString();
    }

}
