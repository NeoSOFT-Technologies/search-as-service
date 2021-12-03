package com.solr.clientwrapper.domain.dto.solr.collection;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode
public class SolrDeleteCollectionDTO {

    String collectionName;

    public SolrDeleteCollectionDTO(String collectionName) {
        this.collectionName = collectionName;
    }

}
