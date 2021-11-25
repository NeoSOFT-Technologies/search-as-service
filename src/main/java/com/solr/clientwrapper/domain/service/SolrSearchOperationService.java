package com.solr.clientwrapper.domain.service;

import java.io.IOException;
import java.util.List;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.SortClause;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.beans.DocumentObjectBinder;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.solr.clientwrapper.domain.port.api.SolrSearchOperationServicePort;
import com.solr.clientwrapper.infrastructure.solrbean.SolrCollectionIndex;
import com.solr.clientwrapper.infrastructure.solrbean.SolrSearchResult;

@Service
@Transactional
public class SolrSearchOperationService implements SolrSearchOperationServicePort {

	@Autowired
	SolrSearchResult solrSearchResult;
	@Autowired
	SolrCollectionIndex solrCollectionIndex;

	@Override
	public SolrSearchResult setUpSelectQuery(	String collection, 
												String queryField, 
												String searchTerm, 
												String startRecord, 
												String pageSize,
												String tag, String order) {
		SolrClient client = new HttpSolrClient.Builder("http://localhost:8983/solr/"+collection).build();
		SolrQuery query = new SolrQuery();
		
		query.set("q", queryField + ":" + searchTerm);
		query.set("start", startRecord);
		query.set("rows", pageSize);
		SortClause sortClause = new SortClause(tag, order);
		query.setSort(sortClause);
		
		try {
			QueryResponse response = client.query(query);
			SolrDocumentList docs = response.getResults();
			DocumentObjectBinder binder = new DocumentObjectBinder();
			List<SolrCollectionIndex> docsBeans = binder.getBeans(SolrCollectionIndex.class, docs);
			response = client.query(query);
			response.getDebugMap();
			long numDocs = docs.getNumFound();
			solrSearchResult.setNumDocs(numDocs);
			solrSearchResult.setSolrCollectionIndex(docsBeans);
			return solrSearchResult;
		} catch (SolrServerException | IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public SolrSearchResult setUpSelectQueryWithPagination(	String collection, 
															String queryField, 
															String searchTerm,
															String startRecord, 
															String pageSize, 
															String tag, String order, 
															String startPage) {
		SolrClient client = new HttpSolrClient.Builder("http://localhost:8983/solr/"+collection).build();
		SolrQuery query = new SolrQuery();
		
		query.set("q", queryField + ":" + searchTerm);
		query.set("start", startRecord);
		query.set("rows", pageSize);
		SortClause sortClause = new SortClause(tag, order);
		query.setSort(sortClause);
		
		try {
			QueryResponse response = client.query(query);
			SolrDocumentList docs = response.getResults();
			DocumentObjectBinder binder = new DocumentObjectBinder();
			List<SolrCollectionIndex> docsBeans = binder.getBeans(SolrCollectionIndex.class, docs);
			response = client.query(query);
			response.getDebugMap();
			long numDocs = docs.getNumFound();
			solrSearchResult.setNumDocs(numDocs);
			solrSearchResult.setSolrCollectionIndex(docsBeans);
			return solrSearchResult;
		} catch (SolrServerException | IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
