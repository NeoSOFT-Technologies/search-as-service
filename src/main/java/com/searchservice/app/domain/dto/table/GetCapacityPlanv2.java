package com.searchservice.app.domain.dto.table;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.searchservice.app.config.CapacityPlanProperties;
import com.searchservice.app.infrastructure.adaptor.versioning.VersionedObjectMapper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetCapacityPlanv2 implements VersionedObjectMapper {

	private int statusCode;
	private String message;
    private List<CapacityPlanProperties.Plan> plans;

    public GetCapacityPlanv2(List<CapacityPlanProperties.Plan> plans) {
        this.plans = plans;
    }

	@Override
	public VersionedObjectMapper toVersion(int version) {
		return this;
	}

}
