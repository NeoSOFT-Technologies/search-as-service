package com.searchservice.app.rest.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.json.JSONObject;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import com.searchservice.app.SearchServiceApplication;
import com.searchservice.app.domain.dto.Response;
import com.searchservice.app.domain.dto.user.User;

@RunWith(SpringRunner.class)
@SpringBootTest(classes =SearchServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestPropertySource(
        properties = {
                "username: admin",
                "password: adminPassword@1"
        }
)
public class UserResourceIntegrationTest {

	@LocalServerPort
	private int port;

	@Value("${username}")
	private String username;
	
	@Value("${password}")
	private String password;
	
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
	public void successGetToken() {
		User user = new User(username, password);
		assertEquals(200, getToken(user));
	}
	
	@Test
	public void invallidCredTest() {
		User user = new User(username, password+"12");
		assertEquals(400, getToken(user));
	}
	
	private String createURLWithPort(String uri) {
		return "http://localhost:" + port + uri;
	}

}

