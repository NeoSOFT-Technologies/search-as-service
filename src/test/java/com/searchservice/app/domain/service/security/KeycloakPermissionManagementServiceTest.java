package com.searchservice.app.domain.service.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
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

import com.searchservice.app.config.UserPermissionConfigProperties;
import com.searchservice.app.domain.utils.HttpStatusCode;
import com.searchservice.app.rest.errors.CustomException;

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
class KeycloakPermissionManagementServiceTest {

	@Mock
	KeycloakUserPermission keycloakUserPermission;
	
	@InjectMocks
	KeycloakPermissionManagementService keycloakPermissionManagementService;
	
	@Autowired
	private UserPermissionConfigProperties userPermissionConfigProperties;
	
	@Mock
	private CacheManager cacheManager;
	
	@Mock
	private Cache cache;
	
	ConcurrentMapCache keyCache = new ConcurrentMapCache("${user-cache.name}");
	
	private String tokenMock = null;
	
	@BeforeAll
	void setUpBeforeAll() {
		ReflectionTestUtils.setField(keycloakPermissionManagementService, "userPermissionConfigProperties", userPermissionConfigProperties);
	}
	
	@BeforeEach
	private void setUp() {
		tokenMock = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJtTjJ1NE1hM2Qtc1BRcDBzYnZTUVp1UXpaT19jWDVTSHFuMTE3U1RaRF9jIn0.eyJleHAiOjE2NTM0MTcxMjcsImlhdCI6MTY1MzM4MTEyNywianRpIjoiYmU3Mjk3NTctZTUxYy00YzdlLTg4NTMtYzk2MzYxY2Y2NDQ5IiwiaXNzIjoiaHR0cHM6Ly9pYW0ta2V5Y2xvYWsubmVvc29mdHRlY2guY29tL2F1dGgvcmVhbG1zL21hc3RlciIsImF1ZCI6WyJzYW50b3NoLXJlYWxtIiwiZGVtbzEtcmVhbG0iLCJUZW5hbnQxLXJlYWxtIiwibWFzdGVyLXJlYWxtIiwiVGVuYW50Mi1yZWFsbSIsImFjY291bnQiXSwic3ViIjoiMzgzNjg3NmEtMTI1YS00OTNiLWI0M2QtN2MwMTMxODg1ZTJiIiwidHlwIjoiQmVhcmVyIiwiYXpwIjoiYWRtaW4tY2xpIiwic2Vzc2lvbl9zdGF0ZSI6IjcyYjNlNDg2LWZlNjYtNDk0Yy1iMjc1LTFlNTYyOTQ3NzliNCIsImFjciI6IjEiLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsiY3JlYXRlLXJlYWxtIiwiZGVmYXVsdC1yb2xlcy1tYXN0ZXIiLCJvZmZsaW5lX2FjY2VzcyIsImFkbWluIiwidW1hX2F1dGhvcml6YXRpb24iXX0sInJlc291cmNlX2FjY2VzcyI6eyJzYW50b3NoLXJlYWxtIjp7InJvbGVzIjpbInZpZXctcmVhbG0iLCJ2aWV3LWlkZW50aXR5LXByb3ZpZGVycyIsIm1hbmFnZS1pZGVudGl0eS1wcm92aWRlcnMiLCJpbXBlcnNvbmF0aW9uIiwiY3JlYXRlLWNsaWVudCIsIm1hbmFnZS11c2VycyIsInF1ZXJ5LXJlYWxtcyIsInZpZXctYXV0aG9yaXphdGlvbiIsInF1ZXJ5LWNsaWVudHMiLCJxdWVyeS11c2VycyIsIm1hbmFnZS1ldmVudHMiLCJtYW5hZ2UtcmVhbG0iLCJ2aWV3LWV2ZW50cyIsInZpZXctdXNlcnMiLCJ2aWV3LWNsaWVudHMiLCJtYW5hZ2UtYXV0aG9yaXphdGlvbiIsIm1hbmFnZS1jbGllbnRzIiwicXVlcnktZ3JvdXBzIl19LCJkZW1vMS1yZWFsbSI6eyJyb2xlcyI6WyJ2aWV3LWlkZW50aXR5LXByb3ZpZGVycyIsInZpZXctcmVhbG0iLCJtYW5hZ2UtaWRlbnRpdHktcHJvdmlkZXJzIiwiaW1wZXJzb25hdGlvbiIsImNyZWF0ZS1jbGllbnQiLCJtYW5hZ2UtdXNlcnMiLCJxdWVyeS1yZWFsbXMiLCJ2aWV3LWF1dGhvcml6YXRpb24iLCJxdWVyeS1jbGllbnRzIiwicXVlcnktdXNlcnMiLCJtYW5hZ2UtZXZlbnRzIiwibWFuYWdlLXJlYWxtIiwidmlldy1ldmVudHMiLCJ2aWV3LXVzZXJzIiwidmlldy1jbGllbnRzIiwibWFuYWdlLWF1dGhvcml6YXRpb24iLCJtYW5hZ2UtY2xpZW50cyIsInF1ZXJ5LWdyb3VwcyJdfSwiVGVuYW50MS1yZWFsbSI6eyJyb2xlcyI6WyJ2aWV3LXJlYWxtIiwidmlldy1pZGVudGl0eS1wcm92aWRlcnMiLCJtYW5hZ2UtaWRlbnRpdHktcHJvdmlkZXJzIiwiaW1wZXJzb25hdGlvbiIsImNyZWF0ZS1jbGllbnQiLCJtYW5hZ2UtdXNlcnMiLCJxdWVyeS1yZWFsbXMiLCJ2aWV3LWF1dGhvcml6YXRpb24iLCJxdWVyeS1jbGllbnRzIiwicXVlcnktdXNlcnMiLCJtYW5hZ2UtZXZlbnRzIiwibWFuYWdlLXJlYWxtIiwidmlldy1ldmVudHMiLCJ2aWV3LXVzZXJzIiwidmlldy1jbGllbnRzIiwibWFuYWdlLWF1dGhvcml6YXRpb24iLCJtYW5hZ2UtY2xpZW50cyIsInF1ZXJ5LWdyb3VwcyJdfSwibWFzdGVyLXJlYWxtIjp7InJvbGVzIjpbInZpZXctaWRlbnRpdHktcHJvdmlkZXJzIiwidmlldy1yZWFsbSIsIm1hbmFnZS1pZGVudGl0eS1wcm92aWRlcnMiLCJpbXBlcnNvbmF0aW9uIiwiY3JlYXRlLWNsaWVudCIsIm1hbmFnZS11c2VycyIsInF1ZXJ5LXJlYWxtcyIsInZpZXctYXV0aG9yaXphdGlvbiIsInF1ZXJ5LWNsaWVudHMiLCJxdWVyeS11c2VycyIsIm1hbmFnZS1ldmVudHMiLCJtYW5hZ2UtcmVhbG0iLCJ2aWV3LWV2ZW50cyIsInZpZXctdXNlcnMiLCJ2aWV3LWNsaWVudHMiLCJtYW5hZ2UtYXV0aG9yaXphdGlvbiIsIm1hbmFnZS1jbGllbnRzIiwicXVlcnktZ3JvdXBzIl19LCJUZW5hbnQyLXJlYWxtIjp7InJvbGVzIjpbInZpZXctaWRlbnRpdHktcHJvdmlkZXJzIiwidmlldy1yZWFsbSIsIm1hbmFnZS1pZGVudGl0eS1wcm92aWRlcnMiLCJpbXBlcnNvbmF0aW9uIiwiY3JlYXRlLWNsaWVudCIsIm1hbmFnZS11c2VycyIsInF1ZXJ5LXJlYWxtcyIsInZpZXctYXV0aG9yaXphdGlvbiIsInF1ZXJ5LWNsaWVudHMiLCJxdWVyeS11c2VycyIsIm1hbmFnZS1ldmVudHMiLCJtYW5hZ2UtcmVhbG0iLCJ2aWV3LWV2ZW50cyIsInZpZXctdXNlcnMiLCJ2aWV3LWNsaWVudHMiLCJtYW5hZ2UtYXV0aG9yaXphdGlvbiIsIm1hbmFnZS1jbGllbnRzIiwicXVlcnktZ3JvdXBzIl19LCJhY2NvdW50Ijp7InJvbGVzIjpbIm1hbmFnZS1hY2NvdW50IiwibWFuYWdlLWFjY291bnQtbGlua3MiLCJ2aWV3LXByb2ZpbGUiXX19LCJzY29wZSI6InByb2ZpbGUgZW1haWwiLCJzaWQiOiI3MmIzZTQ4Ni1mZTY2LTQ5NGMtYjI3NS0xZTU2Mjk0Nzc5YjQiLCJlbWFpbF92ZXJpZmllZCI6ZmFsc2UsIm5hbWUiOiJTYW50b3NoIFNoaW5kZSIsInBlcm1pc3Npb24iOlsiY3JlYXRlIiwidmlldyIsImVkaXQiLCJkZWxldGUiXSwicHJlZmVycmVkX3VzZXJuYW1lIjoiYWRtaW4iLCJnaXZlbl9uYW1lIjoiU2FudG9zaCIsImZhbWlseV9uYW1lIjoiU2hpbmRlIiwiZW1haWwiOiJzYW50b3NoLnNoaW5kZUBuZW9zb2Z0dGVjaC5jb20ifQ.P0lQvklFhdy6lB92T1NvdAO50nALhB9bxR_pS4B0eN8yBQcrCxBanoqP7v-4LabluQiH9tHEKkL2hjMeS6ZOdY6WPqQNLaRuFDIMOtcx7SNnNZ8yLNwDSkmEfkd1Bxd0zRyxgDyXy6RWzJrk7c5ekeIt_w1Zp_ChZuCsRr2F0hO3rUvgfwlovf6eNypE9cIhd-T1PhoJfDccjXETjB5xeNRBAwIxJHVBNH6_XKpzGchMvbJQ8up7YN0B2uiAPRUNzs9CtDZIb3yKzW7OGOHdwWuw1hVgBVERzo7QmBk2zGxtoLh7NxUN4CEPP-AYypPzDiDFJ1ZnZe1816zG6RgotQ";
	}
	
