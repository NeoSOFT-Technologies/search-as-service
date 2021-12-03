package com.solr.clientwrapper.domain.dto.solr.collection;

import com.solr.clientwrapper.config.CapacityPlanProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@EqualsAndHashCode
public class SolrGetCapacityPlanDTO {

    List<CapacityPlanProperties.Plan> plans;

    public SolrGetCapacityPlanDTO(List<CapacityPlanProperties.Plan> plans) {
        this.plans = plans;
    }

}
