package com.searchservice.app.domain.dto.table;

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
public class TableInfo {
	
	private int replicationFactor;
	private int noOfShards;

	public TableInfo(TableInfo schemaDTO) {
		this.replicationFactor = schemaDTO.getReplicationFactor();
		this.replicationFactor = schemaDTO.getNoOfShards();
	}
}
