package com.searchservice.app.rest;

import java.util.Collection;

import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.searchservice.app.domain.service.KeycloakAdminClientService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;


/**
 * We will be using
 * https://www.keycloak.org/docs-api/9.0/javadocs/org/keycloak/admin/client/resource/RolesResource.html
 * API.
 * 
 * @author Edward P. Legaspi | czetsuya@gmail.com
 * 
 * @version 0.0.1
 * @since 0.0.1
 */
@RestController
@RequestMapping(path = "/user/token", produces = MediaType.APPLICATION_JSON_VALUE)
public class KeycloakController {

    @Autowired
    private KeycloakAdminClientService keycloakAdminClientService;

    @GetMapping(path = "/test1-hello")
    @Operation(summary = "HELLO THERE.....", security = @SecurityRequirement(name = "bearerAuth"))
    public String hello() {
        return "Hello World!";
    }

    @GetMapping(path = "/test1-roles")
    @Operation(summary = "HELLO THERE.....", security = @SecurityRequirement(name = "bearerAuth"))
    public Collection<String> rolesOfCurrentUser() {
        return keycloakAdminClientService.getCurrentUserRoles();
    }

    @GetMapping(path = "/test1-profile")
    @Operation(summary = "HELLO THERE.....", security = @SecurityRequirement(name = "bearerAuth"))
    public Object profileOfCurrentUser() {
        return keycloakAdminClientService.getUserProfileOfLoggedUser();
    }
    
    @PutMapping(path = "/test1-updateroles")
    @Operation(summary = "HELLO THERE.....", security = @SecurityRequirement(name = "bearerAuth"))
    public Object updateRolesOfCurrentUserAsPerPermissionsAllowed(@RequestParam String username) {
        return keycloakAdminClientService.updateExistingUserRoles(username);
    }
    
    @PutMapping("/test1-updateuser")
    @Operation(summary = "HELLO THERE.....", security = @SecurityRequirement(name = "bearerAuth"))
    public Object updateUserDescriptionAttribute(@RequestParam String username,
                                                 @RequestParam String description) {
    	return keycloakAdminClientService.updateExistingUser(username, description);
    }
}
