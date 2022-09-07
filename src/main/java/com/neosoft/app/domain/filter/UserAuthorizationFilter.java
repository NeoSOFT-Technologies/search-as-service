package com.neosoft.app.domain.filter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neosoft.app.domain.service.security.AppUserService;
import com.neosoft.app.domain.utils.security.AuthUtil;
import com.neosoft.app.domain.utils.security.SecurityLabel;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserAuthorizationFilter extends OncePerRequestFilter {

	private final AuthUtil authUtil;
	
	private final AppUserService appUserService;
	
	public UserAuthorizationFilter(AuthUtil authUtil, AppUserService appUserService) {
		this.authUtil = authUtil;
		this.appUserService = appUserService;
	}
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException, AccessDeniedException {

		if (request.getServletPath().equals(SecurityLabel.URL_LOGIN.getLabel())
				|| request.getServletPath().equals(SecurityLabel.URL_SIGNIN.getLabel())) {
			filterChain.doFilter(request, response);
		} else {
			try {
				String jwt = parseJwt(request);
				if (jwt != null && authUtil.validateJwtToken(jwt)) {
					String username = authUtil.getUserNameFromJwtToken(jwt);

					UserDetails userDetails = appUserService.loadUserByUsername(username);
					UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
							userDetails, null, userDetails.getAuthorities());
					authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

					SecurityContextHolder.getContext().setAuthentication(authentication);
				} else {
					Map<String, String> error = new HashMap<>();
					error.put("error_msg", "User authentication incomplete!");

					response.setContentType(MediaType.APPLICATION_JSON_VALUE);
					new ObjectMapper().writeValue(response.getOutputStream(), error);
				}
			} catch (Exception e) {
				log.error("Error occurred while verifying access token!");
				response.setHeader("error", e.getMessage());

				Map<String, String> error = new HashMap<>();
				error.put("error_msg", e.getMessage());

				response.setContentType(MediaType.APPLICATION_JSON_VALUE);
				new ObjectMapper().writeValue(response.getOutputStream(), error);
			}
			
			filterChain.doFilter(request, response);
		}
	}

	private String parseJwt(HttpServletRequest request) {
		String headerAuth = request.getHeader("Authorization");
		if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
			return headerAuth.substring(7, headerAuth.length());
		}
		return null;
	}

}
