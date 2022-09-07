package com.neosoft.app.rest;

import java.net.URI;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.neosoft.app.domain.dto.security.RoleToUserDTO;
import com.neosoft.app.domain.port.api.AppUserServicePort;
import com.neosoft.app.infrastructure.entity.security.AppUser;
import com.neosoft.app.infrastructure.entity.security.Role;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;


@RestController
@RequestMapping("${base-url.api-endpoint.app-user}")
public class AppUserResource {
	
	private final AppUserServicePort libUserServicePort;
	
	public AppUserResource(AppUserServicePort libUserServicePort) {
		super();
		this.libUserServicePort = libUserServicePort;
	}
	
	@GetMapping
	@Operation(summary = "GET ALL USERS FROM DATABASE.", security = @SecurityRequirement(name = "bearerAuth"))
	public ResponseEntity<?> getusers(){
		try {
			return ResponseEntity.ok().body(libUserServicePort.getUsers());
		}
		catch(Exception e) {
			return new ResponseEntity<>("Cannot get users - " + e.getMessage(),HttpStatus.BAD_REQUEST);
		}
	}
	
	@PostMapping
	@Operation(summary = "REGISTER THE NEW USER.", security = @SecurityRequirement(name = "bearerAuth"))
	public ResponseEntity<AppUser> saveAppUser(@RequestBody AppUser newAppUser){
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/user/save").toString());
		return ResponseEntity.created(uri).body(libUserServicePort.saveUser(newAppUser));
	}
	
	@PostMapping("/role")
	@Operation(summary = "SAVE A NEW ROLE IN THE DATABASE.", security = @SecurityRequirement(name = "bearerAuth"))
	public ResponseEntity<Role> saveRole(@RequestBody Role newRole){
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/role/save").toString());
		return ResponseEntity.created(uri).body(libUserServicePort.saveRole(newRole));
	}
	
	@PostMapping("/role/add-role-to-user")
	@Operation(summary = "ADD ROLE TO AN EXISTING USER.", security = @SecurityRequirement(name = "bearerAuth"))
	public ResponseEntity<String> addRoleToUser(@RequestBody RoleToUserDTO form){
		libUserServicePort.addRoleToAppUser(form.getUsername(), form.getRolename());
		return ResponseEntity.ok().body("User Role Added");
	}
	
	@DeleteMapping
	@Operation(summary = "DELETE AN EXISTING USER.", security = @SecurityRequirement(name = "bearerAuth"))
	public ResponseEntity<?> deleteUserByUsername(@RequestParam String username){
		try {
			libUserServicePort.deleteUserByUsername(username);
			return ResponseEntity.ok().body(username + " is deleted");
		}
		catch(Exception e) {
			 return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Deletion Failed");
		}
	}
	
}
