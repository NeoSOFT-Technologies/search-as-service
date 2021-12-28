package com.solr.clientwrapper.infrastructure.solrbean;

import java.util.List;

import org.apache.solr.common.SolrDocument;
import org.springframework.stereotype.Component;
import lombok.Data;


@Data
@Component
public class SolrSearchResult {
	private Long numDocs;
	private List<SolrDocument> solrDocuments;

	@Override
	public String toString() {
		return "SolrSearchResult [numDocs=" + numDocs + ", solrDocuments=" + solrDocuments + "]";
	}
}
