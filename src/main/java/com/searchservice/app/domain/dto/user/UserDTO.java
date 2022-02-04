package com.searchservice.app.domain.dto.user;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode
public class UserDTO {
	
	private String userName;
	private String password;

	public UserDTO(String userName, String password) {
		this.userName = userName;
		this.password = password;
	}
}
