package com.solr.clientwrapper.domain.dto.solr;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode
public class SolrSingleCoreDTO {

    String coreName;

    public SolrSingleCoreDTO(String coreName) {
        this.coreName = coreName;
    }

}
