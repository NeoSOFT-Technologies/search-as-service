package com.searchservice.app.domain.dto;

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

	public ResponseDTO(int statusCode, String name, String message) {
		super();
		this.statusCode = statusCode;
		this.name = name;
		this.message = message;
	}
}
