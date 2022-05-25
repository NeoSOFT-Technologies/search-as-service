package com.searchservice.app.domain.service.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeAll;
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
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import com.searchservice.app.config.UserPermissionConfigProperties;
import com.searchservice.app.rest.errors.CustomException;
import com.searchservice.app.rest.errors.HttpStatusCode;


@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
@SpringBootTest
@TestInstance(Lifecycle.PER_CLASS)
@TestPropertySource(
        properties = {
           "user-cache.name: userPermissionCacheIngress", 
           "user-cache.key: userPermissions", 
           "user-cache.view: viewPermission", 
           "user-cache.create: createPermission", 
           "user-cache.edit: editPermission", 
           "user-cache.delete: deletePermission"
        }
)
class KeycloakUserPermissionTest {

	@InjectMocks
	KeycloakUserPermission keycloakUserPermission;
	
	@Autowired
	private UserPermissionConfigProperties userPermissionConfigProperties;

	@Mock
    private RestTemplate restTemplate;
	
	@Mock
	private CacheManager cacheManager;
	
	@Mock
	private Cache cache;
	
	ConcurrentMapCache keyCache = new ConcurrentMapCache("${user-cache.name}");
	
	@BeforeAll
	void setUp() {
		ReflectionTestUtils.setField(keycloakUserPermission,"userPermissionConfigProperties",userPermissionConfigProperties);
	}
	
	void setUserPermissionResponse() {
		keyCache.put(userPermissionConfigProperties.getName(), "testingAllPermissionsCache");
		keyCache.put(userPermissionConfigProperties.getKey(), "testingAllPermissions");
		keyCache.put(userPermissionConfigProperties.getView(), true);
		keyCache.put(userPermissionConfigProperties.getCreate(), true);
		keyCache.put(userPermissionConfigProperties.getEdit(), true);
		keyCache.put(userPermissionConfigProperties.getDelete(), true);
		Mockito.when(cacheManager.getCache(Mockito.anyString())).thenReturn(keyCache);
		Mockito.lenient().when(cache.get(Mockito.anyString())).thenReturn(keyCache.get("${user-cache.view}"));
		Mockito.lenient().when(cache.get("${user-cache.delete}")).thenReturn(keyCache.get("${user-cache.delete}"));
	}
	
	void setErrorResponse() {
		keyCache.clear();
		keyCache.put(userPermissionConfigProperties.getName(), "testingAllPermissions");
		Mockito.when(cacheManager.getCache(Mockito.anyString())).thenReturn(keyCache);
		Mockito.lenient().when(cache.get(Mockito.anyString())).thenReturn(keyCache.get("${user-cache.view}"));
	}

	@Test
	void userPermissionsTest() {

		setUserPermissionResponse();
		assertTrue(keycloakUserPermission.checkIfUserPermissionExistsInCache(userPermissionConfigProperties.getView()));
		assertTrue(keycloakUserPermission.getUserPermissionFromCache(userPermissionConfigProperties.getView()));
		assertTrue(keycloakUserPermission.isViewPermissionEnabled());
		assertTrue(keycloakUserPermission.isCreatePermissionEnabled());
		assertTrue(keycloakUserPermission.isEditPermissionEnabled());
		assertTrue(keycloakUserPermission.isDeletePermissionEnabled());
		
		setErrorResponse();
		CustomException exception = assertThrows(CustomException.class, () -> {
			keycloakUserPermission.getUserPermissionFromCache(userPermissionConfigProperties.getView());
		});
		assertEquals(HttpStatusCode.NULL_POINTER_EXCEPTION.getCode(), exception.getExceptionCode());
	}
}