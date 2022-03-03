package com.searchservice.app.domain.service;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.OncePerRequestFilter;

//@Component
public class JwtTokenFilterService extends OncePerRequestFilter{
	
	private String realm_name;
	private String client_id;
	private String client_Secret;
	private RestTemplate restTemplate;
	private final Logger log = LoggerFactory.getLogger(JwtTokenFilterService.class);
	
	public JwtTokenFilterService() {
		super();
	}
	public JwtTokenFilterService(String realm_name, String client_id, String client_Secret, RestTemplate restTemplate) {
		super();
		this.realm_name = realm_name;
		this.client_id = client_id;
		this.client_Secret = client_Secret;
		this.restTemplate = restTemplate;
	}

	@Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
	 
      // Get authorization header and validate
        final String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        log.info("[JwtTokenFilterService][doFilterInternal] Authorization Header Value : "+header);
        if (null == header || header.isEmpty() || !header.startsWith("Bearer ")) {
             response.setStatus(HttpStatus.UNAUTHORIZED.value());
             response.getWriter().write("The token is not valid");
            return;
        }

      // Get jwt token and validate
        final String token = header.split(" ")[1].trim();
        log.info("[JwtTokenFilterService][doFilterInternal] Token Value : "+token);
        if (!validate(token)) {
        	response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write("The token is not valid");
            return;
        }else {
        	chain.doFilter(request, response);
        }
    }
	 
	 private boolean validate(String token) {
			
    	String url = "http://localhost:8080/auth/realms/"+realm_name+"/protocol/openid-connect/token/introspect";
    	
    	log.info("[JwtTokenFilterService][validate] Token Value : "+token);
    	log.info("[JwtTokenFilterService][validate] realm_name : "+realm_name);
    	log.info("[JwtTokenFilterService][validate] client_Secret : "+client_Secret);
    	log.info("[JwtTokenFilterService][validate] url : "+url);
    	
    // creating and setting the Header
    	HttpHeaders headers = new HttpHeaders();
    	headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    	
    // creating Body parameters	
    	MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
    	map.add("token", token);
    	map.add("client_id", client_id);
    	map.add("client_secret", client_Secret);
    	
    // Creating HttpEntity and set header and body
    	HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);
    
    // Consuming rest API
    	ResponseEntity<String> response = restTemplate.postForEntity( url, request , String.class );
    	log.info("[JwtTokenFilterService][validate] Final Response : "+response.getBody());
    	JSONObject obj = new JSONObject(response.getBody());
    	Boolean active = obj.getBoolean("active");
    	log.info("[JwtTokenFilterService][validate] active : "+active);
        return active;
    }
}