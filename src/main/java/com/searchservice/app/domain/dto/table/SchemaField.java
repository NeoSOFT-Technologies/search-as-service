package com.searchservice.app.domain.dto.table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.searchservice.app.infrastructure.adaptor.versioning.VersionedObjectMapper;
import com.searchservice.app.rest.errors.BadRequestOccurredException;

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
	
	@Override
	public VersionedObjectMapper toVersion(int version) {
		return this;
	}

//	public boolean isFilterable() {
//		return isFilterable;
//	}

	public void setFilterable(Object value) {
		if (value instanceof Boolean) {
            this.isFilterable = (Boolean) value;
        }else {
        	throw new BadRequestOccurredException(400, "Value for Filterable is expected as : true/false");
        }
	}

//	public boolean isRequired() {
//		return isRequired;
//	}

	public void setRequired(Object value) {
		if (value instanceof Boolean) {
            this.isRequired = (Boolean) value;
        }else {
        	throw new BadRequestOccurredException(400, "Value for Required is expected as : true/false");
        }
	}

//	public boolean isStorable() {
//		return isStorable;
//	}

	public void setStorable(Object value) {
		//this.isStorable = isStorable;
		if (value instanceof Boolean) {
            this.isStorable = (Boolean) value;
        }else {
        	throw new BadRequestOccurredException(400, "Value for Storable is expected as : true/false");
        }
	}

//	public boolean isMultiValue() {
//		return isMultiValue;
//	}

	public void setMultiValue(Object value) {
		//this.isMultiValue = isMultiValue;
		if (value instanceof Boolean) {
            this.isMultiValue = (Boolean) value;
        }else {
        	throw new BadRequestOccurredException(400, "Value for MultiValue is expected as : true/false");
        }
	}

//	public boolean isSortable() {
//		return isSortable;
//	}

	public void setSortable(Object value) {
		//this.isSortable = isSortable;
		if (value instanceof Boolean) {
            this.isSortable = (Boolean) value;
        }else {
        	throw new BadRequestOccurredException(400, "Value for Sortable is expected as : true/false");
        }
	}
	
}
