package com.solr.clientwrapper.domain.service.throttler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ThrottlerService {
    public ResponseEntity<String> rateLimiterFallback(Exception e){
        return new ResponseEntity<>("Throttle service restricts further calls", HttpStatus.TOO_MANY_REQUESTS);
    }
}
