package com.neosoft.app.domain.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.neosoft.app.domain.dto.Response;
import com.neosoft.app.domain.dto.Response.ProductResponse;
import com.neosoft.app.domain.port.api.ProductServicePort;
import com.neosoft.app.domain.port.spi.ProductPersistencePort;
import com.neosoft.app.domain.utils.PersistenceLabel;
import com.neosoft.app.infrastructure.entity.Product;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Data
@Slf4j
public class ProductService implements ProductServicePort {

	@Autowired
	private ProductPersistencePort productPersistencePort;

	ProductService(ProductPersistencePort productPersistencePort) {
		this.productPersistencePort = productPersistencePort;
	}

	@Override
	public Response getAllProducts() {
		Response responseDTO = new Response();

		try {
			List<Product> products = productPersistencePort.getAll();
			if(products != null) {
				responseDTO.setStatusCode(200);
				responseDTO.setMessage("All products are retrieved successfully.");
				List<ProductResponse> productsResponse = new ArrayList<>();
				products.stream().forEach(p -> productsResponse.add(new ProductResponse(p.getId(), p.getProductName())));
				responseDTO.setProductList(productsResponse);
			} else {
				responseDTO.setStatusCode(500);
			}
		} catch(Exception e) {
			responseDTO.setStatusCode(400);
			log.error("Exception occured while fetching all products: ", e.getMessage());
		}

		return responseDTO;
	}

	@Override
	public Response getProduct(int productId) {
		Response responseDTO = new Response();

		try {
			Optional<Product> getProduct = productPersistencePort.getOne(productId);
			if (getProduct.isPresent()) {
				List<ProductResponse> productsResponse = new ArrayList<>();
				productsResponse.add(new ProductResponse(getProduct.get().getId(), getProduct.get().getProductName()));
				responseDTO.setProductList(productsResponse);
				responseDTO.setStatusCode(200);
			} else {
				responseDTO.setStatusCode(405);
			}	
		} catch(Exception e) {
			log.error("Exception occurred while fetching the product: ", e.getMessage());
			responseDTO.setStatusCode(400);
		}

		return responseDTO;
	}

	@Override
	public Response createProduct(Product productDTO) {
		Response responseDTO = new Response();

		responseDTO.setDataSize(null);
		try {
			Optional<Product> addProduct = productPersistencePort.addOne(productDTO);
			if (addProduct.isPresent()) {
				List<ProductResponse> productsResponse = new ArrayList<>();
				productsResponse.add(new ProductResponse(addProduct.get().getId(), addProduct.get().getProductName()));
				responseDTO.setProductList(productsResponse);
				responseDTO.setStatusCode(200);
			} else {
				responseDTO.setStatusCode(400);
			}
		} catch(Exception e) {
			log.error("Exception occurred while adding new product: ", e.getMessage());
			responseDTO.setStatusCode(400);
		}

		return responseDTO;
	}

	@Override
	public Response updateProduct(int productId, Product productDTO) {
		Response responseDTO = new Response();

		try {
			Optional<Product> updateProduct = productPersistencePort.getOne(productId);
			if (updateProduct.isPresent()) {
				productDTO.setId(productId);
				productPersistencePort.addOne(productDTO);
				List<ProductResponse> productsResponse = new ArrayList<>();
				productsResponse.add(new ProductResponse(productDTO.getId(), productDTO.getProductName()));
				responseDTO.setProductList(productsResponse);
				responseDTO.setStatusCode(200);
				responseDTO.setMessage(String.format("The entity- '%s', with id- '%s' is updated successfully in the database!",
						PersistenceLabel.ENTITY_PRODUCT.getLabel(), productId));
			} else {
				responseDTO.setStatusCode(405);
				responseDTO.setMessage(String.format("Entity- '%s', with id- '%s' could not be found in the database!",
						PersistenceLabel.ENTITY_PRODUCT.getLabel(), productId));
			}
		} catch (Exception e) {
			log.error("Exception occurred while updating the given product: ", e.getMessage());
			responseDTO.setStatusCode(400);
		}

		return responseDTO;
	}

	@Override
	public Response deleteProduct(int productId) {
		Response responseDTO = new Response();

		responseDTO.setDataSize(null);
		try {
			boolean isExists = productPersistencePort.isExistsById(productId);
			if(!isExists) {
				responseDTO.setStatusCode(405);
				responseDTO.setMessage(String.format("Entity- '%s', with id- '%s' could not be found in the database!",
						PersistenceLabel.ENTITY_PRODUCT.getLabel(), 
						productId));
			} else {
				productPersistencePort.removeOne(productId);
				responseDTO.setStatusCode(200);
				responseDTO.setMessage(String.format("The entity- '%s', with id- '%s' is deleted successfully from database!",
						PersistenceLabel.ENTITY_PRODUCT.getLabel(), productId));
			}
			
		} catch (Exception e) {
			log.error("Product with Id - {} couldn't be saved! EXception: ", productId, e.getMessage());
			responseDTO.setStatusCode(400);
		}

		return responseDTO;
	}

}
