package com.searchservice.app.domain.dto.user;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode
public class User {
	
	private String username;
	private String password;

	public User(String userName, String password) {
		this.username = userName;
		this.password = password;
	}
}