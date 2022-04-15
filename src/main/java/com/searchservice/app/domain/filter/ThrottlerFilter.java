/*
 * package com.searchservice.app.domain.filter;
 * 
 * import java.io.IOException; import java.util.stream.Collectors;
 * 
 * import javax.servlet.Filter; import javax.servlet.FilterChain; import
 * javax.servlet.ServletException; import javax.servlet.ServletRequest; import
 * javax.servlet.ServletResponse; import javax.servlet.http.HttpServletRequest;
 * import javax.servlet.http.HttpServletResponse;
 * 
 * import org.springframework.beans.factory.annotation.Autowired; import
 * org.springframework.http.HttpStatus; import
 * org.springframework.http.ResponseEntity; import
 * org.springframework.stereotype.Component; import
 * org.springframework.web.util.ContentCachingRequestWrapper;
 * 
 * import com.searchservice.app.domain.dto.throttler.ThrottlerResponse; import
 * com.searchservice.app.domain.port.api.ThrottlerServicePort; import
 * com.searchservice.app.domain.service.InputDocumentService; import
 * com.searchservice.app.domain.service.ThrottlerService; import
 * com.searchservice.app.rest.errors.BadRequestOccurredException;
 * 
 * @Component public class ThrottlerFilter implements Filter {
 * 
 * @Autowired public ThrottlerService throttlerServicePort;
 * 
 * // public ThrottlerFilter(ThrottlerServicePort throttlerServicePort) { //
 * this.throttlerServicePort=throttlerServicePort; // }
 * 
 * @Override public void doFilter(ServletRequest request, ServletResponse
 * response, FilterChain chain) throws IOException, ServletException {
 * HttpServletRequest req = (HttpServletRequest) request; HttpServletResponse
 * res = (HttpServletResponse) response;
 * 
 * // System.out.println("My Request URI is: " + req.getRequestURI());
 * //chain.doFilter(request, response);
 * 
 * // Apply RequestSizeLimiting Throttler on payload before serving the request
 * 
 * // testing String payload = ""; if ("POST".equalsIgnoreCase(req.getMethod()))
 * { ContentCachingRequestWrapper req2 = new ContentCachingRequestWrapper(req);
 * payload =
 * req2.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
 * }
 * 
 * String test =""; // payload = (String) req.getParameter("payload");
 * 
 * System.out.println("payload >>> "+payload);
 * System.out.println("test >>> "+test);
 * System.out.println("throttlerServicePort >>> "+throttlerServicePort);
 * 
 * ThrottlerResponse documentInjectionThrottlerResponse = throttlerServicePort
 * .documentInjectionRequestSizeLimiter(payload, true);
 * 
 * System.out.println("documentInjectionThrottlerResponse >>>>> "
 * +documentInjectionThrottlerResponse);
 * 
 * if (documentInjectionThrottlerResponse.getStatusCode() == 406) throw new
 * BadRequestOccurredException(406,
 * "Request not allowed. Request size exceeded the limit"); //return
 * ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(
 * documentInjectionThrottlerResponse); else {
 * System.out.println("Going to chain filter ......."); chain.doFilter(request,
 * response); }
 * 
 * 
 * System.out.println("My Response Status Code is: " + res.getStatus());
 * 
 * }
 * 
 * }
 */