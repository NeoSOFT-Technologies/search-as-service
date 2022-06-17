package com.searchservice.app.domain.service.security;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.searchservice.app.config.UserPermissionConfigProperties;
import com.searchservice.app.rest.errors.CustomException;
import com.searchservice.app.rest.errors.HttpStatusCode;

@Service
public class KeycloakPermissionManagementService {

	@Autowired 
	private UserPermissionConfigProperties userPermissionConfigProperties;
	
	@Autowired
	KeycloakUserPermission keycloakUserPermission;

	public JSONObject getDecodedTokenPayloadJson(String token) {

		String payload = token.split("\\.")[1];

		String decodedPayload = new String(Base64.decodeBase64(payload), StandardCharsets.UTF_8);

		try {
			return new JSONObject(decodedPayload);
		} catch (JSONException e) {
			throw new CustomException(HttpStatusCode.INVALID_JSON_INPUT.getCode(), HttpStatusCode.INVALID_JSON_INPUT,
					HttpStatusCode.INVALID_JSON_INPUT.getMessage());
		}
	}

	public List<String> getActiveUserPermissions(JSONObject tokenPayload) {
		JSONArray permissions = tokenPayload.getJSONArray("permission");

		return permissions.toList().stream().map(p -> Objects.toString(p, null)).collect(Collectors.toList());
	}

	public boolean isViewPermissionGranted(String token) {

		JSONObject tokenPayload = getDecodedTokenPayloadJson(token);
		List<String> permissions = getActiveUserPermissions(tokenPayload);

		return permissions.contains(KeycloakPermissionsConstants.P1);
	}

	public boolean isCreatePermissionGranted(String token) {

		JSONObject tokenPayload = getDecodedTokenPayloadJson(token);
		List<String> permissions = getActiveUserPermissions(tokenPayload);

		return permissions.contains(KeycloakPermissionsConstants.P2);
	}

	public boolean isEditPermissionGranted(String token) {

		JSONObject tokenPayload = getDecodedTokenPayloadJson(token);
		List<String> permissions = getActiveUserPermissions(tokenPayload);

		return permissions.contains(KeycloakPermissionsConstants.P3);
	}

	public boolean isDeletePermissionGranted(String token) {

		JSONObject tokenPayload = getDecodedTokenPayloadJson(token);
		List<String> permissions = getActiveUserPermissions(tokenPayload);

		return permissions.contains(KeycloakPermissionsConstants.P4);
	}
	
	public void validateAndSetActiveUserAuthorities(String token) {
		/**
		 *  Verify and validate active user's authorities and keep info in cache
		 */
		
		JSONObject tokenPayload = getDecodedTokenPayloadJson(token);
		List<String> permissions = getActiveUserPermissions(tokenPayload);

		// Set View Permission
		keycloakUserPermission.setViewPermissionEnabled(
				userPermissionConfigProperties.getView(), 
				permissions.contains(KeycloakPermissionsConstants.P1));
		
		// Set Create Permission
		keycloakUserPermission.setCreatePermissionEnabled(
				userPermissionConfigProperties.getCreate(), 
				permissions.contains(KeycloakPermissionsConstants.P2));
		
		// Set Edit Permission
		keycloakUserPermission.setEditPermissionEnabled(
				userPermissionConfigProperties.getEdit(), 
				permissions.contains(KeycloakPermissionsConstants.P3));
		
		// Set Delete Permission
		keycloakUserPermission.setDeletePermissionEnabled(
				userPermissionConfigProperties.getDelete(), 
				permissions.contains(KeycloakPermissionsConstants.P4));
		
	}

	public String getRealmNameFromToken(String token) {
		
		JSONObject tokenPayload = getDecodedTokenPayloadJson(token);
		String iss = (String)tokenPayload.get("iss");
		String [] splitUrl = iss.split("/");
		
		return splitUrl[splitUrl.length-1];
	}
	
}
