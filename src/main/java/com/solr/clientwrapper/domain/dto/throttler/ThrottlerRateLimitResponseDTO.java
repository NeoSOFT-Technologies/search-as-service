package com.solr.clientwrapper.domain.dto.throttler;

import com.solr.clientwrapper.domain.dto.solr.SolrResponseDTO;

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
	private String currentRefreshWindow; // limitrefreshPeriod, in seconds
}
