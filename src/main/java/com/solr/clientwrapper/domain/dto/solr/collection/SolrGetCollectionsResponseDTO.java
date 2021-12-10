package com.solr.clientwrapper.domain.dto.solr.collection;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@EqualsAndHashCode
public class SolrGetCollectionsResponseDTO {

    private int statusCode;
    private String message;
    private List<String> collections;

}
