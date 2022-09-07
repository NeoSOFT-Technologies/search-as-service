package com.neosoft.app.domain.port.api;

import java.util.List;

import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.neosoft.app.infrastructure.entity.security.AppUser;
import com.neosoft.app.infrastructure.entity.security.Role;

public interface AppUserServicePort {
	AppUser saveUser(AppUser newAppUser);
	Role saveRole(Role role);
	void addRoleToAppUser(String username,String rolename);
	AppUser getUser(String username);
	List<AppUser>getUsers();
	void deleteUserByUsername(String username) throws UsernameNotFoundException;
	
}
