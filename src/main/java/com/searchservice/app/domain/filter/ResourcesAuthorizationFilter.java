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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.searchservice.app.domain.service.security.KeycloakPermissionManagementService;

@Component
public class ResourcesAuthorizationFilter extends OncePerRequestFilter {

	private final Logger log = LoggerFactory.getLogger(ResourcesAuthorizationFilter.class);
	
	private ObjectMapper mapper = new ObjectMapper();
	@Autowired
	private KeycloakPermissionManagementService keycloakPermissionManagementService;
	
	public ResourcesAuthorizationFilter() {
		super();
	}

	public ResourcesAuthorizationFilter(KeycloakPermissionManagementService keycloakPermissionManagementService) {
		super();
		this.keycloakPermissionManagementService = keycloakPermissionManagementService;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {
		Map<String, Object> errorDetails = new HashMap<>();
		// Get authorization header and validate
		final String header = request.getHeader(HttpHeaders.AUTHORIZATION);
		log.info("[JwtTokenFilterService][doFilterInternal] Authorization Header Value : {}",header);
		if (null == header || header.isEmpty() || !header.startsWith("Bearer ")) {
			errorDetails.put("Unauthorized", "Access token not found");
			response.setStatus(HttpStatus.UNAUTHORIZED.value());
			response.setContentType(MediaType.APPLICATION_JSON_VALUE);
			mapper.writeValue(response.getWriter(), errorDetails);
			return;
		}

		// Get jwt token and validate user permissions
		final String token = header.split(" ")[1].trim();
		log.info("[JwtTokenFilterService][doFilterInternal] Token Value : {}",token);

		keycloakPermissionManagementService.validateAndSetActiveUserAuthorities(token);

		chain.doFilter(request, response);

	}

}