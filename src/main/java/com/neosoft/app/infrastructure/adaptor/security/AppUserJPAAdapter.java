package com.neosoft.app.infrastructure.adaptor.security;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.neosoft.app.domain.dto.security.AppUserDTO;
import com.neosoft.app.domain.dto.security.RoleDTO;
import com.neosoft.app.domain.port.spi.AppUserPersistencePort;
import com.neosoft.app.domain.port.spi.RolePersistencePort;
import com.neosoft.app.infrastructure.entity.security.AppUser;
import com.neosoft.app.infrastructure.entity.security.Role;
import com.neosoft.app.infrastructure.repository.security.AppUserRepository;

import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@Service
@Transactional
public class AppUserJPAAdapter implements AppUserPersistencePort {

	@Autowired
	private final AppUserRepository appUserRepository;
	
	@Autowired
	private final AppUserEntityMapper appUserEntityMapper;
	
	@Autowired
	private final RolePersistencePort rolePersistencePort;
	
	private final RoleEntityMapper roleEntityMapper;
	

	@Override
	public List<AppUserDTO> getAllAppUsers() {
		return appUserEntityMapper.entitiesToDTOs(appUserRepository.findAll());
	}

	@Override
	public Optional<AppUserDTO> getAppUser(String username) {
		Optional<AppUser> getAppUser = Optional.ofNullable(appUserRepository.findByUsername(username));
		
		if(getAppUser.isPresent()) {
			return Optional.ofNullable(appUserEntityMapper.entityToDTO(getAppUser.get()));
		} else
			return Optional.ofNullable(null);
		
	}

	@Override
	public boolean isAppUserExistsByUsername(String username) {
		return appUserRepository.existsByUsername(username);
	}

	@Override
	public Optional<AppUserDTO> addAppUser(AppUserDTO appUserDTO) {
		return Optional.ofNullable(
				appUserEntityMapper.entityToDTO(
						appUserRepository.save(appUserEntityMapper.dtoToEntity(appUserDTO))));
	}

	@Override
	public boolean removeAppUserByUsername(String username) {
		if(isAppUserExistsByUsername(username)) {
			appUserRepository.deleteByUsername(username);
			return true;
		} else
			return false;
	}
	
	@Override
	public void addRoleToAppUser(String username, String rolename) {
		Optional<AppUserDTO> tempAppUser = getAppUser(username);
		Optional<RoleDTO> tempRole = rolePersistencePort.getOneByName(rolename);
		if(tempAppUser.isPresent() && tempRole.isPresent()) {
			AppUser appUser = appUserEntityMapper.dtoToEntity(tempAppUser.get());
			Role role = roleEntityMapper.dtoToEntity(tempRole.get());
			appUser.getRoles().add(role);
		}
			
	}
	
}
