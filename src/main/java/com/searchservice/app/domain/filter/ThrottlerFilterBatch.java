package com.searchservice.app.domain.filter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.searchservice.app.domain.dto.throttler.ThrottlerResponse;
import com.searchservice.app.domain.service.ThrottlerService;

@Component
@Order(3)
public class ThrottlerFilterBatch implements Filter {

	@Autowired
	public ThrottlerService throttlerServicePort;
	
	private ObjectMapper mapper = new ObjectMapper();

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
		
        // Make request to be ready to be read multiple times
        request = new RequestWrapper(req);
		
        // testing
        String payload = "";
        if ("POST".equalsIgnoreCase(req.getMethod())) 
        {
        	payload = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        }

        ThrottlerResponse documentInjectionThrottlerResponse = throttlerServicePort
				.documentInjectionRequestSizeLimiter(payload, false);
        Map<String, Object> errorDetails = new HashMap<>();
		if (documentInjectionThrottlerResponse.getStatusCode() == 406) {
			errorDetails.put("Request not allowed", "Request size exceeded the limit");
			res.setStatus(HttpStatus.FORBIDDEN.value());
			response.setContentType(MediaType.APPLICATION_JSON_VALUE);
			mapper.writeValue(response.getWriter(), errorDetails);
			  
			//throw new BadRequestOccurredException(406, "Request not allowed. Request size exceeded the limit");
			//return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(documentInjectionThrottlerResponse);
		}
		
		chain.doFilter(request, response);
	}
	
	
}
