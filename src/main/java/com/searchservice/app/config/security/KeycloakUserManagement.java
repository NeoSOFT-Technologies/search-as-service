package com.searchservice.app.config.security;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
public class KeycloakUserManagement {

	@Value("${keycloak.realm}")
	private String realmName;

	@Value("${keycloak.auth-server-url}")
	private String keycloakServerUrl;

	@Value("${keycloak.resource}")
	private String clientId;

	@Value("${keycloak.credentials.secret}")
	private String clientSecret;
	
	public UserRepresentation updateUserAttribute(String username, String password, String description) {
		
		Keycloak keycloak = getKeycloakInstance(username, password);
		
		///////////
		keycloak.tokenManager().getAccessToken();
		RealmResource realmResource = keycloak.realm("realm-name");
		
		System.out.println(" check realmResource >>>> "+realmResource);
		///////////
		
		Optional<UserRepresentation> user = keycloak.realm(realmName).users().search(username).stream()
				.filter(u -> u.getUsername().equals(username)).findFirst();
		if (user.isPresent()) {
			UserRepresentation userRepresentation = user.get();
			UserResource userResource = keycloak.realm(realmName).users().get(userRepresentation.getId());
			Map<String, List<String>> attributes = new HashMap<>();
			attributes.put("description", Arrays.asList(description));
			userRepresentation.setAttributes(attributes);
			userResource.update(userRepresentation);
			return userRepresentation;
		} else {
			return new UserRepresentation();
		}

	}


	private Keycloak getKeycloakInstance(String username, String password) {
		// Using a user
//		return KeycloakBuilder.builder()
//	            .grantType("password")
//	            .serverUrl(keycloakServerUrl)
//	            .realm(realmName)
//	            .clientId(clientId)
//	            .clientSecret(clientSecret)
//	            .username(username)
//	            .password(password)
//	            .build();
		
		// Using a confidential service account
		return KeycloakBuilder.builder()
	            .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
	            .serverUrl(keycloakServerUrl)
	            .realm(realmName)
	            .clientId(clientId)
	            .clientSecret(clientSecret)
	            .resteasyClient(
	                    new ResteasyClientBuilder()
	                    .connectionPoolSize(10).build()
	            )
	            .build();
	}
	
	
//	@PostConstruct
//	public void testUpdateUser() {
//		UserRepresentation userRepresentation = updateUserAttribute("kirtiji", "admin", "I am Inevitable!!");
//		
//		System.out.println("check userRepresentation >>> "+userRepresentation);
//		
//	}
	
}
