package com.searchservice.app.rest.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.json.JSONObject;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import com.searchservice.app.SearchServiceApplication;
import com.searchservice.app.domain.dto.Response;
import com.searchservice.app.domain.dto.user.User;

@SpringBootTest(classes =SearchServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserResourceIntegrationTest {

	@LocalServerPort
	private int port;

	@Value("${adminUserName}")
	private String username;
	
	@Value("${adminPassword}")
	private String password;
	
	@Value("${base-url.request}")
	private String hostURL;
	
	TestRestTemplate restTemplate = new TestRestTemplate();

	HttpHeaders headers = new HttpHeaders();		
	
	int getToken(User user) {
		HttpEntity<User> entity = new HttpEntity<User>(user, headers);
		ResponseEntity<Response> response = restTemplate.postForEntity(
				createURLWithPort("/user/token"),
				 entity, Response.class);
		return new JSONObject(response.getBody()).getInt("statusCode");
	}
	@Test
	void successGetToken() {
		
		User user = new User(username, password);
		assertEquals(200, getToken(user));
	}
	
	@Test
	void invallidCredTest() {
		User user = new User(username, password+"12");
		assertEquals(400, getToken(user));
	}
	
	private String createURLWithPort(String uri) {
		return hostURL +":" + port + uri;
	}

}

