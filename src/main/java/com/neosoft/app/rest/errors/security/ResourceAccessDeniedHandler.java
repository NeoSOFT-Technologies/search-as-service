package com.neosoft.app.rest.errors.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;

import com.neosoft.app.rest.errors.HttpStatusCode;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ResourceAccessDeniedHandler implements AccessDeniedHandler {

	@Autowired
	DelegatedAuthenticationEntryPoint delegatedAuthenticationEntryPoint;
	
	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
			AccessDeniedException accessDeniedException) throws IOException, ServletException {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null) {
			log.error("User: '{}' attempted to access the unauthorised URL: {}", auth.getName(),
					request.getRequestURI());
			delegatedAuthenticationEntryPoint.commence(request, response,
					new AuthenticationException(HttpStatusCode.FORBIDDEN_EXCEPTION.getMessage()) {
						private static final long serialVersionUID = 1L;});

		}
	}
}
