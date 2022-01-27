package com.solr.clientwrapper.domain.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode
public class ResponseDTO {

    private int statusCode;
    private String name;
    private String message;

    public ResponseDTO(String name) {
        this.name = name;
    }
}
