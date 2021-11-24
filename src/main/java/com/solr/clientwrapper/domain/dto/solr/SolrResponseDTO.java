package com.solr.clientwrapper.domain.dto.solr;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode
public class SolrResponseDTO {

    int statusCode;
    String coreName;
    String message;


}
