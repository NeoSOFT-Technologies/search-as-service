package com.neosoft.app.infrastructure.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
@MappedSuperclass
public class EntityId {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	public EntityId(EntityId manageProduct) {
		this.id = manageProduct.getId();
	}
}
