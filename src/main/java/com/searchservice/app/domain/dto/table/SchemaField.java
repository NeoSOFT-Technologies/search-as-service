package com.searchservice.app.domain.dto.table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.searchservice.app.infrastructure.adaptor.versioning.VersionedObjectMapper;
import com.searchservice.app.rest.errors.BadRequestOccurredException;
import com.searchservice.app.rest.errors.HttpStatusCode;
import com.searchservice.app.rest.errors.InvalidColumnOccurredException;
import com.searchservice.app.rest.errors.InvalidInputOccurredException;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SchemaField implements VersionedObjectMapper {

	String name;
	String type;
	@JsonIgnore
	String default_;
	boolean isRequired;
	boolean isFilterable;
	boolean isStorable;
	boolean isMultiValue;
	boolean isSortable;
	boolean isPartialSearch;
	
	@Override
	public VersionedObjectMapper toVersion(int version) {
		return this;
	}
	
	public void setFilterable(Object value) {
		if (value instanceof Boolean) {
            this.isFilterable = (Boolean) value;
        }else {
        	throw new InvalidColumnOccurredException(HttpStatusCode.INVALID_COLUMN_ATTRIBUTE.getCode(), "Value for Filterable is expected as : true/false");
        }
	}

	public void setRequired(Object value) {
		if (value instanceof Boolean) {
            this.isRequired = (Boolean) value;
        }else {
        	throw new InvalidColumnOccurredException(HttpStatusCode.INVALID_COLUMN_ATTRIBUTE.getCode(), "Value for Required is expected as : true/false");
        }
	}


	public void setStorable(Object value) {
		//this.isStorable = isStorable;
		if (value instanceof Boolean) {
            this.isStorable = (Boolean) value;
        }else {
        	throw new InvalidColumnOccurredException(HttpStatusCode.INVALID_COLUMN_ATTRIBUTE.getCode(), "Value for Storable is expected as : true/false");
        }
	}


	public void setMultiValue(Object value) {
		//this.isMultiValue = isMultiValue;
		if (value instanceof Boolean) {
            this.isMultiValue = (Boolean) value;
        }else {
        	throw new InvalidColumnOccurredException(HttpStatusCode.INVALID_COLUMN_ATTRIBUTE.getCode(), "Value for MultiValue is expected as : true/false");
        }
	}


	public void setSortable(Object value) {
		//this.isSortable = isSortable;
		if (value instanceof Boolean) {
            this.isSortable = (Boolean) value;
        }else {
        	throw new InvalidColumnOccurredException(HttpStatusCode.INVALID_COLUMN_ATTRIBUTE.getCode(), "Value for Sortable is expected as : true/false");
        }
	}
}
