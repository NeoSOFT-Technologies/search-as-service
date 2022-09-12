
package com.neosoft.app.domain.dto.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;


@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
@JsonIgnoreProperties("id")
public class AppUserDTO {

	private Long id;
	private String firstName;
	private String lastName;
	private String email;
	private Date dob;
	private String username;
	private String password;
	private Collection<RoleDTO> roles = new ArrayList<>();

}
