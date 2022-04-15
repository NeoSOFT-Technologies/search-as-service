
package com.searchservice.app.domain.filter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.searchservice.app.domain.dto.throttler.ThrottlerResponse;
import com.searchservice.app.domain.service.ThrottlerService;
import com.searchservice.app.rest.errors.BadRequestOccurredException;

@Component
@Order(2)
public class ThrottlerFilter1 extends OncePerRequestFilter {

	private ObjectMapper mapper = new ObjectMapper();

	@Autowired
	public ThrottlerService throttlerServicePort;

	@Override protected void doFilterInternal(HttpServletRequest request,
  HttpServletResponse response, FilterChain filterChain) throws
  ServletException, IOException {
  
  // System.out.println("My Request URI is: " + request.getRequestURI());
  //chain.doFilter(request, response);
  
  // Apply RequestSizeLimiting Throttler on payload before serving the request
  
  // testing
  String payload = "";
  if ("POST".equalsIgnoreCase(request.getMethod())) {
	  ContentCachingRequestWrapper req2 = new ContentCachingRequestWrapper(request);
	  payload = req2.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
  }
  
  String test = "";
  
  System.out.println("TF1 payload >>> " + payload);
  System.out.println("TF1 test >>> " + test);
  System.out.println("TF1 throttlerServicePort >>> " + throttlerServicePort);
  
  ThrottlerResponse documentInjectionThrottlerResponse = throttlerServicePort
  .documentInjectionRequestSizeLimiter(payload, true);
  
  System.out.println("TF1 documentInjectionThrottlerResponse >>>>> " + documentInjectionThrottlerResponse);
  
  Map<String, Object> errorDetails = new HashMap<>();
  if (documentInjectionThrottlerResponse.getStatusCode() == 406) {
  errorDetails.put("Request not allowed", "Request size exceeded the limit");
  response.setStatus(HttpStatus.FORBIDDEN.value());
  response.setContentType(MediaType.APPLICATION_JSON_VALUE);
  mapper.writeValue(response.getWriter(), errorDetails);
  
  // return;
  //throw new BadRequestOccurredException(406, "Request not allowed. Request size exceeded the limit");
  }
  
  // return
  // ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(documentInjectionThrottlerResponse); else {
  System.out.println("TF1 Going to chain filter .......");
  
  
  filterChain.doFilter(request, response); }
  
  System.out.println("TF1 My Response Status Code is: " + response.getStatus());
  
  }

}
