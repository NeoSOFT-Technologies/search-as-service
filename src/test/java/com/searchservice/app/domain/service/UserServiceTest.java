package com.searchservice.app.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.searchservice.app.domain.dto.Response;
import com.searchservice.app.domain.dto.user.User;
import com.searchservice.app.rest.errors.HttpStatusCode;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
@SpringBootTest
@TestInstance(Lifecycle.PER_CLASS)
class UserServiceTest {

	@InjectMocks
	UserService userService;
	
	HttpHeaders headers = new HttpHeaders();
	
	@Value("${base-token-url}")
	private String baseTokenUrl;
	private User user;
	
	String expectedTokenJson = "{\"access_token\":\"Valid-Token\",\"expires_in\":600,\"refresh_expires_in\":1800,"
			+ "\"refresh_token\":\"Refresh-Token\",\"token_type\":"
			+ "\"Bearer\",\"not-before-policy\":0,\"session_state\":"
			+ "\"Session-Value\",\"scope\":\"profileemail\"}";
	
	String expectedErrorJson = "{\"error\":\"Something Went Wrong\",\"error_description\":\"Invalid Credentials\"}";
	
	@Mock
    private RestTemplate restTemplate;
	
	private String userName = "test";
	private String password = "12234";
	
	@BeforeAll
	public void setUp() {
		user = new User();
		ReflectionTestUtils.setField(userService,"baseTokenUrl",baseTokenUrl);
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
	}
	
	public void setMockitoSuccessResponse() {
		HttpEntity<User> request = new HttpEntity<>(user,headers);
		Mockito.when(this.restTemplate.postForEntity(baseTokenUrl, request, String.class)).
		thenReturn(new ResponseEntity<String>(expectedTokenJson, HttpStatus.OK));
	}
	
	public void setMockitoBadResponse(boolean isError) {
		HttpEntity<User> request = new HttpEntity<>(user,headers);
		if(isError) {
		    Mockito.when(this.restTemplate.postForEntity(baseTokenUrl, request, String.class)).
		    thenReturn(new ResponseEntity<String>(expectedErrorJson, HttpStatus.OK));
		}else {
			Mockito.when(this.restTemplate.postForEntity(baseTokenUrl, request, String.class)).
			thenReturn(new ResponseEntity<String>("{}", HttpStatus.OK));
		}
	}
	
	public void setMockitoExceptionResponse() {
		HttpEntity<User> request = new HttpEntity<>(user,headers);
		Mockito.when(this.restTemplate.postForEntity(baseTokenUrl, request, String.class)).
		thenThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR));
		
	}
	
	@Test
	void getTokenSuccessTest() {
		user.setUsername(userName);
		user.setPassword(password);
		setMockitoSuccessResponse();
		Response tokenResponse = userService.getToken(user);
		assertEquals(200, tokenResponse.getStatusCode());
	}
	
	@Test
	void getTokenErrorTest() {
		user.setUsername(userName+"12");
		user.setPassword(password);
		setMockitoBadResponse(true);
		Response tokenResponse = userService.getToken(user);
		assertEquals(HttpStatusCode.BAD_REQUEST_EXCEPTION.getCode(), tokenResponse.getStatusCode());
	}
	
	@Test
	void getTokenExceptionTest() {
		user.setUsername(userName);
		user.setPassword(password+"p0");
		setMockitoExceptionResponse();
		Response tokenResponse = userService.getToken(user);
		assertEquals(HttpStatusCode.BAD_REQUEST_EXCEPTION.getCode(), tokenResponse.getStatusCode());
		 
	}
	
	@Test
	void getTokenEmptyResponseTest() {
		user.setUsername(userName);
		user.setPassword(password);
		setMockitoBadResponse(false);
		Response tokenResponse = userService.getToken(user);
		assertEquals(HttpStatusCode.BAD_REQUEST_EXCEPTION.getCode(), tokenResponse.getStatusCode());
	}
	
	@Test
	void getTokenInvalidUsername() {
		user.setUsername("");
		user.setPassword(password);
		Response tokenResponse = userService.getToken(user);
		assertEquals(HttpStatusCode.BAD_REQUEST_EXCEPTION.getCode(), tokenResponse.getStatusCode());
		
	}
	
	@Test
	void getTokenInvalidPassword() {
		user.setUsername(userName);
		user.setPassword("");
		Response tokenResponse = userService.getToken(user);
		assertEquals(HttpStatusCode.BAD_REQUEST_EXCEPTION.getCode(), tokenResponse.getStatusCode());
		
	}
}
