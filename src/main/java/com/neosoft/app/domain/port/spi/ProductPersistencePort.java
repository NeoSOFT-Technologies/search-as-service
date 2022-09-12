package com.neosoft.app.domain.port.spi;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.neosoft.app.domain.dto.ProductDTO;


@Component
public interface ProductPersistencePort {

	List<ProductDTO> getAll();
	Optional<ProductDTO> getOne(int productId);
	boolean isExistsById(int productId);
	Optional<ProductDTO> addOne(ProductDTO product);
	void removeOne(int productId);
	
}
