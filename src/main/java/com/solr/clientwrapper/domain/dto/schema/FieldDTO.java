package com.solr.clientwrapper.domain.dto.schema;

import com.solr.clientwrapper.infrastructure.solrenum.SchemaFieldType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class FieldDTO {

	String name;
	SchemaFieldType type;
	String default_;
	boolean isRequired;
	boolean isFilterable;
	boolean isStorable;
	boolean isMultiValue;
	boolean isSortable;
	
}
