package com.searchservice.app.domain.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.searchservice.app.domain.service.security.KeycloakPermissionManagementService;
import com.searchservice.app.domain.utils.security.SecurityUtil;

@Component
public class ResourcesAuthorizationFilter extends OncePerRequestFilter {
	/**
	 * User Permission Authorization Filter
	 */
	
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
		// Get authorization header and validate
		final String token = SecurityUtil.getTokenFromRequestHeader(request, response, mapper);
		log.info("[JwtTokenFilterService][doFilterInternal] Token Value : {}", token);

		keycloakPermissionManagementService.validateAndSetActiveUserAuthorities(token);
		keycloakPermissionManagementService.getRealmNameFromToken("tenantName", token);

		chain.doFilter(request, response);

	}

}