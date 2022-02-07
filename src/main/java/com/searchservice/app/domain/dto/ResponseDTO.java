package com.searchservice.app.domain.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.searchservice.app.infrastructure.adaptor.versioning.VersionedObjectMapper;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
//ApiResponseDTO
//GetListItemsResponseDTO
@Data
@NoArgsConstructor
@EqualsAndHashCode

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseDTO implements VersionedObjectMapper {

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
    
	@Override
	public VersionedObjectMapper toVersion(int version) {
		return this;
	}

	public ResponseDTO(int statusCode, String name, String message) {
		super();
		this.responseStatusCode = statusCode;
		this.name = name;
		this.responseMessage = message;
	}
}
