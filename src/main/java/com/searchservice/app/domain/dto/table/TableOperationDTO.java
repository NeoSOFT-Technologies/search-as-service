package com.searchservice.app.domain.dto.table;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

//CreateTableDTO
//DeleteTableDTO
//RenameTableDTO
@Data
@NoArgsConstructor
@EqualsAndHashCode

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TableOperationDTO {

	private String tableName;
    private String sku;
    private String tableNewName;
    
    public TableOperationDTO(String tableName, String sku) {
    	this.tableName = tableName;
    	this.sku = sku;
    }
    
    public TableOperationDTO(String tableName) {
    	this.tableName = tableName;
    }
}
