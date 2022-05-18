package com.searchservice.app.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.searchservice.app.domain.utils.HttpStatusCode;
import com.searchservice.app.rest.errors.CustomException;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)

@SpringBootTest
@TestInstance(Lifecycle.PER_CLASS)
class KeycloakAuthServiceTest {

	
	@InjectMocks
	KeycloakAuthService keycloakAuthService;
	
	private String tokenMock = null;
	
	@BeforeEach
	private void setUp() {
		tokenMock = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJud0NjR0lzbmNkZlctazJ0cFBLZU82R2xweDRUZTNvbzNPMVB3NW9GWVZvIn0.eyJleHAiOjE2NTIyNzc0MTEsImlhdCI6MTY1MjI3NzExMSwianRpIjoiOGU0Y2MzOGEtNDExZS00ZDJjLWIzOTEtNjQyODE0ODU3MzEyIiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDgwL2F1dGgvcmVhbG1zL3NhYXMtcmVhbG0iLCJhdWQiOlsicmVhbG0tbWFuYWdlbWVudCIsImFjY291bnQiXSwic3ViIjoiM2FiNTFkYTktNWE1NS00NzdkLWFhYjctZTFiNWRiYjQwOTU5IiwidHlwIjoiQmVhcmVyIiwiYXpwIjoic2Fhcy1jbGllbnQiLCJzZXNzaW9uX3N0YXRlIjoiNTU1NWIzYWMtYTI3ZS00ZDNkLTk3YzAtZDIwNTU3MDVhN2Y0IiwiYWNyIjoiMSIsInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyJkZWZhdWx0LXJvbGVzLXNhYXMtcmVhbG0iLCJvZmZsaW5lX2FjY2VzcyIsInVtYV9hdXRob3JpemF0aW9uIiwiYXBwLXVzZXIiXX0sInJlc291cmNlX2FjY2VzcyI6eyJyZWFsbS1tYW5hZ2VtZW50Ijp7InJvbGVzIjpbInZpZXctaWRlbnRpdHktcHJvdmlkZXJzIiwidmlldy1yZWFsbSIsIm1hbmFnZS1pZGVudGl0eS1wcm92aWRlcnMiLCJpbXBlcnNvbmF0aW9uIiwicmVhbG0tYWRtaW4iLCJjcmVhdGUtY2xpZW50IiwibWFuYWdlLXVzZXJzIiwicXVlcnktcmVhbG1zIiwidW1hX3Byb3RlY3Rpb24iLCJ2aWV3LWF1dGhvcml6YXRpb24iLCJxdWVyeS1jbGllbnRzIiwicXVlcnktdXNlcnMiLCJtYW5hZ2UtZXZlbnRzIiwibWFuYWdlLXJlYWxtIiwidmlldy1ldmVudHMiLCJ2aWV3LXVzZXJzIiwidmlldy1jbGllbnRzIiwibWFuYWdlLWF1dGhvcml6YXRpb24iLCJtYW5hZ2UtY2xpZW50cyIsInF1ZXJ5LWdyb3VwcyJdfSwic2Fhcy1jbGllbnQiOnsicm9sZXMiOlsibWFuYWdlX3RhYmxlX2FkbWluIiwiZG9jdW1lbnRfaW5nZXN0aW9uX2FkbWluIiwidXNlciJdfSwiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19fSwic2NvcGUiOiJlbWFpbCBteXNjb3BlIHByb2ZpbGUiLCJzaWQiOiI1NTU1YjNhYy1hMjdlLTRkM2QtOTdjMC1kMjA1NTcwNWE3ZjQiLCJlbWFpbF92ZXJpZmllZCI6dHJ1ZSwibmFtZSI6IktpcnRpIEppIiwicGVybWlzc2lvbiI6WyJyZWFkIiwid3JpdGUiLCJ1cGRhdGUiLCJkZWxldGUiXSwicHJlZmVycmVkX3VzZXJuYW1lIjoia2lydGlqaSIsImdpdmVuX25hbWUiOiJLaXJ0aSIsImZhbWlseV9uYW1lIjoiSmkiLCJlbWFpbCI6ImtpcnRpamlAZ21haWwuY29tIn0.XgS5DofWC0F1wflUf-t8o1QSXo6_o8QJ-FioEfiJmSacDSeCZJplnPCAli_WAT7nQ5plfoj1zKtS95ejtKOp0pg3hcRz72O0eyxXTZU8Uh1R1OrTRwsSyBsymtFx63mm_aqkddcFtxPyPsAEIzpaZrj9VTie98cA3BEwXzdzwE6eG8BXinf-o0iL8jk_NpEg7KHqw2FUUh2rT5MoqPfapRoNrs0Z3hAS4RH_c4oS0Nb6zmQxU_d8s6t7VQY3I3Ox28JJ8eCxQ0vYHPT0AEulp0RpzzVu8oLFNpSQZdXNuUTrnZvRYYaSsQJzoee-3LHy6U9gr45GaBpW1nw1-7xy3w";
	}
	
	@Test
	void testGetDecodedTokenPayloadJsonWhenValidTokenIsProvided() {
		JSONObject tokenPayloadJson = keycloakAuthService.getDecodedTokenPayloadJson(tokenMock);
		
		JSONArray permissions = tokenPayloadJson.getJSONArray("permission");
		
		List<String> permissionsList = permissions.toList().stream().map(p -> Objects.toString(p, null)).collect(Collectors.toList());
		
		assertTrue(!permissionsList.isEmpty());
		assertEquals(Arrays.asList("read", "write", "update", "delete"), permissionsList);
	}
	
	@Test
	void testGetDecodedTokenPayloadJsonWhenInvalidTokenIsProvided() {
		CustomException exception = assertThrows(CustomException.class, () -> {
			keycloakAuthService.getDecodedTokenPayloadJson("[invalid token header].[invalid token payload].[invalid token signature]");
		});
		
		assertEquals(HttpStatusCode.INVALID_JSON_INPUT.getCode(), exception.getExceptionCode());
		assertEquals(HttpStatusCode.INVALID_JSON_INPUT.getMessage(), exception.getExceptionMessage());
	}
	
	@Test
	void testGetActiveUserPermissions() {
		JSONObject tokenPayloadJson = keycloakAuthService.getDecodedTokenPayloadJson(tokenMock);
		
		List<String> userPermissions = keycloakAuthService.getActiveUserPermissions(tokenPayloadJson);
		
		assertTrue(!userPermissions.isEmpty());
		assertEquals(Arrays.asList("read", "write", "update", "delete"), userPermissions);
	}
	
	@Test
	void testIsReadPermissionGranted() {
		assertTrue(keycloakAuthService.isReadPermissionGranted(tokenMock));
	}
	
	@Test
	void testIsWritePermissionGranted() {
		assertTrue(keycloakAuthService.isWritePermissionGranted(tokenMock));
	}
	
	@Test
	void testIsUpdatePermissionGranted() {
		assertTrue(keycloakAuthService.isUpdatePermissionGranted(tokenMock));
	}
	
	@Test
	void testIsDeletePermissionGranted() {
		assertTrue(keycloakAuthService.isDeletePermissionGranted(tokenMock));
	}
	

}