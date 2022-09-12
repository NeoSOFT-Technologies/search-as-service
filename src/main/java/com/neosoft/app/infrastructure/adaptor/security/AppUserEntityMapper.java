package com.neosoft.app.infrastructure.adaptor.security;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.neosoft.app.domain.dto.security.AppUserDTO;
import com.neosoft.app.infrastructure.entity.security.AppUser;


/**
 * Mapper for the entity {@link AppUser} and its DTO called {@link AppUserDTO}.
 *
 *With the hard-coded mappers, it could get very tedious in the future 
 *when we have lots of entities with many fields each
 *
 *So, here we are making use of ModelMapper to generate the DTO mappings automatically
 *
 */

@Component
public class AppUserEntityMapper {
	/**
	 * This class provides mapping strategy for AppUserDTO to AppUser-entity and vice-versa
	 */
	
	// inject ModelMapper
	private ModelMapper modelMapper = new ModelMapper();
	

	////////////////////////// Using ModelMapper library /////////////////////
	// Entity to DTO Mapping
	public AppUserDTO entityToDTO(AppUser appUser) {
		return modelMapper.map(appUser, AppUserDTO.class);
	}
	
    public List<AppUserDTO> entitiesToDTOs(List<AppUser> appUsers) {
        return appUsers.stream().filter(Objects::nonNull).map(this::entityToDTO).collect(Collectors.toList());
    }
	
	// DTO to entity Mapping
	public AppUser dtoToEntity(AppUserDTO appUserDTO) {
		return modelMapper.map(appUserDTO, AppUser.class);
	}
	
    public List<AppUser> dtosToEntities(List<AppUserDTO> appUserDTOs) {
        return appUserDTOs.stream().filter(Objects::nonNull).map(this::dtoToEntity).collect(Collectors.toList());
    }

}
