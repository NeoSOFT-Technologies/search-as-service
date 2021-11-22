package com.solr.clientwrapper.domain.dto.solr;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode
public class SolrCreateCoreDTO {

    String coreName;

    public SolrCreateCoreDTO(String coreName) {
        this.coreName = coreName;
    }

}
