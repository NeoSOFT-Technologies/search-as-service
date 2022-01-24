package com.searchservice.app.domain.dto;

import com.searchservice.app.infrastructure.solrenum.SolrFieldType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class SolrFieldDTO {

	String name;
	SolrFieldType type;
	String default_;
	boolean isRequired;
	boolean isFilterable;
	boolean isStorable;
	boolean isMultiValue;
	boolean isSortable;
	
}
