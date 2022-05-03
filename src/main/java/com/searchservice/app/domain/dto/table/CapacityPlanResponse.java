package com.searchservice.app.domain.dto.table;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.searchservice.app.config.CapacityPlanProperties;
import com.searchservice.app.config.CapacityPlanProperties.Plan;
import com.searchservice.app.domain.dto.BaseResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CapacityPlanResponse extends BaseResponse{

	private List<CapacityPlanProperties.Plan> plans;
	
	public CapacityPlanResponse(int statusCode, String message) {
		super(statusCode, message);
	}
	public CapacityPlanResponse(int statusCode, String message, List<Plan> plans) {
		super(statusCode, message);
		this.plans = plans;
	}
	
}
