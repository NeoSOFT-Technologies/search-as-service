package com.searchservice.app.domain.dto.table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode
public class CreateTableDTO {

    private String collectionName;
    private String sku;

    public CreateTableDTO(String collectionName, String sku) {
        this.collectionName = collectionName;
        this.sku = sku;
    }


}
