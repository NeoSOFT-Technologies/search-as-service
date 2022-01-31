package com.searchservice.app.domain.dto;



import com.searchservice.app.infrastructure.enums.SchemaFieldType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class SchemaFieldDTO {

	String name;
	SchemaFieldType type;
	String default_;
	boolean isRequired;
	boolean isIndexed;
	boolean isStorable;
	boolean isMultiValue;
	boolean isDocValues;
	
}
