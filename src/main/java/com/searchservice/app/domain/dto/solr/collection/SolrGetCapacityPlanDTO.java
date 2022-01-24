package com.searchservice.app.domain.dto.solr.collection;

import com.searchservice.app.config.CapacityPlanProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@EqualsAndHashCode
public class SolrGetCapacityPlanDTO {

    private List<CapacityPlanProperties.Plan> plans;

    public SolrGetCapacityPlanDTO(List<CapacityPlanProperties.Plan> plans) {
        this.plans = plans;
    }

}
