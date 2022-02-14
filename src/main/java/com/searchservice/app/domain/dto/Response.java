package com.searchservice.app.domain.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.searchservice.app.infrastructure.adaptor.versioning.VersionedObjectMapper;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response implements VersionedObjectMapper {

    private int responseStatusCode;
    private String name;
    private String responseMessage;
    private List<String> items;

    public Response(String name) {
        this.name = name;
    }
    
    public Response(int responseStatusCode, String responseMessage) {
    	this.responseStatusCode = responseStatusCode;
    	this.responseMessage = responseMessage;
    }

	public Response(int statusCode, String name, String message) {
		this.responseStatusCode = statusCode;
		this.name = name;
		this.responseMessage = message;
	}
	
	@Override
	public VersionedObjectMapper toVersion(int version) {
		return this;
	}
	
}
