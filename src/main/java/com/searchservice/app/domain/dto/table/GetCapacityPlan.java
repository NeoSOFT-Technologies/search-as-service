package com.searchservice.app.domain.dto.table;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.searchservice.app.config.CapacityPlanProperties;
import com.searchservice.app.infrastructure.adaptor.versioning.VersionedObjectMapper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetCapacityPlan implements VersionedObjectMapper {

    private List<CapacityPlanProperties.Plan> plans;

	@Override
	public VersionedObjectMapper toVersion(int version) {
		if(version >= 2)
			return new GetCapacityPlanv2(
					200, 
					"Operation completed successfully" ,
					plans).toVersion(version);
		return this;
	}

}
