package com.searchservice.app.domain.dto.throttler;

import com.fasterxml.jackson.annotation.JsonInclude;
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
public class ThrottlerResponse implements VersionedObjectMapper {
	private int statusCode;
	private String message;
	
	// Rate Limiter member variables
	private String maxRequestsAllowed; // maximum number of requests allowed for current limitRefreshPeriod
	private String currentRefreshWindow; // limitRefreshPeriod, in seconds
	private String requestTimeoutDuration; // Incoming request timeout duration

	// MaxRequestSize Limiter member variables
	private String maxAllowedRequestSize;
	private String incomingRequestSize;
	private String apiResponseData;
	
	public ThrottlerResponse(int statusCode, String responseMessage) {
		this.statusCode = statusCode;
		this.message = responseMessage;
	}
	
	@Override
	public VersionedObjectMapper toVersion(int version) {
		return this;
	}
	
}