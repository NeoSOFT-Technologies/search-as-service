package com.solr.clientwrapper.domain.dto.solr;

import com.solr.clientwrapper.infrastructure.Enum.SolrFieldType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode 
@AllArgsConstructor
public class SolrFieldDTO {

	private String name;
	private SolrFieldType type;
	private String default_;
	private boolean isRequired;
	private boolean isFilterable;
	private boolean isStorable;
	private boolean isMultiValue;
	private boolean isSortable;
}
