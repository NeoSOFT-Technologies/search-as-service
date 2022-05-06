package com.searchservice.app.config.security;

import com.searchservice.app.domain.constants.InMemoryCacheConstants;
import com.searchservice.app.domain.port.spi.KeycloakConfigAdapterPort;
import com.searchservice.app.domain.utils.BasicUtil;
import com.searchservice.app.infrastructure.adaptor.KeycloakConfigAdapter.KeycloakConfigAdapterResponse;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Optional;

import javax.annotation.PostConstruct;


@Data
@NoArgsConstructor
@Component
public class KeycloakRolesManagement {
	
	private final Logger log = LoggerFactory.getLogger(KeycloakRolesManagement.class);

	@Value("${keycloak.auth-server-url}"+"${keycloak-url.realm}")
	private String keycloakClientRoleUrl;
	@Value("${keycloak.realm}")
	private String realmName;
	@Value("${keycloak-config.tenant-id-1}")
	private String saasClientTenantId1;
	
	@Autowired
	private KeycloakConfigAdapterPort keycloakConfigAdapterPort;
	
	
	@PostConstruct
	public void fetchKeycloakRolesAndMaintainCache() {
		
		// GET Roles details for a realm
		Optional<JSONArray> jsonArrayRealmRoles;
		if(!InMemoryCacheConstants.KEYCLOAK_REALM_ROLES.containsKey(realmName)) {
			String url = keycloakClientRoleUrl + "/" + "saas-realm" + "/roles";
			KeycloakConfigAdapterResponse rolesInfoResponse = keycloakConfigAdapterPort.getRolesInfo(url);
			
			Object tempObj = null;
			try {
				tempObj = new JSONTokener(rolesInfoResponse.getResponseString()).nextValue();
			} catch (JSONException e) {
		        log.error("Exception occurred while JSON parsing: ", e);
		    }
			
			if(tempObj instanceof JSONArray)
				jsonArrayRealmRoles = Optional.ofNullable((JSONArray)tempObj);
			else
				jsonArrayRealmRoles = Optional.ofNullable(new JSONArray(rolesInfoResponse.getResponseString()));
			InMemoryCacheConstants.KEYCLOAK_REALM_ROLES.put(
					realmName, 
					BasicUtil.parseJsonArrayToListOfMaps(jsonArrayRealmRoles.get()));
		} else {
			jsonArrayRealmRoles = Optional.ofNullable(new JSONArray(InMemoryCacheConstants.KEYCLOAK_REALM_ROLES.get(realmName)));
		}
		
		log.debug("jsonArray ####### {}", jsonArrayRealmRoles);
		
		// GET Roles details for a client/tenant
		Optional<JSONArray> jsonArrayTenantRoles1;
		if(!InMemoryCacheConstants.KEYCLOAK_CLIENT_ROLES.containsKey(saasClientTenantId1)) {
			String url = keycloakClientRoleUrl + "/" + realmName + "/clients" + "/" + saasClientTenantId1 + "/roles";
			KeycloakConfigAdapterResponse rolesInfoResponse = keycloakConfigAdapterPort.getRolesInfo(url);
			
			Object tempObj = null;
			try {
				tempObj = new JSONTokener(rolesInfoResponse.getResponseString()).nextValue();
			} catch (JSONException e) {
		        log.error("Exception occurred while JSON parsing: ", e);
		    }
			
			if(tempObj instanceof JSONArray)
				jsonArrayTenantRoles1 = Optional.ofNullable((JSONArray)tempObj);
			else
				jsonArrayTenantRoles1 = Optional.ofNullable(new JSONArray(rolesInfoResponse.getResponseString()));
			InMemoryCacheConstants.KEYCLOAK_CLIENT_ROLES.put(
					saasClientTenantId1, 
					BasicUtil.parseJsonArrayToListOfMaps(jsonArrayTenantRoles1.get()));
		} else {
			jsonArrayTenantRoles1 = Optional.ofNullable(new JSONArray(InMemoryCacheConstants.KEYCLOAK_CLIENT_ROLES.get(saasClientTenantId1)));
		}
		
		log.debug("jsonArray TenantId 1 ####### {}", jsonArrayTenantRoles1);
		
		// GET Roles details for current user
		Optional<JSONArray> jsonArrayUserRoles;
		if(!InMemoryCacheConstants.KEYCLOAK_CURRENT_USER_ROLES.containsKey(InMemoryCacheConstants.ACTIVE_USERNAME)) {
			String url = keycloakClientRoleUrl + "/" + realmName + "/users" + "/" + InMemoryCacheConstants.ACTIVE_USERNAME + "/role-mappings";
			KeycloakConfigAdapterResponse rolesInfoResponse = keycloakConfigAdapterPort.getRolesInfo(url);
			
			Object tempObj = null;
			try {
				tempObj = new JSONTokener(rolesInfoResponse.getResponseString()).nextValue();
			} catch (JSONException e) {
		        log.error("Exception occurred while JSON parsing: ", e);
		    }
			
			// testing
			System.out.println("rolesInfoResponse.getResponseString() ####### "+rolesInfoResponse.getResponseString());
			
			if(tempObj instanceof JSONArray) {
				jsonArrayUserRoles = Optional.ofNullable((JSONArray)tempObj);
				InMemoryCacheConstants.KEYCLOAK_CURRENT_USER_ROLES.put(
						InMemoryCacheConstants.ACTIVE_USERNAME, 
						BasicUtil.parseJsonArrayToListOfMaps(jsonArrayUserRoles.get()));
			}

		} else {
			jsonArrayUserRoles = Optional.ofNullable(new JSONArray(InMemoryCacheConstants.KEYCLOAK_CURRENT_USER_ROLES.get(InMemoryCacheConstants.ACTIVE_USERNAME)));
		}
		
		// GET details for current user
		Optional<JSONArray> jsonArrayUserDetails;
		if(!InMemoryCacheConstants.KEYCLOAK_CURRENT_USER_DETAILS.containsKey(InMemoryCacheConstants.ACTIVE_USERNAME)) {
			String url = keycloakClientRoleUrl + "/" + realmName + "/users" + "/" + InMemoryCacheConstants.ACTIVE_USERNAME;
			KeycloakConfigAdapterResponse userInfoResponse = keycloakConfigAdapterPort.getRolesInfo(url);
			
			Object tempObj = null;
			try {
				tempObj = new JSONTokener(userInfoResponse.getResponseString()).nextValue();
			} catch (JSONException e) {
		        log.error("Exception occurred while JSON parsing: ", e);
		    }
			
			// testing
			System.out.println("userInfoResponse.getResponseString() ####### "+userInfoResponse.getResponseString());
			
			if(tempObj instanceof JSONArray) {
				jsonArrayUserDetails = Optional.ofNullable((JSONArray)tempObj);
				InMemoryCacheConstants.KEYCLOAK_CURRENT_USER_DETAILS.put(
						InMemoryCacheConstants.ACTIVE_USERNAME, 
						BasicUtil.parseJsonArrayToListOfMaps(jsonArrayUserDetails.get()));
			}

		} else {
			jsonArrayUserDetails = Optional.ofNullable(new JSONArray(InMemoryCacheConstants.KEYCLOAK_CURRENT_USER_DETAILS.get(InMemoryCacheConstants.ACTIVE_USERNAME)));
		}
		
		
		// GET details for given client
		Optional<JSONArray> jsonArrayClientDetails;
		if(!InMemoryCacheConstants.KEYCLOAK_CLIENT_DETAILS.containsKey(saasClientTenantId1)) {
			String url = keycloakClientRoleUrl + "/" + realmName + "/clients" + "/" + saasClientTenantId1 + "/authz/resource-server/export-settings";
			KeycloakConfigAdapterResponse userInfoResponse = keycloakConfigAdapterPort.getRolesInfo(url);
			
			Object tempObj = null;			try {
				tempObj = new JSONTokener(userInfoResponse.getResponseString()).nextValue();
			} catch (JSONException e) {
		        log.error("Exception occurred while JSON parsing: ", e);
		    }
			
			// testing
			System.out.println("CLIENTInfoResponse.getResponseString() ####### "+userInfoResponse.getResponseString());
			
			if(tempObj instanceof JSONArray) {
				jsonArrayClientDetails = Optional.ofNullable((JSONArray)tempObj);
				InMemoryCacheConstants.KEYCLOAK_CLIENT_DETAILS.put(
						saasClientTenantId1, 
						BasicUtil.parseJsonArrayToListOfMaps(jsonArrayClientDetails.get()));
			}

		} else {
			jsonArrayClientDetails = Optional.ofNullable(new JSONArray(InMemoryCacheConstants.KEYCLOAK_CLIENT_DETAILS.get(saasClientTenantId1)));
		}
		
		
		// TESTING............
		Optional<JSONArray> jsonArrayCustomDetails;
		if(!InMemoryCacheConstants.KEYCLOAK_TEST_DETAILS.containsKey(saasClientTenantId1)) {
			String url = keycloakClientRoleUrl + "/" + realmName + "/clients" + "/" + "933b7340-7345-4529-ba62-6acbaabaafa9" + "/management/permissions";
			//String url = keycloakClientRoleUrl + "/" + realmName + "/client-scopes";
			KeycloakConfigAdapterResponse userInfoResponse = keycloakConfigAdapterPort.getRolesInfo(url);
			
			Object tempObj = null;			try {
				tempObj = new JSONTokener(userInfoResponse.getResponseString()).nextValue();
			} catch (JSONException e) {
		        log.error("Exception occurred while JSON parsing: ", e);
		    }
			
			// testing
			System.out.println("TEST INFO Response ##### "+userInfoResponse.getResponseString());
			
			if(tempObj instanceof JSONArray) {
				jsonArrayCustomDetails = Optional.ofNullable((JSONArray)tempObj);
				InMemoryCacheConstants.KEYCLOAK_TEST_DETAILS.put(
						saasClientTenantId1, 
						BasicUtil.parseJsonArrayToListOfMaps(jsonArrayCustomDetails.get()));
			}

		} else {
			jsonArrayCustomDetails = Optional.ofNullable(new JSONArray(InMemoryCacheConstants.KEYCLOAK_TEST_DETAILS.get(saasClientTenantId1)));
		}
		
		
		log.debug("key_cl_roles >>> {}", InMemoryCacheConstants.KEYCLOAK_CLIENT_ROLES);
		log.debug("key_realm_roles >>> {}", InMemoryCacheConstants.KEYCLOAK_REALM_ROLES);
		log.debug("key_user_roles >>> {}", InMemoryCacheConstants.KEYCLOAK_CURRENT_USER_ROLES);
		log.debug("key_user_details >>> {}", InMemoryCacheConstants.KEYCLOAK_CURRENT_USER_DETAILS);
		log.debug("key_client_details >>> {}", InMemoryCacheConstants.KEYCLOAK_CLIENT_DETAILS);

	}
	
	
	
//	http://localhost:8080/auth/admin/realms/saas-realm/clients/1d68fd1b-7f05-4856-b268-7b19cd572dab/management/permissions
	
//	evaluate-scopes/generate-example-userinfo
//	authz/resource-server/export-settings
//	/users/{id}/role-mappings/clients/{client}
//	/client-scopes
}
