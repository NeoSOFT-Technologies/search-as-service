package com.solr.clientwrapper.domain.dto.solr;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode
public class SolrReloadCoreDTO {

    String coreName;

    public SolrReloadCoreDTO(String coreName) {
        this.coreName = coreName;
    }

}
