package com.searchservice.app.domain.dto.solr.collection;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode
public class SolrRenameCollectionDTO {

    private String collectionName;
    private String collectionNewName;

    public SolrRenameCollectionDTO(String collectionName, String collectionNewName) {
        this.collectionName = collectionName;
        this.collectionNewName = collectionNewName;
    }

}
