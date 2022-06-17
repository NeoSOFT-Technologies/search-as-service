package com.searchservice.app.domain.dto.user;

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

	public User(String username, String password, String tenantName) {
		this.username = username;
		this.password = password;
		this.tenantName = tenantName;
	}
}