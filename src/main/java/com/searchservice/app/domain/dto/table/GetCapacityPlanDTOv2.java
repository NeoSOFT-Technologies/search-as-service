package com.searchservice.app.domain.dto.table;

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
public class GetCapacityPlanDTOv2 implements VersionedObjectMapper {

	private int statusCode;
	private String message;
    private List<CapacityPlanProperties.Plan> plans;

    public GetCapacityPlanDTOv2(List<CapacityPlanProperties.Plan> plans) {
        this.plans = plans;
    }

	@Override
	public VersionedObjectMapper toVersion(int version) {
		return this;
	}

}
