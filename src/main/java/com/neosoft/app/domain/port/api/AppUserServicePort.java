package com.neosoft.app.domain.port.api;

import java.util.List;

import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.neosoft.app.domain.dto.security.AppUserDTO;

public interface AppUserServicePort {
	AppUserDTO saveAppUser(AppUserDTO newAppUserDTO);
	void addRoleToAppUser(String username, String rolename);
	AppUserDTO getAppUser(String username);
	List<AppUserDTO>getAppUsers();
	void deleteAppUserByUsername(String username) throws UsernameNotFoundException;
	
}
