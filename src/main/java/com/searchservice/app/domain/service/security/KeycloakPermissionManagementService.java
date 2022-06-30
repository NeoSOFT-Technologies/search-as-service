package com.searchservice.app.domain.service.security;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import com.searchservice.app.config.AuthConfigProperties;
import com.searchservice.app.config.TenantInfoConfigProperties;
import com.searchservice.app.config.UserPermissionConfigProperties;
import com.searchservice.app.rest.errors.CustomException;
import com.searchservice.app.rest.errors.HttpStatusCode;

@Service
public class KeycloakPermissionManagementService {

	private static final String TENANT_KEY = "tenantInfo";

	@Autowired 
	private UserPermissionConfigProperties userPermissionConfigProperties;
	
	@Autowired
	KeycloakUserPermission keycloakUserPermission;
	
	@Autowired
	AuthConfigProperties authConfigProperties;

	@Autowired
	TenantInfoConfigProperties tenantInfoConfigProperties;
	
	@Autowired
	private CacheManager cacheManager;
	
	@Nullable
	Cache cache;

	
	public JSONObject getDecodedTokenPayloadJson(String token) {
		try {
			String payload = token.split("\\.")[1];
			
			String decodedPayload = new String(Base64.decodeBase64(payload), StandardCharsets.UTF_8);

			return new JSONObject(decodedPayload);

		} catch (JSONException e) {
			throw new CustomException(
					HttpStatusCode.INVALID_JSON_INPUT.getCode(), 
					HttpStatusCode.INVALID_JSON_INPUT, 
					HttpStatusCode.INVALID_JSON_INPUT.getMessage());
		} catch(Exception e) {
			throw new CustomException(
					HttpStatusCode.BAD_REQUEST_EXCEPTION.getCode(), 
					HttpStatusCode.BAD_REQUEST_EXCEPTION, 
					"Invalid token!");
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


	// Add Realm Name in cache
	@Cacheable(cacheNames = TENANT_KEY, key = "#tenant", condition = "#tenant!=null")
	public String getRealmNameFromToken(String tenant, String token) {
		JSONObject tokenPayload = getDecodedTokenPayloadJson(token);
		String iss = (String)tokenPayload.get("iss");
		String [] splitUrl = iss.split("/");
		String realmName = splitUrl[splitUrl.length-1];
		
		authConfigProperties.setRealmName(realmName);

		return realmName;
	}
	
	// Fetch Realm Name from cache if present
	public String getRealmNameFromCache(String tenantName) {
	    cache = cacheManager.getCache(tenantInfoConfigProperties.getKey());
	    
	    String realmName = null;
	    if(cache == null)
	    	throw new CustomException(
	    			HttpStatusCode.NULL_POINTER_EXCEPTION.getCode(), 
	    			HttpStatusCode.NULL_POINTER_EXCEPTION, 
	    			HttpStatusCode.NULL_POINTER_EXCEPTION.getMessage());
	    else {
	    	try {
	    		Optional<ValueWrapper> tenantInfoValueWrapper = Optional.of(cache.get(tenantName));
	    		ValueWrapper tenantInfoValue = tenantInfoValueWrapper.get();
	    		if(tenantInfoValue.get() != null) {
	    			Object obj = tenantInfoValue.get();
	    			if(obj != null)
	    				realmName = obj.toString();
	    		}
	    	} catch(Exception e) {
		    	throw new CustomException(
		    			HttpStatusCode.NULL_POINTER_EXCEPTION.getCode(), 
		    			HttpStatusCode.NULL_POINTER_EXCEPTION, 
		    			HttpStatusCode.NULL_POINTER_EXCEPTION.getMessage());
	    	}


		    return realmName;
	    }
	}
	
	public boolean checkIfRealmNameExistsInCache(String tenantName) {
	    cache = cacheManager.getCache(tenantInfoConfigProperties.getKey());
	    if(cache == null)
	    	throw new CustomException(
	    			HttpStatusCode.NULL_POINTER_EXCEPTION.getCode(), 
	    			HttpStatusCode.NULL_POINTER_EXCEPTION, 
	    			HttpStatusCode.NULL_POINTER_EXCEPTION.getMessage());
	    else {
	    	return (cache.get(tenantName)!=null);
	    }
	}
	
}
