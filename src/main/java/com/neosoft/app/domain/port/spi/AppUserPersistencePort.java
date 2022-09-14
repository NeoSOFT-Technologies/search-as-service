package com.neosoft.app.domain.port.spi;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.neosoft.app.domain.dto.security.AppUserDTO;


@Component
public interface AppUserPersistencePort {

	List<AppUserDTO> getAllAppUsers();
	Optional<AppUserDTO> getAppUser(String username);
	boolean isAppUserExistsByUsername(String username);
	Optional<AppUserDTO> addAppUser(AppUserDTO appUserDTO);
	boolean removeAppUserByUsername(String username);
	public void addRoleToAppUser(String username, String rolename);
}
