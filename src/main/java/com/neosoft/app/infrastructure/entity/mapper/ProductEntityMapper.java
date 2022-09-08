package com.neosoft.app.infrastructure.entity.mapper;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.neosoft.app.domain.dto.ProductDTO;
import com.neosoft.app.infrastructure.entity.Product;


/**
 * Mapper for the entity {@link Product} and its DTO called {@link ProductDTO}.
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

}
