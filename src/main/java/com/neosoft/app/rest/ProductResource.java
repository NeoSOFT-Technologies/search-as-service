package com.neosoft.app.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.neosoft.app.domain.dto.Response;
import com.neosoft.app.domain.dto.Response.ProductResponse;
import com.neosoft.app.domain.port.api.ProductServicePort;
import com.neosoft.app.infrastructure.entity.Product;
import com.neosoft.app.rest.errors.CustomException;
import com.neosoft.app.rest.errors.HttpStatusCode;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("${base-url.api-endpoint.product}")
@Slf4j
public class ProductResource {
	
	private static final String PRODUCT = "Product ";
	private static final String ERROR_MSG = "Something Went Wrong While";

	@Autowired
	private ProductServicePort productServicePort;

	public ProductResource(ProductServicePort productServicePort) {
		this.productServicePort = productServicePort;
	}

	@GetMapping
	@Operation(summary = "GET ALL THE PRODUCTS.", security = @SecurityRequirement(name = "bearerAuth"))
	public ResponseEntity<Response> getAllProducts() {

		Response getResponseDTO = productServicePort.getAllProducts();

		if (getResponseDTO == null || getResponseDTO.getStatusCode() == 500)
			throw new CustomException(HttpStatusCode.NULL_POINTER_EXCEPTION.getCode(),
					HttpStatusCode.NULL_POINTER_EXCEPTION, HttpStatusCode.NULL_POINTER_EXCEPTION.getMessage());
		if (getResponseDTO.getStatusCode() == 200) {
			List<ProductResponse> existingProductsList = getResponseDTO.getProductList();
			getResponseDTO.setProductList(existingProductsList);
			getResponseDTO.setDataSize(existingProductsList.size());
			return ResponseEntity.status(HttpStatus.OK).body(getResponseDTO);
		} else {
			throw new CustomException(HttpStatusCode.BAD_REQUEST_EXCEPTION.getCode(),
					HttpStatusCode.BAD_REQUEST_EXCEPTION, HttpStatusCode.BAD_REQUEST_EXCEPTION.getMessage());
		}
	}

	@GetMapping("/{productId}")
	@Operation(summary = "GET PRODUCT.", security = @SecurityRequirement(name = "bearerAuth"))
	public ResponseEntity<Response> getProduct(@PathVariable int productId) {

		// GET product
		Response productResponseDTO = productServicePort.getProduct(productId);
		if (productResponseDTO == null)
			throw new CustomException(HttpStatusCode.BAD_REQUEST_EXCEPTION.getCode(),
					HttpStatusCode.BAD_REQUEST_EXCEPTION, HttpStatusCode.BAD_REQUEST_EXCEPTION.getMessage());
		else {
			if (productResponseDTO.getStatusCode() == 200) {
				List<ProductResponse> existingProductsList = productResponseDTO.getProductList();
				productResponseDTO.setProductList(existingProductsList);
				productResponseDTO.setDataSize(existingProductsList.size());
				return ResponseEntity.status(HttpStatus.OK).body(productResponseDTO);
			} else if(productResponseDTO.getStatusCode() == 404)
				throw new CustomException(HttpStatusCode.PRODUCT_NOT_FOUND.getCode(),
						HttpStatusCode.PRODUCT_NOT_FOUND, HttpStatusCode.PRODUCT_NOT_FOUND.getMessage());
			else {
				throw new CustomException(HttpStatusCode.BAD_REQUEST_EXCEPTION.getCode(),
						HttpStatusCode.BAD_REQUEST_EXCEPTION, HttpStatusCode.BAD_REQUEST_EXCEPTION.getMessage());
			}
		}
	}

	@PostMapping
	@Operation(summary = "CREATE AND SAVE A PRODUCT IN THE DATABASE.", security = @SecurityRequirement(name = "bearerAuth"))
	public ResponseEntity<Response> createProduct(@RequestBody Product productDTO) {
		Response createResponseDTO = productServicePort.createProduct(productDTO);
		if (createResponseDTO.getStatusCode() == 200) {
			createResponseDTO.setMessage("Product-" + productDTO.getProductName() + ", is saved successfully");
			return ResponseEntity.status(HttpStatus.OK).body(createResponseDTO);
		} else {
			log.info(PRODUCT + "could not be saved: {}", createResponseDTO);
			throw new CustomException(HttpStatusCode.BAD_REQUEST_EXCEPTION.getCode(),
					HttpStatusCode.BAD_REQUEST_EXCEPTION,
					String.format(ERROR_MSG + " Creating Product: %s", productDTO.getProductName()));
		}
	}

	@PutMapping("/{productId}")
	@Operation(summary = "UPDATE AN EXISTING PRODUCT.", security = @SecurityRequirement(name = "bearerAuth"))
	public ResponseEntity<Response> updateProduct(@PathVariable int productId,
			@RequestBody Product productDTO) {
		Response updateResponseDTO = productServicePort.updateProduct(productId, productDTO);
		if (updateResponseDTO.getStatusCode() == 200) {
			updateResponseDTO.setMessage("Product is updated successfully");
			return ResponseEntity.status(HttpStatus.OK).body(updateResponseDTO);
		}  else if(updateResponseDTO.getStatusCode() == 404)
			throw new CustomException(HttpStatusCode.PRODUCT_NOT_FOUND.getCode(),
					HttpStatusCode.PRODUCT_NOT_FOUND, HttpStatusCode.PRODUCT_NOT_FOUND.getMessage());
		else {
			throw new CustomException(HttpStatusCode.BAD_REQUEST_EXCEPTION.getCode(),
					HttpStatusCode.BAD_REQUEST_EXCEPTION, HttpStatusCode.BAD_REQUEST_EXCEPTION.getMessage());
		}
	}

	@DeleteMapping("/{productId}")
	@Operation(summary = "DELETE A PRODUCT FROM DATABASE.", security = @SecurityRequirement(name = "bearerAuth"))
	public ResponseEntity<Response> deleteProduct(@PathVariable int productId) {
		Response deleteResponseDTO = productServicePort.deleteProduct(productId);
		if (deleteResponseDTO.getStatusCode() == 200) {
			return ResponseEntity.status(HttpStatus.OK).body(deleteResponseDTO);
		} else if(deleteResponseDTO.getStatusCode() == 404)
			throw new CustomException(
					HttpStatusCode.PRODUCT_NOT_FOUND.getCode(),
					HttpStatusCode.PRODUCT_NOT_FOUND, 
					deleteResponseDTO.getMessage());
		else {
			log.debug("Exception occurred: {}", deleteResponseDTO);
			throw new CustomException(HttpStatusCode.BAD_REQUEST_EXCEPTION.getCode(),
					HttpStatusCode.BAD_REQUEST_EXCEPTION, HttpStatusCode.BAD_REQUEST_EXCEPTION.getMessage());
		}
	}

}
