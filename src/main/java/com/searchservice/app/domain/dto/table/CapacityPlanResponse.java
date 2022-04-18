package com.searchservice.app.domain.dto.table;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.searchservice.app.config.CapacityPlanProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CapacityPlanResponse {

	private int statusCode;
	private String message;
	private List<CapacityPlanProperties.Plan> plans;

}
