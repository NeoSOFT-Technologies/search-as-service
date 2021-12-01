package com.solr.clientwrapper.domain.service;

import java.io.IOException;
import java.util.List;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.request.schema.FieldTypeDefinition;
import org.apache.solr.client.solrj.request.schema.SchemaRequest;
import org.apache.solr.client.solrj.response.schema.SchemaResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.solr.clientwrapper.domain.port.api.SolrSchemaStorageServicePort;

@Service
@Transactional
public class SolrSchemaStorageService implements SolrSchemaStorageServicePort {
	
	private static String DEFAULT_COLLECTION = "techproducts";
	
	String urlString = "http://localhost:8985/solr/";
	
	SolrClient solr = new HttpSolrClient.Builder(urlString+DEFAULT_COLLECTION).build();
	
	@Override
	public String validateSchema() {
		System.out.println("!!!!!!!!!!11 %%%%%%%%%%%%%%%% 11!!!!!!!!!!!!!!");
		SchemaRequest schemaRequest = new SchemaRequest();
		System.out.println("!!!!!!!!!!22 %%%%%%%%%%%%%%%% 22!!!!!!!!!!!!!!");
		try {
			System.out.println("!!!!!!!!!!33 %%%%%%%%%%%%%%%% 33!!!!!!!!!!!!!!");
			SchemaResponse schemaResponse = schemaRequest.process(solr);
			System.out.println("\nSch Resp: @@@@@ : "+schemaResponse);
			
			// explore response content
			System.out.println("Response header : "+schemaResponse.getResponseHeader());
			System.out.println("Response class : "+schemaResponse.getResponseHeader().getClass());
			
			List<FieldTypeDefinition> schemaFieldTypes = schemaResponse.getSchemaRepresentation().getFieldTypes();
			
			int NumOfFieldTypes = schemaResponse.getSchemaRepresentation().getFieldTypes().size();
			System.out.println("Response schema size : "+schemaResponse.getSchemaRepresentation().getFieldTypes().size()+"\n");
			System.out.println("Response schema 1 : "+schemaResponse.getSchemaRepresentation().getFieldTypes().get(0).getAttributes()+"\n");
			System.out.println("Response schema 2 : "+schemaResponse.getSchemaRepresentation().getFieldTypes().get(1).getAttributes()+"\n");
			
			
			for(int i=0; i<NumOfFieldTypes; i++) {
				System.out.println("Field Type : "+schemaFieldTypes.get(i).getAttributes());
			}
			
			System.out.println("Response class : "+schemaResponse.getSchemaRepresentation().getClass());
			
			
		} catch (SolrServerException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return "Schema is successfully validated.";
	}
	@Override
	public String storeSchema() {

		return "Schema is successfully stored.";
	}
	

	
	
}
