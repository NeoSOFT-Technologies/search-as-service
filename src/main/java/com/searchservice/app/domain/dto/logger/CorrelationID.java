package com.searchservice.app.domain.dto.logger;

import java.util.UUID;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Before;
import org.slf4j.MDC;

public class CorrelationID {
    
    public String generateUniqueCorrelationId() {
        return UUID.randomUUID().toString();
    }

}