	private void setUpKeycloakUserPermissionsMock() {
		when(keycloakUserPermission.setViewPermissionEnabled(Mockito.anyString(), Mockito.anyBoolean())).
			thenReturn(true);
		when(keycloakUserPermission.setCreatePermissionEnabled(Mockito.anyString(), Mockito.anyBoolean())).
			thenReturn(true);
		when(keycloakUserPermission.setEditPermissionEnabled(Mockito.anyString(), Mockito.anyBoolean())).
			thenReturn(true);
		when(keycloakUserPermission.setDeletePermissionEnabled(Mockito.anyString(), Mockito.anyBoolean())).
			thenReturn(true);
	}
	
	@Test
	void testGetDecodedTokenPayloadJsonWhenValidTokenIsProvided() {
		JSONObject tokenPayloadJson = keycloakPermissionManagementService.getDecodedTokenPayloadJson(tokenMock);
		
		JSONArray permissions = tokenPayloadJson.getJSONArray("permission");
		
		List<String> permissionsList = permissions.toList().stream().map(p -> Objects.toString(p, null)).collect(Collectors.toList());
		
		assertTrue(!permissionsList.isEmpty());
		assertEquals(Arrays.asList("create", "view", "edit", "delete"), permissionsList);
	}
	
	@Test
	void testGetDecodedTokenPayloadJsonWhenInvalidTokenIsProvided() {
		CustomException exception = assertThrows(CustomException.class, () -> {
			keycloakPermissionManagementService.getDecodedTokenPayloadJson("[invalid token header].[invalid token payload].[invalid token signature]");
		});
		
		assertEquals(HttpStatusCode.INVALID_JSON_INPUT.getCode(), exception.getExceptionCode());
		assertEquals(HttpStatusCode.INVALID_JSON_INPUT.getMessage(), exception.getExceptionMessage());
	}
	
