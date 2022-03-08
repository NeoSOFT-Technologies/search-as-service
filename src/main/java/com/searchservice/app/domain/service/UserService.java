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

@Service
public class UserService implements UserServicePort{
	
	@Autowired RestTemplate restTemplate;
	
	@Value("${keycloak.realm}")
	private String realm_name;
	
	@Value("${keycloak.auth-server-url}")
	private String keycloakServerUrl;
	
	@Value("${keycloak.resource}")
	private String client_id;
	
	@Value("${keycloak.credentials.secret}")
	private String client_Secret;
	
	private final Logger log = LoggerFactory.getLogger(UserService.class);

	@Override
	public Response getToken(String userName, String password) {
		log.info("userName : "+userName);
		log.info("password : "+password);
		//ResponseDTO responseDTO = new ResponseDTO("token");
		if(userName.isBlank() || userName.isEmpty() || password.isBlank() || password.isEmpty()) {
			return createResponse("error", "username and password must bot be blank.", 400);
		}
		String url = keycloakServerUrl+"/realms/"+realm_name+"/protocol/openid-connect/token";
    	log.info(" client_Id : "+client_id);
    	log.info(" client_Secret : "+client_Secret);
    	log.info(" url : "+url);
    	
	    // creating and setting the Header
	    	HttpHeaders headers = new HttpHeaders();
	    	headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
	    	
	    // creating Body parameters	
	    	MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
	    	map.add("grant_type", "password");
	    	map.add("client_id", client_id);
	    	map.add("client_secret", client_Secret);
	    	map.add("username", userName);
	    	map.add("password", password);
	    	
	    // Creating HttpEntity and set header and body
	    	HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);
	    
	    // Consuming rest API
	    	ResponseEntity<String> response = new ResponseEntity<>(HttpStatus.OK);
	    	try {
	    		response = restTemplate.postForEntity( url, request , String.class );
			} catch (Exception e) {
				return createResponse("error", "Invalid credentials", 400);
			}
	    	JSONObject obj = new JSONObject(response.getBody());
	    	if (obj.has("access_token")) {
	    		String access_token = obj.getString("access_token");
				return createResponse("token", access_token, 200);
	    	}
	    	if (obj.has("error")) {
	    		String errorDesc = obj.getString("error_description");
	    		String error = obj.getString("error");
				return createResponse(error, errorDesc, 400);
	    	}
		return createResponse("error", "something went wrong! Please try again...", 400);
	}
	
	public Response createResponse(String name, String message, int statusCode) {
		Response responseDTO = new Response(name);
		responseDTO.setMessage(message);
		responseDTO.setStatusCode(statusCode);
		return responseDTO;
	}

}