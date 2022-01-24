package com.searchservice.app.domain.dto.solr.collection;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode
public class SolrCreateCollectionDTO {

    private String collectionName;
    private String sku;

    public SolrCreateCollectionDTO(String collectionName, String sku) {
        this.collectionName = collectionName;
        this.sku = sku;
    }


}
