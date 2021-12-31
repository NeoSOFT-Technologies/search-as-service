package com.solr.clientwrapper.domain.dto.solr;

import org.apache.solr.client.solrj.SolrClient;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode
public class SolrAPIAdapterResponseDTO {
	private int statusCode;
	private String responseMessage;
	private SolrClient solrClient;
	
	@Override
	public String toString() {
		return "SolrAPIAdapterResponseDTO [statusCode=" + statusCode + ", responseMessage=" + responseMessage
				+ ", solrClient=" + solrClient + "]";
	}
}
