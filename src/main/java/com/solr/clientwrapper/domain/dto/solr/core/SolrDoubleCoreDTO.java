package com.solr.clientwrapper.domain.dto.solr.core;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode
public class SolrDoubleCoreDTO {

    private String coreOne;
    private String coreTwo;

    public SolrDoubleCoreDTO(String coreOne, String coreTwo) {
        this.coreOne = coreOne;
        this.coreTwo = coreTwo;
    }

}
