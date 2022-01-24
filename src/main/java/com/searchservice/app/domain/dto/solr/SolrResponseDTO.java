package com.searchservice.app.domain.dto.solr;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode
public class SolrResponseDTO {

    private int statusCode;
    private String name;
    private String message;

    public SolrResponseDTO(String name) {
        this.name = name;
    }
}
