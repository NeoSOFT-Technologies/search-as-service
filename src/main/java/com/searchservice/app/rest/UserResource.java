package com.searchservice.app.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.searchservice.app.domain.dto.Response;
import com.searchservice.app.domain.dto.user.User;
import com.searchservice.app.domain.port.api.PublicKeyServicePort;
import com.searchservice.app.domain.port.api.UserServicePort;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/user/token")
public class UserResource {
	
	private final UserServicePort userServicePort;


    public UserResource(UserServicePort userServicePort) {
        this.userServicePort = userServicePort;
    }
	
	@PostMapping
    @Operation(summary = "/ GET AUTHORIZED TOKEN BY PROVIDING USERNAME AND PASSWORD ")
    public ResponseEntity<Response> getToken(@RequestBody User userDTO) {
        Response responseDTO = userServicePort.getToken(userDTO);
        if(responseDTO.getStatusCode()==200){
            return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseDTO);
        }

    }
	

}