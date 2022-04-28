package com.searchservice.app.domain.service;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.searchservice.app.domain.dto.Response;
import com.searchservice.app.domain.port.api.UserServicePort;
import com.searchservice.app.domain.utils.HttpStatusCode;

@Service
public class UserService implements UserServicePort {

	private static final String ERROR = "error";

	@Autowired
	RestTemplate restTemplate;

	@Value("${keycloak.realm}")
	private String realmName;

	@Value("${keycloak.auth-server-url}")
	private String keycloakServerUrl;

	@Value("${keycloak.resource}")
	private String clientId;

	@Value("${keycloak.credentials.secret}")
	private String clientSecret;

	private final Logger log = LoggerFactory.getLogger(UserService.class);

	@Override
	public Response getToken(String userName, String password) {
		
		if (userName.isBlank() || userName.isEmpty() || password.isBlank() || password.isEmpty()) {
			return createResponse(ERROR, "username and password must bot be blank.", 
					HttpStatusCode.BAD_REQUEST_EXCEPTION.getCode());
		}
		String url = keycloakServerUrl + "/realms/" + realmName + "/protocol/openid-connect/token";
		log.info(" client_Id : {}", clientId);
		log.info(" client_Secret : {}", clientSecret);
		log.info(" url : {}", url);

		// Creating and setting the Header
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		// Creating Body parameters
		MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
		map.add("grant_type", "password");
		map.add("client_id", clientId);
		map.add("client_secret", clientSecret);
		map.add("username", userName);
		map.add("password", password);

		// Creating HttpEntity and set header and body
		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

		// Consuming rest API
		ResponseEntity<String> response = new ResponseEntity<>(HttpStatus.OK);
		try {
			response = restTemplate.postForEntity(url, request, String.class);
		} catch (Exception e) {
			return createResponse(null, "Invalid credentials", HttpStatusCode.BAD_REQUEST_EXCEPTION.getCode());
		}
		JSONObject obj = new JSONObject(response.getBody());
		if (obj.has("access_token")) {
			String accessToken = obj.getString("access_token");
			return createResponse(accessToken, "Token is generated successfully", 200);
		}
		if (obj.has(ERROR)) {
			String errorDesc = obj.getString("error_description");
			String error = obj.getString(ERROR);
			return createResponse(error, errorDesc, 400);
		}
		return createResponse(null, "Something went wrong! Please try again...", HttpStatusCode.BAD_REQUEST_EXCEPTION.getCode());
	}

	public Response createResponse(String token, String message, int statusCode) {
		Response responseDTO = new Response(token);
		responseDTO.setMessage(message);
		responseDTO.setStatusCode(statusCode);
		return responseDTO;
	}

}