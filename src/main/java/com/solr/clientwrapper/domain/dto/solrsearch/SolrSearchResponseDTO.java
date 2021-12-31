package com.solr.clientwrapper.domain.dto.solrsearch;

import org.springframework.stereotype.Component;

import com.solr.clientwrapper.infrastructure.solrbean.SolrSearchResult;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Component
public class SolrSearchResponseDTO {
	private int statusCode;
	private String responseMessage;
	private SolrSearchResult solrSearchResultResponse;
	
	public SolrSearchResponseDTO(String responseMessage, SolrSearchResult solrSearchResultResponse) {
		this.responseMessage = responseMessage;
		this.solrSearchResultResponse = solrSearchResultResponse;
	}

	@Override
	public String toString() {
		return "SolrSearchResponseDTO [statusCode=" + statusCode + ", "
						+ "responseMessage=" + responseMessage + ", "
						+ "solrSearchResultResponse=" + solrSearchResultResponse + "]";
	}
}
