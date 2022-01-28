package com.searchservice.app.domain.dto.table;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ManageSchemaResponseDTO {
	private int responseStatuscode;
	private String responseMessage;
	private String schemaName;
	private Map<String, List<String>> schemaAttributes;
}
