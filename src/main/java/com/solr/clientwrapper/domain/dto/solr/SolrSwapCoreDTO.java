package com.solr.clientwrapper.domain.dto.solr;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode
public class SolrSwapCoreDTO {

    String coreOne;
    String coreTwo;

    public SolrSwapCoreDTO(String coreOne, String coreTwo) {
        this.coreOne = coreOne;
        this.coreTwo = coreTwo;
    }

}
