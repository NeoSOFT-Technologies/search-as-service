package com.neosoft.app.infrastructure.adaptor.security;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.neosoft.app.domain.dto.security.RoleDTO;
import com.neosoft.app.domain.port.spi.RolePersistencePort;
import com.neosoft.app.infrastructure.entity.security.Role;
import com.neosoft.app.infrastructure.repository.security.RoleRepository;

import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@Service
@Transactional
public class RoleJPAAdapter implements RolePersistencePort {

	@Autowired
	private final RoleRepository roleRepository;
	
	@Autowired
	private final RoleEntityMapper roleEntityMapper;

	@Override
	public List<RoleDTO> getAll() {
		return roleEntityMapper.entitiesToDTOs(roleRepository.findAll());
	}

	@Override
	public Optional<RoleDTO> getOneByName(String rolename) {
		Optional<Role> getRole = Optional.ofNullable(roleRepository.findByName(rolename));
		
		if(getRole.isPresent()) {
			return Optional.ofNullable(roleEntityMapper.entityToDTO(getRole.get()));
		} else
			return Optional.ofNullable(null);
		
	}

	@Override
	public Optional<RoleDTO> addOne(RoleDTO roleDTO) {
		return Optional.ofNullable(roleEntityMapper.entityToDTO(
				roleRepository.save(roleEntityMapper.dtoToEntity(roleDTO))));
	}

	@Override
	public void removeOne(String rolename) {
		roleRepository.deleteByName(rolename);
	}
	
}
