package com.solr.clientwrapper.domain.dto.throttler;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ThrottlerMaxRequestSizeResponseDTO {
	private int statusCode;
	private String responseMessage;
	private String maxAllowedRequestSize;
	private String incomingRequestSize;
	private String apiResponseData;
}
