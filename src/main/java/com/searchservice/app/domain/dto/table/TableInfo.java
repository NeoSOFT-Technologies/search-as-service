package com.searchservice.app.domain.dto.table;

import java.util.Map;

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
	private Map<String, String> tenantInfo;

	public TableInfo(TableInfo tableInfoDto) {
		this.replicationFactor = tableInfoDto.getReplicationFactor();
		this.replicationFactor = tableInfoDto.getNoOfShards();
		this.tenantInfo = tableInfoDto.getTenantInfo();
	}
}
