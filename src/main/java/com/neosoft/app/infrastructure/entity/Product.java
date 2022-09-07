package com.neosoft.app.infrastructure.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties("id")
@Entity
@Table(name = "product")
public class Product extends EntityId {

	@Column(name = "product_name")
	private String productName;

//	@OneToMany(mappedBy = "product")
//	private List<Property> fields;

	public Product() {
		super();
	}
	
	public Product(Product manageProduct) {
		super();
		this.productName = manageProduct.getProductName();
	}
	
}
