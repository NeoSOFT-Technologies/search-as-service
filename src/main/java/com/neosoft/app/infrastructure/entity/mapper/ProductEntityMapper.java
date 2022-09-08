package com.neosoft.app.infrastructure.entity.mapper;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.neosoft.app.domain.dto.ProductDTO;
import com.neosoft.app.infrastructure.entity.Product;


/**
 * Mapper for the entity {@link SampleEntity} and its DTO called {@link SampleEntityDTO}.
 *
 *With the hard-coded mappers, it could get very tedious in the future 
 *when we have lots of entities with many fields each
 *
 *So, here we are making use of ModelMapper to generate the DTO mappings automatically
 *
 */

@Component
public class ProductEntityMapper {
	/**
	 * This class provides mapping strategy for ProductDTO to Product-entity and vice-versa
	 */
	
	// inject ModelMapper
	private ModelMapper modelMapper = new ModelMapper();
	

	////////////////////////// Using ModelMapper library /////////////////////
	// Entity to DTO Mapping
	public ProductDTO entityToDTO(Product product) {
		return modelMapper.map(product, ProductDTO.class);
	}
	
    public List<ProductDTO> entitiesToDTOs(List<Product> sampleEntities) {
        return sampleEntities.stream().filter(Objects::nonNull).map(this::entityToDTO).collect(Collectors.toList());
    }
	
	// DTO to entity Mapping
	public Product dtoToEntity(ProductDTO sampleEntityDTO) {
		return modelMapper.map(sampleEntityDTO, Product.class);
	}
	
    public List<Product> dtosToEntities(List<ProductDTO> sampleEntityDTOs) {
        return sampleEntityDTOs.stream().filter(Objects::nonNull).map(this::dtoToEntity).collect(Collectors.toList());
    }
	
	
	//////////////////////////2. Hard-coded way /////////////////////
    
	/*
	 * public List<SampleEntityDTO>
	 * sampleEntitiesToSampleEntityDTOs(List<SampleEntity> sampleEntities) { return
	 * sampleEntities.stream().filter(Objects::nonNull).map(this::
	 * sampleEntityToSampleEntityDTO).collect(Collectors.toList()); }
	 * 
	 * public SampleEntityDTO sampleEntityToSampleEntityDTO(SampleEntity
	 * sampleEntity) { return new SampleEntityDTO(sampleEntity); }
	 * 
	 * public List<User> sampleEntityDTOsToSampleEntities(List<AdminUserDTO>
	 * userDTOs) { return userDTOs.stream().filter(Objects::nonNull).map(this::
	 * sampleEntityDTOToSampleEntity).collect(Collectors.toList()); }
	 * 
	 * public User sampleEntityDTOToSampleEntity(AdminUserDTO userDTO) { if (userDTO
	 * == null) { return null; } else { User user = new User();
	 * user.setId(userDTO.getId()); user.setLogin(userDTO.getLogin());
	 * user.setFirstName(userDTO.getFirstName());
	 * user.setLastName(userDTO.getLastName()); user.setEmail(userDTO.getEmail());
	 * user.setImageUrl(userDTO.getImageUrl());
	 * user.setActivated(userDTO.isActivated());
	 * user.setLangKey(userDTO.getLangKey()); Set<Authority> authorities =
	 * this.authoritiesFromStrings(userDTO.getAuthorities());
	 * user.setAuthorities(authorities); return user; } }
	 */
    

	/*
	 * private Set<Authority> authoritiesFromStrings(Set<String>
	 * authoritiesAsString) { Set<Authority> authorities = new HashSet<>();
	 * 
	 * if (authoritiesAsString != null) { authorities = authoritiesAsString
	 * .stream() .map( string -> { Authority auth = new Authority();
	 * auth.setName(string); return auth; } ) .collect(Collectors.toSet()); }
	 * 
	 * return authorities; }
	 * 
	 * public Product sampleEntityFromId(Integer id) { if (id == null) { return
	 * null; } Product sampleEntity = new Product(); sampleEntity.setId(id); return
	 * sampleEntity; }
	 * 
	 * @Named("id")
	 * 
	 * @BeanMapping(ignoreByDefault = true)
	 * 
	 * @Mapping(target = "id", source = "id") public ProductDTO toDtoId(Product
	 * user) { if (user == null) { return null; } ProductDTO userDto = new
	 * ProductDTO(); userDto.setId(user.getId()); return userDto; }
	 * 
	 * @Named("idSet")
	 * 
	 * @BeanMapping(ignoreByDefault = true)
	 * 
	 * @Mapping(target = "id", source = "id") public Set<ProductDTO>
	 * toDtoIdSet(Set<Product> sampleEntities) { if (sampleEntities == null) {
	 * return Collections.emptySet(); }
	 * 
	 * Set<ProductDTO> sampleEntitySet = new HashSet<>(); for (Product
	 * sampleEntityEntity : sampleEntities) {
	 * sampleEntitySet.add(this.toDtoId(sampleEntityEntity)); }
	 * 
	 * return sampleEntitySet; }
	 */

}