	@Test
	void testGetActiveUserPermissions() {
		JSONObject tokenPayloadJson = keycloakPermissionManagementService.getDecodedTokenPayloadJson(tokenMock);
		
		List<String> userPermissions = keycloakPermissionManagementService.getActiveUserPermissions(tokenPayloadJson);
		
		assertTrue(!userPermissions.isEmpty());
		assertEquals(Arrays.asList("create", "view", "edit", "delete"), userPermissions);
	}
	
	@Test
	void testIsViewPermissionGranted() {
		assertTrue(keycloakPermissionManagementService.isViewPermissionGranted(tokenMock));
	}
	
	@Test
	void testIsCreatePermissionGranted() {
		assertTrue(keycloakPermissionManagementService.isCreatePermissionGranted(tokenMock));
	}
	
	@Test
	void testIsEditPermissionGranted() {
		assertTrue(keycloakPermissionManagementService.isEditPermissionGranted(tokenMock));
	}
	
	@Test
	void testIsDeletePermissionGranted() {
		assertTrue(keycloakPermissionManagementService.isDeletePermissionGranted(tokenMock));
	}
	
	@Test
	void testValidateAndSetActiveUserAuthorities() {
		
		setUpKeycloakUserPermissionsMock();
		keycloakPermissionManagementService.validateAndSetActiveUserAuthorities(tokenMock);
		keycloakUserPermission.setViewPermissionEnabled("", false);
		
		verify(keycloakUserPermission, times(1)).setViewPermissionEnabled("", false);
		
	}

}