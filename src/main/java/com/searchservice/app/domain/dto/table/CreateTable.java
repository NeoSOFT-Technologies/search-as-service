package com.searchservice.app.domain.dto.table;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor

@EqualsAndHashCode(callSuper = false)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties("")
public class CreateTable extends ManageTable {
	private String sku;

	public CreateTable(String tableName, String sku, List<SchemaField> columns) {
		super(tableName, columns);
		this.sku = sku;
	}

}