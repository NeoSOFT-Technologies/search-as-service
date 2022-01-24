package com.searchservice.app.domain.dto.solr.collection;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode
public class SolrDeleteCollectionDTO {

    private String collectionName;

    public SolrDeleteCollectionDTO(String collectionName) {
        this.collectionName = collectionName;
    }

}
