package com.searchservice.app.domain.dto.table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConfigSetDTO {
	String baseConfigSetName;
	String configSetName;
}
