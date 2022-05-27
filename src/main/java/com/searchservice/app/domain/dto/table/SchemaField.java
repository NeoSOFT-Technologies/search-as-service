package com.searchservice.app.domain.dto.table;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.searchservice.app.rest.errors.CustomException;
import com.searchservice.app.rest.errors.HttpStatusCode;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SchemaField {

	String name;
	String type;
	boolean isRequired;
	boolean isFilterable;
	boolean isStorable;
	boolean isMultiValue;
	boolean isSortable;
	boolean isPartialSearch;

	public void setFilterable(Object value) {
		if (value instanceof Boolean) {
			this.isFilterable = (Boolean) value;
		} else {
			throw new CustomException(HttpStatusCode.INVALID_COLUMN_ATTRIBUTE.getCode(),
					HttpStatusCode.INVALID_COLUMN_ATTRIBUTE,"Value for filterable is expected as : true/false");
		}
	}

	public void setRequired(Object value) {
		if (value instanceof Boolean) {
			this.isRequired = (Boolean) value;
		} else {
			throw new CustomException(HttpStatusCode.INVALID_COLUMN_ATTRIBUTE.getCode(),
					HttpStatusCode.INVALID_COLUMN_ATTRIBUTE,"Value for required is expected as : true/false");
		}
	}

	public void setStorable(Object value) {
		if (value instanceof Boolean) {
			this.isStorable = (Boolean) value;
		} else {
			throw new CustomException(HttpStatusCode.INVALID_COLUMN_ATTRIBUTE.getCode(),
					HttpStatusCode.INVALID_COLUMN_ATTRIBUTE,"Value for storable is expected as : true/false");
		}
	}

	public void setMultiValue(Object value) {
		if (value instanceof Boolean) {
			this.isMultiValue = (Boolean) value;
		} else {
			throw new CustomException(HttpStatusCode.INVALID_COLUMN_ATTRIBUTE.getCode(),
					HttpStatusCode.INVALID_COLUMN_ATTRIBUTE,"Value for multiValue is expected as : true/false");
		}
	}

	public void setSortable(Object value) {
		if (value instanceof Boolean) {
			this.isSortable = (Boolean) value;
		} else {
			throw new CustomException(HttpStatusCode.INVALID_COLUMN_ATTRIBUTE.getCode(),
					HttpStatusCode.INVALID_COLUMN_ATTRIBUTE,"Value for sortable is expected as : true/false");
		}
	}
	
	public void setPartialSearch(Object value) {
		if (value instanceof Boolean) {
			this.isSortable = (Boolean) value;
		} else {
			throw new CustomException(HttpStatusCode.INVALID_COLUMN_ATTRIBUTE.getCode(),
					HttpStatusCode.INVALID_COLUMN_ATTRIBUTE,"Value for partialSearch is expected as : true/false");
		}
	}
}
