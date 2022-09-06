package com.neosoft.app.domain.dto.user;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode
public class User {
	
	private String tenantName;
	private String username;
	private String password;

	public User(String tenantName, String username, String password) {
		this.tenantName = tenantName;
		this.username = username;
		this.password = password;
	}
	
	public User(String username, String password) {
		this.username = username;
		this.password = password;
	}
}