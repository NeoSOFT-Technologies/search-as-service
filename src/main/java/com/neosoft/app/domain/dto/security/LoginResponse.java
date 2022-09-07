package com.neosoft.app.domain.dto.security;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoginResponse {
  private String token;
  private String type = "Bearer";
  private Long id;
  private String username;
  private String email;
  private List<String> roles;

  public LoginResponse(String token, Long id, String username, String email, List<String> roles) {
    this.token = token;
    this.id = id;
    this.username = username;
    this.email = email;
    this.roles = roles;
  }
  
	public LoginResponse(String token, String username, List<String> roles) {
		this.token = token;
		this.username = username;
		this.roles = roles;
	}

}
