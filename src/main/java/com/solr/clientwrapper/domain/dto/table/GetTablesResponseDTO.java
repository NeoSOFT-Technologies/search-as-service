package com.solr.clientwrapper.domain.dto.table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@EqualsAndHashCode
public class GetTablesResponseDTO {

    private int statusCode;
    private String message;
    private List<String> tables;

}
