package com.searchservice.app.domain.service;

import java.util.Arrays;

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
import org.springframework.web.client.RestTemplate;
import com.searchservice.app.domain.dto.Response;
import com.searchservice.app.domain.dto.user.User;
import com.searchservice.app.domain.port.api.UserServicePort;
import com.searchservice.app.domain.utils.HttpStatusCode;

@Service
public class UserService implements UserServicePort {

	private static final String ERROR = "error";

	@Autowired
	RestTemplate restTemplate;
	
	@Value("${base-token-url}")
	private String baseTokenUrl;
	
	private final Logger log = LoggerFactory.getLogger(UserService.class);
	
	@Override
	public Response getToken(User user) {
		if (user.getUsername().isBlank() || user.getUsername().isEmpty() || user.getPassword().isBlank() || user.getPassword().isEmpty()) {
			return createResponse(ERROR, "username and password must bot be blank.", 
					HttpStatusCode.BAD_REQUEST_EXCEPTION.getCode());
		}
		HttpHeaders headers = new HttpHeaders();
	    headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
	    HttpEntity<User> request = new HttpEntity<>(user,headers);
	    ResponseEntity<String> response = new ResponseEntity<>(HttpStatus.OK);
	    try {
			response = restTemplate.postForEntity(baseTokenUrl, request, String.class);
		} catch (Exception e) {
			log.error("Something Went Wrong Whil Obtaining Token Value", e);
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
			return createResponse(error, errorDesc, HttpStatusCode.BAD_REQUEST_EXCEPTION.getCode());
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