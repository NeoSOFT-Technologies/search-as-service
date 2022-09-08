package com.neosoft.app.domain.port.api;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import com.neosoft.app.domain.dto.ProductDTO;
import com.neosoft.app.domain.dto.Response;


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
	Response createProduct(ProductDTO productDTO);

	// UPDATE requests
	Response updateProduct(int productId, ProductDTO productDTO);
	
	// DELETE requests
	Response deleteProduct(int productId);

}
