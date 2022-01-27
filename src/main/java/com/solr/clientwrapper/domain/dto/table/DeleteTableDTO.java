package com.solr.clientwrapper.domain.dto.table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode
public class DeleteTableDTO {

    private String tableName;

    public DeleteTableDTO(String tableName) {
        this.tableName = tableName;
    }

}
