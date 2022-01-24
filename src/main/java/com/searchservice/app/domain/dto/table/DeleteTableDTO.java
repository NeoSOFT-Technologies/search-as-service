package com.searchservice.app.domain.dto.table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode
public class DeleteTableDTO {

    private String collectionName;

    public DeleteTableDTO(String collectionName) {
        this.collectionName = collectionName;
    }

}
