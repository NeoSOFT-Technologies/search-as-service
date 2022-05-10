package com.searchservice.app.domain.constant;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KeycloakRolesIMC {

	private KeycloakRolesIMC() {}
	
	/**
	 * Contains roles info for a given <client-id> on keycloak server
	 */
	public static final Map<String, List<Map<String, Object>>> KEYCLOAK_CLIENT_ROLES = new HashMap<>();
	
	/**
	 * Contains roles info for a given <realm-name> on keycloak server
	 */
	public static final Map<String, List<Map<String, Object>>> KEYCLOAK_REALM_ROLES = new HashMap<>();
	
	/**
	 * Contains roles info for the current <user> on keycloak server
	 */
	public static final Map<String, List<Map<String, Object>>> KEYCLOAK_CURRENT_USER_ROLES = new HashMap<>();
	
	/**
	 * Contains info about the current <user> on keycloak server
	 */
	public static final Map<String, List<Map<String, Object>>> KEYCLOAK_CURRENT_USER_DETAILS = new HashMap<>();
	
	/**
	 * Contains info about the current <user> on keycloak server
	 */
	public static final Map<String, List<Map<String, Object>>> KEYCLOAK_CLIENT_DETAILS = new HashMap<>();
	
	/**
	 * Contains info about the current test on keycloak server
	 */
	public static final Map<String, List<Map<String, Object>>> KEYCLOAK_TEST_DETAILS = new HashMap<>();
	
	public static final String ACTIVE_USERNAME = "3ab51da9-5a55-477d-aab7-e1b5dbb40959";
}
