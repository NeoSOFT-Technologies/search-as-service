package com.solr.clientwrapper.domain.dto.solr.core;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode
public class SolrSingleCoreDTO {

    private String coreName;

    public SolrSingleCoreDTO(String coreName) {
        this.coreName = coreName;
    }

}
