package com.neosoft.app.rest;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.neosoft.app.domain.dto.security.LoginDTO;
import com.neosoft.app.domain.dto.security.LoginResponse;
import com.neosoft.app.domain.utils.security.AuthUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@RestController
@RequestMapping("${base-url.api-endpoint.login}")
public class LoginResource {
	
	@Autowired
	AuthenticationManager authenticationManager;
	
	@Autowired
	AuthUtil authUtil;

	
	@PostMapping
	@Operation(summary = "AUTHENTICATE THE INCOMING USER.", security = @SecurityRequirement(name = "bearerAuth"))
	public ResponseEntity<LoginResponse> authenticateUser(@Valid @RequestBody LoginDTO loginRequest) {
		
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = authUtil.generateJwtToken(authentication);

		User userDetails = (User) authentication.getPrincipal();		
		List<String> roles = userDetails.getAuthorities().stream()
				.map(GrantedAuthority::getAuthority)
				.collect(Collectors.toList());
		return ResponseEntity.ok(new LoginResponse(jwt, 
												 userDetails.getUsername(),  
												 roles));
	}

}
