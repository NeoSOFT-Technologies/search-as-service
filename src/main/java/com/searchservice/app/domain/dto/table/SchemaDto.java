package com.searchservice.app.domain.dto.table;

public enum SchemaDto {
	NAME("name"),
	MULTIVALUED("multiValued"),
	STORED("stored"),
	REQUIRED("required"),
	VALIDATED("validated"),
	DOCVALUES("docValues"),
	INDEXED("indexed"),
	PARTIAL_SEARCH("partial_search");

	private String label;
	
	SchemaDto(String label) {
        this.label = label;
	}

	public String getLabel() {
		return label;
	}

}
