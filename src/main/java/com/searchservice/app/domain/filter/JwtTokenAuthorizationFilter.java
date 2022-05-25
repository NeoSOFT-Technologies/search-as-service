package com.searchservice.app.domain.filter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.searchservice.app.config.AuthConfigProperties;
import com.searchservice.app.domain.service.PublicKeyService;
import com.searchservice.app.domain.utils.security.SecurityUtil;

public class JwtTokenAuthorizationFilter extends OncePerRequestFilter {

	private AuthConfigProperties authConfigProperties;
	private ObjectMapper mapper = new ObjectMapper();
	private PublicKeyService publicKeyService;
	
	private final Logger log = LoggerFactory.getLogger(JwtTokenAuthorizationFilter.class);
	
	public JwtTokenAuthorizationFilter() {
		super();
	}

	public JwtTokenAuthorizationFilter(AuthConfigProperties authConfigProperties,PublicKeyService publicKeyService) {
		super();
		this.authConfigProperties = authConfigProperties;
		this.publicKeyService = publicKeyService;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {
		
		Map<String, Object> errorDetails = new HashMap<>();
		
		// Get authorization header and validate
		final String token = SecurityUtil.getTokenFromRequestHeader(request, response, mapper);
		log.info("[JwtTokenFilterService][doFilterInternal] Token Value : {}", token);

		if (!SecurityUtil.validate(token, publicKeyService.retrievePublicKey(authConfigProperties.getRealmName()))) {
			errorDetails.put("Unauthorized", "Invalid token");
			response.setStatus(HttpStatus.FORBIDDEN.value());
			response.setContentType(MediaType.APPLICATION_JSON_VALUE);
			mapper.writeValue(response.getWriter(), errorDetails);

		} else {
			chain.doFilter(request, response);
		}
	}

}
