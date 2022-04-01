package com.searchservice.app.domain.dto.table;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConfigSetResponse {

	private int statusCode;
	private String message;
	private List<String> configSets;

	public ConfigSetResponse(int statusCode, String message) {
		this.statusCode = statusCode;
		this.message = message;
	}

}
