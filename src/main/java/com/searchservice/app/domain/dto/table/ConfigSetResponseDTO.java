package com.searchservice.app.domain.dto.table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
public class ConfigSetResponseDTO {

    private int statusCode;
    private String message;
    private List<String> configSets;
	
    public ConfigSetResponseDTO(int statusCode, String message) {
		this.statusCode = statusCode;
		this.message = message;
	}
}
