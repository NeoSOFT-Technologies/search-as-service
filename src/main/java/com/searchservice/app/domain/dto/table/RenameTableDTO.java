package com.searchservice.app.domain.dto.table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode
public class RenameTableDTO {

    private String collectionName;
    private String collectionNewName;

    public RenameTableDTO(String collectionName, String collectionNewName) {
        this.collectionName = collectionName;
        this.collectionNewName = collectionNewName;
    }

}
