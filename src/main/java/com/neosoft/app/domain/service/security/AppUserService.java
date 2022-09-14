package com.neosoft.app.domain.service.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.neosoft.app.domain.dto.security.AppUserDTO;
import com.neosoft.app.domain.port.api.AppUserServicePort;
import com.neosoft.app.domain.port.spi.AppUserPersistencePort;
import com.neosoft.app.rest.errors.CustomException;
import com.neosoft.app.rest.errors.HttpStatusCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AppUserService implements AppUserServicePort, UserDetailsService {

	private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
	
	@Autowired
	private final AppUserPersistencePort appUserPersistencePort;
	

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		AppUserDTO user = getAppUser(username);
		if (user == null) {
			throw new UsernameNotFoundException("Username not found: " + username);
		}

		Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
		user.getRoles().forEach(role -> {
			authorities.add(new SimpleGrantedAuthority(role.getName()));
		});

		return new User(user.getUsername(), user.getPassword(), authorities);
	}

	@Override
	public AppUserDTO saveAppUser(AppUserDTO newAppUserDTO) {
		newAppUserDTO.setPassword(bCryptPasswordEncoder.encode(newAppUserDTO.getPassword()));
		Optional<AppUserDTO> saveUser = appUserPersistencePort.addAppUser(newAppUserDTO);
		
		if(saveUser.isPresent()) {
			return saveUser.get();
		} else
			return null;
		
	}

	@Override
	public void addRoleToAppUser(String username, String rolename) {
		appUserPersistencePort.addRoleToAppUser(username, rolename);
			
	}

	@Override
	public AppUserDTO getAppUser(String username) {
		Optional<AppUserDTO> saveUser = appUserPersistencePort.getAppUser(username);
		if(saveUser.isPresent()) {
			return saveUser.get();
		} else
			return null;
	}

	@Override
	public List<AppUserDTO> getAppUsers() {
		return appUserPersistencePort.getAllAppUsers();
	}

	@Override
	public void deleteAppUserByUsername(String username) throws UsernameNotFoundException {
		boolean isDeleted = appUserPersistencePort.removeAppUserByUsername(username);
		if(!isDeleted)
			throw new CustomException(
					HttpStatusCode.ENTITY_NOT_FOUND.getCode(), 
					HttpStatusCode.ENTITY_NOT_FOUND, 
					HttpStatusCode.ENTITY_NOT_FOUND.getMessage());
	}

}
