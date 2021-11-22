package com.solr.clientwrapper.domain.dto.solr;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode
public class SolrRenameCoreDTO {

    String coreName;
    String newName;

    public SolrRenameCoreDTO(String coreName, String newName) {
        this.coreName = coreName;
        this.newName = newName;
    }

}
