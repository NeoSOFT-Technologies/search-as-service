package com.solr.clientwrapper.domain.dto.throttler;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ThrottlerRateLimitResponseDTO {
	private int statusCode;
	private String responseMsg;
	private String maxRequestsAllowed; // maximum number of requests allowed for current limitRefreshPeriod
	private String currentRefreshWindow; // limitRefreshPeriod, in seconds
	private String requestTimeoutDuration; // Incoming request timeout duration
}
