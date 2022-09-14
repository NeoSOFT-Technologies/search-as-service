package com.neosoft.app.infrastructure.adaptor.security;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.neosoft.app.domain.dto.security.RoleDTO;
import com.neosoft.app.infrastructure.entity.security.Role;


/**
 * Mapper for the entity {@link Role} and its DTO called {@link RoleDTO}.
 *
 *With the hard-coded mappers, it could get very tedious in the future 
 *when we have lots of entities with many fields each
 *
 *So, here we are making use of ModelMapper to generate the DTO mappings automatically
 *
 */

@Component
public class RoleEntityMapper {
	/**
	 * This class provides mapping strategy for RoleDTO to Role-entity and vice-versa
	 */
	
	// inject ModelMapper
	private ModelMapper modelMapper = new ModelMapper();
	

	////////////////////////// Using ModelMapper library /////////////////////
	// Entity to DTO Mapping
	public RoleDTO entityToDTO(Role role) {
		return modelMapper.map(role, RoleDTO.class);
	}
	
    public List<RoleDTO> entitiesToDTOs(List<Role> roles) {
        return roles.stream().filter(Objects::nonNull).map(this::entityToDTO).collect(Collectors.toList());
    }
	
	// DTO to entity Mapping
	public Role dtoToEntity(RoleDTO roleDTO) {
		return modelMapper.map(roleDTO, Role.class);
	}
	
    public List<Role> dtosToEntities(List<RoleDTO> roleDTOs) {
        return roleDTOs.stream().filter(Objects::nonNull).map(this::dtoToEntity).collect(Collectors.toList());
    }

}
