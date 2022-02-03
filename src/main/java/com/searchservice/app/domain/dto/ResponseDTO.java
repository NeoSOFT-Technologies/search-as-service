package com.searchservice.app.domain.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
//ApiResponseDTO
//GetListItemsResponseDTO
@Data
@NoArgsConstructor
@EqualsAndHashCode

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseDTO {

    private int responseStatusCode;
    private String name;
    private String responseMessage;
    private List<String> items;

    public ResponseDTO(String name) {
        this.name = name;
    }
    
    public ResponseDTO(int responseStatusCode, String responseMessage) {
    	this.responseStatusCode = responseStatusCode;
    	this.responseMessage = responseMessage;
    }
}
