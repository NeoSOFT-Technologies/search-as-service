package com.searchservice.app.domain.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.keycloak.KeycloakPrincipal;
import org.keycloak.adapters.RefreshableKeycloakSecurityContext;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import com.searchservice.app.config.security.CurrentUserProvider;
import com.searchservice.app.config.security.KeycloakAdminClientConfig;
import com.searchservice.app.config.security.KeycloakAdminClientUtils;
import com.searchservice.app.config.security.KeycloakPropertyReader;
import com.searchservice.app.domain.constant.KeycloakRolesIMC;


/**
 * @author Edward P. Legaspi | czetsuya@gmail.com
 * 
 * @version 0.0.1
 * @since 0.0.1
 */
@Service
public class KeycloakAdminClientService {

    @Value("${keycloak.resource}")
    private String keycloakClient;
    
    @Value("${mykc.res}")
    private String keycloakClient2;
    
    @Value("${mykc.sec}")
    private String keycloakClientSecret2;

    @Autowired
    private CurrentUserProvider currentUserProvider;

    @Autowired
    private KeycloakPropertyReader keycloakPropertyReader;

    
    public List<String> getCurrentUserRoles() {
        return currentUserProvider.getCurrentUser().getRoles();
    }

    public Object getUserProfileOfLoggedUser() {

        @SuppressWarnings("unchecked")
        KeycloakPrincipal<RefreshableKeycloakSecurityContext> principal = (KeycloakPrincipal<RefreshableKeycloakSecurityContext>) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        KeycloakAdminClientConfig keycloakAdminClientConfig = KeycloakAdminClientUtils.loadConfig(keycloakPropertyReader);
        Keycloak keycloak = KeycloakAdminClientUtils.getKeycloakClient(principal.getKeycloakSecurityContext(), keycloakAdminClientConfig);

        // Get realm
        RealmResource realmResource = keycloak.realm(keycloakAdminClientConfig.getRealm());
        UsersResource usersResource = realmResource.users();
        UserResource userResource = usersResource.get(currentUserProvider.getCurrentUser().getUserId());
        UserRepresentation userRepresentation = userResource.toRepresentation();

        return userRepresentation;
    }
    
