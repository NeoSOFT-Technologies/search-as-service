package com.solr.clientwrapper.domain.dto.table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode
public class CreateTableDTO {

    private String tableName;
    private String sku;

    public CreateTableDTO(String tableName, String sku) {
        this.tableName = tableName;
        this.sku = sku;
    }


}
