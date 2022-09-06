package com.neosoft.app.domain.port.api;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import com.neosoft.app.domain.dto.Response;
import com.neosoft.app.infrastructure.entity.Product;


@Component
@Repository
public interface ProductServicePort {

	/*
	 * CRUD operations for managing products
	 */
	// GET request
	Response getAllProducts();
	Response getProduct(int productId);
	
	// CREATE requests
	Response createProduct(Product productDTO);

	// UPDATE requests
	Response updateProduct(int productId, Product productDTO);
	
	// DELETE requests
	Response deleteProduct(int productId);

}
