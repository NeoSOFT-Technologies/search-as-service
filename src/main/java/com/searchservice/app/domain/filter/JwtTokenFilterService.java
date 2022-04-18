package com.searchservice.app.domain.filter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.searchservice.app.config.KeycloakConfigProperties;

//@Component
public class JwtTokenFilterService extends OncePerRequestFilter {

	private RestTemplate restTemplate;
	private KeycloakConfigProperties keycloakConfigProperties;
	private ObjectMapper mapper = new ObjectMapper();

	public JwtTokenFilterService() {
		super();
	}

	public JwtTokenFilterService(KeycloakConfigProperties keycloakConfigProperties, RestTemplate restTemplate) {
		super();
		this.keycloakConfigProperties = keycloakConfigProperties;
		this.restTemplate = restTemplate;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {
		Map<String, Object> errorDetails = new HashMap<>();
		// Get authorization header and validate
		final String header = request.getHeader(HttpHeaders.AUTHORIZATION);

		if (null == header || header.isEmpty() || !header.startsWith("Bearer ")) {
			errorDetails.put("Unauthorized", "Invalid token");
			response.setStatus(HttpStatus.UNAUTHORIZED.value());
			response.setContentType(MediaType.APPLICATION_JSON_VALUE);
			mapper.writeValue(response.getWriter(), errorDetails);
			return;
		}

		// Get jwt token and validate
		final String token = header.split(" ")[1].trim();

		if (!validate(token)) {
			errorDetails.put("Unauthorized", "Invalid token");
			response.setStatus(HttpStatus.FORBIDDEN.value());
			response.setContentType(MediaType.APPLICATION_JSON_VALUE);
			mapper.writeValue(response.getWriter(), errorDetails);

		} else {
			chain.doFilter(request, response);
		}
	}

	
	private boolean validate(String token) {

		String url = keycloakConfigProperties.getAuth_server_url() + "/realms/" + keycloakConfigProperties.getRealm()
				+ "/protocol/openid-connect/token/introspect";

		// creating and setting the Header
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		// creating Body parameters
		MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
		map.add("token", token);
		map.add("client_id", keycloakConfigProperties.getResource());
		map.add("client_secret", keycloakConfigProperties.getCredentials().getSecret());

		// Creating HttpEntity and set header and body
		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

		// Consuming rest API
		ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

		JSONObject obj = new JSONObject(response.getBody());

		return obj.getBoolean("active");
	}
}