package com.searchservice.app.domain.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import com.searchservice.app.config.AuthConfigProperties;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;


@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
@SpringBootTest
@TestInstance(Lifecycle.PER_CLASS)
@TestPropertySource(
        properties = {
           "cache-name: cacheTest"
        }
)
class PublicKeyServiceTest {

	@InjectMocks
	PublicKeyService publicKeyService;
	
	@Autowired
	private AuthConfigProperties authConfigProperties;

	@Mock
    private RestTemplate restTemplate;
	
	@Mock
	private CacheManager cache;
		
	private String expectedPublicKeyJson = "{\"realm\":\"master\",\"public_key\":\"Public-Key-Test\"}";
	ConcurrentMapCache keyCache = new ConcurrentMapCache("${cache-name}");
	
	@BeforeAll
	void setUp() {
		ReflectionTestUtils.setField(publicKeyService,"authConfigProperties",authConfigProperties);
	}
	void setPublicKeyResponse() {
		keyCache.put(authConfigProperties.getRealmName(), "testing");
		Mockito.lenient().when(this.restTemplate.getForEntity(authConfigProperties.getKeyUrl()
				+ authConfigProperties.getRealmName(), String.class)).
		thenReturn(new ResponseEntity<String>(expectedPublicKeyJson, HttpStatus.OK));
		Mockito.when(cache.getCache("${cache-name}")).thenReturn(keyCache);	
	}
	
	void setErrorResponse() {
		Mockito.lenient().when(this.restTemplate.getForEntity(authConfigProperties.getKeyUrl()
				+ authConfigProperties.getRealmName(), String.class)).
		thenThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR));	
	}

	@Test
	void publicKeyServiceTest() {
		setPublicKeyResponse();
		assertEquals("Public-Key-Test",publicKeyService.retrievePublicKey(authConfigProperties.getRealmName()));
		assertTrue(publicKeyService.checkIfPublicKeyExistsInCache());
		
		setErrorResponse();
		assertEquals("",publicKeyService.retrievePublicKey(authConfigProperties.getRealmName()));
	}
}

