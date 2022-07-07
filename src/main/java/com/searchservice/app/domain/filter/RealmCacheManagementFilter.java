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
import com.searchservice.app.config.TenantInfoConfigProperties;
import com.searchservice.app.domain.service.security.KeycloakPermissionManagementService;
import com.searchservice.app.domain.utils.security.SecurityUtil;

@Component
public class RealmCacheManagementFilter extends OncePerRequestFilter {
	/**
	 * User Permission Authorization Filter
	 */
	
	private final Logger log = LoggerFactory.getLogger(RealmCacheManagementFilter.class);
	
	private ObjectMapper mapper = new ObjectMapper();

	@Autowired
	private KeycloakPermissionManagementService keycloakPermissionManagementService;
	
	@Autowired
	TenantInfoConfigProperties tenantInfoConfigProperties;
	
	public RealmCacheManagementFilter() {
		super();
	}

	public RealmCacheManagementFilter(KeycloakPermissionManagementService keycloakPermissionManagementService) {
		super();
		this.keycloakPermissionManagementService = keycloakPermissionManagementService;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {
		// Get authorization header and validate
		final String token = SecurityUtil.getTokenFromRequestHeader(request, response, mapper);
		log.info("[JwtTokenFilterService][doFilterInternal] Token Value : {}", token);

		/**
		 *  Set Tenant Name(~ Realm Name) in cache
		 */
		String tenantNameParameter = request.getParameter("tenantName");
		// Evict Realm Info cache before adding new Realm Info
		keycloakPermissionManagementService.evictRealmNameFromCache(tenantInfoConfigProperties.getTenant());
		if(request.getRequestURI().equals("/api/v1/manage/table/")
				&& "POST".equalsIgnoreCase(request.getMethod())
				&& 
				!keycloakPermissionManagementService.checkIfRealmNameExistsInCache(tenantInfoConfigProperties.getTenant())) {

			// Validate User: if admin, then take tenantName from request param
			if(keycloakPermissionManagementService.isActiveUserAdmin(token))
				keycloakPermissionManagementService.setRealmNameInCache(
						tenantInfoConfigProperties.getTenant(), token, tenantNameParameter);
			else
				keycloakPermissionManagementService.setRealmNameInCache(
						tenantInfoConfigProperties.getTenant(), token, "");
		}

		chain.doFilter(request, response);

	}

}