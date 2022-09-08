package com.neosoft.app.domain.port.spi;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.neosoft.app.domain.dto.ProductDTO;
import com.neosoft.app.infrastructure.entity.Product;


@Component
public interface ProductPersistencePort {

	List<Product> getAll();
	Optional<Product> getOne(int productId);
	boolean isExistsById(int productId);
	Optional<Product> addOne(ProductDTO product);
	void removeOne(int productId);
	
}
