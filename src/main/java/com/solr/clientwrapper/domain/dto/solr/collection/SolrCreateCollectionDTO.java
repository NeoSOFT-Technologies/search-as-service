package com.solr.clientwrapper.domain.dto.solr.collection;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode
public class SolrCreateCollectionDTO {

    String collectionName;
    String sku;

    public SolrCreateCollectionDTO(String collectionName, String sku) {
        this.collectionName = collectionName;
        this.sku = sku;
    }


}
