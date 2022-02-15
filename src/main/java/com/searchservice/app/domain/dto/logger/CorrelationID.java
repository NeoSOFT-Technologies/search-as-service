package com.searchservice.app.domain.dto.logger;

import java.util.UUID;

public class CorrelationID {
    
    public String generateUniqueCorrelationId() {
        return UUID.randomUUID().toString();
    }

}
