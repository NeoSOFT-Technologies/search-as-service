package com.neosoft.app.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties("id")
public class ProductDTO {

	private int id;
	private String productName;
	
	public ProductDTO(ProductDTO productDTO) {
		this.productName = productDTO.getProductName();
	}
	
}
