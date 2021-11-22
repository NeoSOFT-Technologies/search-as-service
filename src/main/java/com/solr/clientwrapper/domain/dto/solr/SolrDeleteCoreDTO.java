package com.solr.clientwrapper.domain.dto.solr;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode
public class SolrDeleteCoreDTO {

    String coreName;
    boolean deleteIndex;
    boolean deleteDataDir;
    boolean deleteInstanceDir;

    public SolrDeleteCoreDTO(String coreName, boolean deleteIndex, boolean deleteDataDir, boolean deleteInstanceDir) {
        this.coreName = coreName;
        this.deleteIndex = deleteIndex;
        this.deleteDataDir = deleteDataDir;
        this.deleteInstanceDir = deleteInstanceDir;
    }

}
