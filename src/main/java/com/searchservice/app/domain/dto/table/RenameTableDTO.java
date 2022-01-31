package com.searchservice.app.domain.dto.table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode
public class RenameTableDTO {

    private String tableName;
    private String tableNewName;

    public RenameTableDTO(String tableName, String tableNewName) {
        this.tableName = tableName;
        this.tableNewName = tableNewName;
    }

}
