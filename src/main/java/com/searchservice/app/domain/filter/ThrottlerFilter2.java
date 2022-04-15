//package com.searchservice.app.domain.filter;
//
//import java.io.IOException;
//import java.util.stream.Collectors;
//
//import javax.servlet.Filter;
//import javax.servlet.FilterChain;
//import javax.servlet.ServletException;
//import javax.servlet.ServletRequest;
//import javax.servlet.ServletResponse;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.core.annotation.Order;
//import org.springframework.stereotype.Component;
//
//import com.searchservice.app.domain.dto.throttler.ThrottlerResponse;
//import com.searchservice.app.domain.service.ThrottlerService;
//import com.searchservice.app.rest.errors.BadRequestOccurredException;
//
//@Component
//@Order(3)
//public class ThrottlerFilter2 implements Filter {
//
//	@Autowired
//	public ThrottlerService throttlerServicePort;
//
//	@Override
//	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
//			throws IOException, ServletException {
//		
//        HttpServletRequest req = (HttpServletRequest) request;
//        HttpServletResponse res = (HttpServletResponse) response;
//		
//        // Make request to be ready to be read multiple times
//        request = new RequestWrapper(req);
//        
//		System.out.println("Ingest filter @@@@@@@@@@@");
//		
//        // testing
//        String payload = "";
//        if ("POST".equalsIgnoreCase(req.getMethod())) 
//        {
//        	payload = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
//        }
//        
//        System.out.println("throttlerServicePort >>>>> "+throttlerServicePort);
//        
//        ThrottlerResponse documentInjectionThrottlerResponse = throttlerServicePort
//				.documentInjectionRequestSizeLimiter(payload, false);
//		if (documentInjectionThrottlerResponse.getStatusCode() == 406)
//			throw new BadRequestOccurredException(406, "Request not allowed. Request size exceeded the limit");
//			//return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(documentInjectionThrottlerResponse);
//		else
//			System.out.println("Req size is under limit!!");
//		
//		
//		chain.doFilter(request, response);
//		System.out.println("Filter chaining done##");
//		
//	}
//	
//	
//}
