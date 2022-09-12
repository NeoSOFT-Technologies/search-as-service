package com.neosoft.app.domain.service.security;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.neosoft.app.domain.dto.security.RoleDTO;
import com.neosoft.app.domain.port.api.RoleServicePort;
import com.neosoft.app.domain.port.spi.RolePersistencePort;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class RoleService implements RoleServicePort {

	@Autowired
	private final RolePersistencePort rolePersistencePort;


	@Override
	public RoleDTO saveRole(RoleDTO newRoleDTO) {
		Optional<RoleDTO> saveUser = rolePersistencePort.addOne(newRoleDTO);
		
		if(saveUser.isPresent()) {
			return saveUser.get();
		} else
			return null;
		
	}

	@Override
	public void deleteAppUserByUsername(String username) {
		rolePersistencePort.removeOne(username);
	}

}