    public Object updateRolesOfLoggedUser() {

        @SuppressWarnings("unchecked")
        KeycloakPrincipal<RefreshableKeycloakSecurityContext> principal = (KeycloakPrincipal<RefreshableKeycloakSecurityContext>) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        KeycloakAdminClientConfig keycloakAdminClientConfig = KeycloakAdminClientUtils.loadConfig(keycloakPropertyReader);
        Keycloak keycloak = KeycloakAdminClientUtils.getKeycloakClient(principal.getKeycloakSecurityContext(), keycloakAdminClientConfig);

        // Get realm
        RealmResource realmResource = keycloak.realm(keycloakAdminClientConfig.getRealm());
        UsersResource usersResource = realmResource.users();
        UserResource userResource = usersResource.get(currentUserProvider.getCurrentUser().getUserId());
        UserRepresentation userRepresentation = userResource.toRepresentation();

        
        KeycloakAdminClientUtils.addRoleToListOf(keycloak, keycloakAdminClientConfig, keycloakClient, "writeeeeee", "");
        
        
        return userRepresentation;
    }
    
    
	public void updateUserRoles() {

        @SuppressWarnings("unchecked")
        KeycloakPrincipal<RefreshableKeycloakSecurityContext> principal = (KeycloakPrincipal<RefreshableKeycloakSecurityContext>) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        KeycloakAdminClientConfig keycloakAdminClientConfig = KeycloakAdminClientUtils.loadConfig(keycloakPropertyReader);
        Keycloak keycloak = KeycloakAdminClientUtils.getKeycloakClient(principal.getKeycloakSecurityContext(), keycloakAdminClientConfig);

//		List<RoleRepresentation> rolesOfUserActual = keycloak.realm(keycloakAdminClientConfig.getRealm()).users()
//				.get(KeycloakRolesIMC.ACTIVE_USERNAME).roles().realmLevel().listAll();
//		List<RoleRepresentation> rolesOfUserActualNew = keycloak.realm(keycloakAdminClientConfig.getRealm()).users()
//				.get(KeycloakRolesIMC.ACTIVE_USERNAME).roles().realmLevel().listAll();

		RoleRepresentation newrole = new RoleRepresentation("ROLE_SONG_OF_THUNDERrrrrrrrrr", null, false); // this role already
																								// exists in keycloak.
//		rolesOfUserActualNew.add(newrole);
//
//		List<RoleRepresentation> differences = rolesOfUserActualNew.stream()
//				.filter(name -> !rolesOfUserActual.contains(name))
//				.collect(Collectors.toList());
//
//		List<RoleRepresentation> roleToAdd = new ArrayList<>();
//		List<RoleRepresentation> roleToDelete = new ArrayList();
//
//		differences.forEach((role) -> {
//			if (rolesOfUserActual.contains(role)) {
//				roleToDelete.add(role);
//			} else {
//				roleToAdd.add(role);
//			}
//		});
//		
//		// testing
//		System.out.println("rolesOfUserActual ??? "+rolesOfUserActual);
//		System.out.println("rolesOfUserActualNew ???? "+rolesOfUserActualNew);
//		System.out.println("differences ???? "+differences);
//		System.out.println("roleToAdd ??? "+roleToAdd);
//		System.out.println("roleToDelete ??? "+roleToDelete);

//		keycloak.realm(keycloakAdminClientConfig.getRealm()).users().get(KeycloakRolesIMC.ACTIVE_USERNAME).roles().realmLevel().add(roleToAdd);
//		keycloak.realm(keycloakAdminClientConfig.getRealm()).users().get(KeycloakRolesIMC.ACTIVE_USERNAME).roles().realmLevel()
//				.remove(roleToDelete);

		System.out.println("start method 2............~~~~~~~~~~~");
		
		// Method 2
		// Get client
		RealmResource realmResource = keycloak.realm(keycloakAdminClientConfig.getRealm());
		ClientRepresentation app1Client = realmResource.clients()
				.findByClientId(keycloakAdminClientConfig.getClientId()).get(0);
		
		// testing
		System.out.println("app1Client !!!!! "+app1Client.getClientId());
		
		ClientRepresentation app2Client = realmResource.clients()
				.findByClientId(keycloakClient2).get(0);
		
		// testing
		System.out.println("app2Client !!!!! "+app2Client.getClientId());
		
		// Get client level role (requires view-clients role)
		RoleRepresentation userClientRole = realmResource.clients().get(app2Client.getId()) //
				.roles().get("user").toRepresentation();
		
		// testing
		System.out.println("userClientRole !!!!! "+userClientRole);	
		// Assign client level role to user
        UsersResource usersResource = realmResource.users();
        
		// testing
		System.out.println("usersResource !!!!! "+usersResource);	
        
        UserResource userResource = usersResource.get(currentUserProvider.getCurrentUser().getUserId());
        
		// testing
		System.out.println("userResource !!!!! "+userResource);	
        
		userResource.roles() //
				.clientLevel(app1Client.getId()).add(Arrays.asList(userClientRole));
		
		
		System.out.println();
		
	}
	
	
	public UserRepresentation updateExistingUser(String username, String description) {
        @SuppressWarnings("unchecked")
        KeycloakPrincipal<RefreshableKeycloakSecurityContext> principal = (KeycloakPrincipal<RefreshableKeycloakSecurityContext>) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        KeycloakAdminClientConfig keycloakAdminClientConfig = KeycloakAdminClientUtils.loadConfig(keycloakPropertyReader);
        Keycloak keycloak = KeycloakAdminClientUtils.getKeycloakClient(principal.getKeycloakSecurityContext(), keycloakAdminClientConfig);

       Optional<UserRepresentation> user = keycloak.realm(keycloakAdminClientConfig.getRealm())
    		   .users().search(username)
    		   .stream()
               .filter(u -> u.getUsername().equals(username)).findFirst();
        if (user.isPresent()) {
            UserRepresentation userRepresentation = user.get();
            UserResource userResource = keycloak.realm(keycloakAdminClientConfig.getRealm()).users().get(userRepresentation.getId());
            Map<String, List<String>> attributes = new HashMap<>();
            attributes.put("skd-description", Arrays.asList(description));
            userRepresentation.setAttributes(attributes);
            userResource.update(userRepresentation);
            return userRepresentation;
        } else {
            return null;
        }
	}
	
	
	public UserRepresentation updateExistingUserRoles(String username) {
        @SuppressWarnings("unchecked")
        KeycloakPrincipal<RefreshableKeycloakSecurityContext> principal
        	= (KeycloakPrincipal<RefreshableKeycloakSecurityContext>) SecurityContextHolder
        	.getContext()
        	.getAuthentication().getPrincipal();
        KeycloakAdminClientConfig keycloakAdminClientConfig = KeycloakAdminClientUtils.loadConfig(keycloakPropertyReader);
        Keycloak keycloak = KeycloakAdminClientUtils.getKeycloakClient(principal.getKeycloakSecurityContext(), keycloakAdminClientConfig);
        //Keycloak keycloak = KeycloakAdminClientUtils.getKeycloakClientRestEasy(keycloakAdminClientConfig);

        System.out.println("CHECK OUT THE TOKEN ~~~~~~~~ :: "+principal.getKeycloakSecurityContext().getTokenString());
        
        // testing
        System.out.println("getting realm res......####");
        
        RealmResource realmResource = keycloak.realm(keycloakAdminClientConfig.getRealm());
        
        System.out.println("getting user repr...###");
        
        Optional<UserRepresentation> user = realmResource
								    		   .users().search(username)
								    		   .stream()
								               .filter(u -> u.getUsername().equals(username)).findFirst();
        if (user.isPresent()) {
        	
        	System.out.println("user is present...##");
        	
            UserRepresentation userRepresentation = user.get();
            
            System.out.println("getting user resource...###");
            
            UserResource userResource = realmResource.users().get(userRepresentation.getId());

//            Map<String, List<String>> attributes = new HashMap<>();
//            attributes.put("skd-description", Arrays.asList(description));
//            userRepresentation.setAttributes(attributes);

            // UPDATE ROLES
    		// Get client
            
            System.out.println("getting client 1...####");
            
    		ClientRepresentation app1Client = realmResource.clients()
    				.findByClientId(keycloakAdminClientConfig.getClientId()).get(0);
    		// testing
    		System.out.println("app1Client !!!!! "+app1Client.getClientId());
    		
    		ClientRepresentation app2Client = realmResource.clients()
    				.findByClientId(keycloakClient2).get(0);
    		// testing
    		System.out.println("app2Client !!!!! "+app2Client.getClientId());
    		
    		// Get client level role (requires view-clients role)
    		RoleRepresentation userClientRole = realmResource.clients().get(app2Client.getId()) //
    				.roles().get("user").toRepresentation();
    		// testing
    		System.out.println("userClientRole !!!!! "+userClientRole);	
    		
    		// Assign client level role to user
            UsersResource usersResource = realmResource.users();
    		// testing
    		System.out.println("usersResource !!!!! "+usersResource);	
            
            //UserResource userResource = usersResource.get(currentUserProvider.getCurrentUser().getUserId());	

    		// testing
    		System.out.println("userResource roles !!!!! "+userResource.roles().clientLevel(app1Client.getId()).listAll());
    		userResource.roles()
    				.clientLevel(app1Client.getId()).add(Arrays.asList(userClientRole));
//    		userResource.roles()
//			.clientLevel(app1Client.getId()).listAll().add(userClientRole);
//    		userResource.roles()
//			.clientLevel("117acfd3-ea7b-4df2-857a-1a51e6a65b89").add(Arrays.asList(userClientRole));

    		
            userResource.update(userRepresentation);
            return userRepresentation;
        } else {
            return null;
        }
	}
}
