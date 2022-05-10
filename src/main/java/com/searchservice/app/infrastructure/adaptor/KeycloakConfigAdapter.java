package com.searchservice.app.infrastructure.adaptor;

import com.searchservice.app.domain.port.spi.KeycloakConfigAdapterPort;
import com.searchservice.app.domain.utils.HttpStatusCode;
import com.squareup.okhttp.*;
import com.searchservice.app.domain.dto.Response;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;


@Data
@NoArgsConstructor
@Component
public class KeycloakConfigAdapter implements KeycloakConfigAdapterPort {
	
	private final Logger log = LoggerFactory.getLogger(KeycloakConfigAdapter.class);	
	private static final String KEYCLOAK_INTERACT_ISSUE = "Couldn't interact with Keycloak server!";
	private static final String ERROR = "error";
	private static final String ADMIN_USERNAME = "admin";
	private static final String ADMIN_PASSWORD = "admin";
	
	@Value("${token-username}")
	private String username;
	@Value("${token-password}")
	private String password;
	@Value("${keycloak.auth-server-url}")
	private String keycloakServerUrl;
	@Value("${base-ingress-ms-url}"+"${token-url}")
	private String baseIngresstokenUrl;


	
	@Autowired
	RestTemplate restTemplate;
	
	@Override
	public KeycloakConfigAdapterResponse getRolesInfo(String url) {
	
		String ingressServiceToken = getAdminCliToken(ADMIN_USERNAME, ADMIN_PASSWORD).getToken();
		
		if (!ingressServiceToken.isBlank()) {
			
			OkHttpClient client = new OkHttpClient();

			log.debug("GET Client App Roles");
			Request request = new Request.Builder().url(url).addHeader("Authorization", "Bearer " + ingressServiceToken)
					.build();

			try {
				String response = client.newCall(request).execute().body().string();

				return new KeycloakConfigAdapterResponse(true, "Roles Retrieved Successfully!",
						response);

			} catch (IOException e) {
				log.error(KEYCLOAK_INTERACT_ISSUE);
				return new KeycloakConfigAdapterResponse(false, "Roles could not be retrieved! IOException.", "");
			}
		} else {
			log.error(KEYCLOAK_INTERACT_ISSUE);
			return new KeycloakConfigAdapterResponse(false, "Ingress Miscroservice Authorization Failed!!", "");
		}

	}

	
	//Get token form admin-cli
	@Override
	public Response getAdminCliToken(String username, String password) {
		
		if (username.isBlank() || username.isEmpty() || password.isBlank() || password.isEmpty()) {
			return createResponse(ERROR, "username and password must bot be blank.", 
					HttpStatusCode.BAD_REQUEST_EXCEPTION.getCode());
		}
		String url = keycloakServerUrl + "/realms/" + "master" + "/protocol/openid-connect/token";

		// Creating and setting the Header
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED);

		// Creating Body parameters
		MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
		map.add("grant_type", "password");
		map.add("client_id", "admin-cli");
		map.add("username", username);
		map.add("password", password);

		// Creating HttpEntity and set header and body
		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

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

	
	@Data
	public static class KeycloakConfigAdapterResponse {
		boolean isRolesRetrieved;
		String message;
		String responseString;

		public KeycloakConfigAdapterResponse(boolean isTableRetrieved, String message, String responseString) {
			this.isRolesRetrieved = isTableRetrieved;
			this.message = message;
			this.responseString = responseString;
		}
	}
}
