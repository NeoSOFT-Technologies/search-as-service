package com.searchservice.app.domain.dto.table;

import com.searchservice.app.config.CapacityPlanProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@EqualsAndHashCode
public class GetCapacityPlanDTO {

    private List<CapacityPlanProperties.Plan> plans;

    public GetCapacityPlanDTO(List<CapacityPlanProperties.Plan> plans) {
        this.plans = plans;
    }

}
