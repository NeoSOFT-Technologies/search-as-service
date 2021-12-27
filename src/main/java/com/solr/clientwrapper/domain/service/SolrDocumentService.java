package com.solr.clientwrapper.domain.service;

import com.solr.clientwrapper.domain.dto.solr.SolrResponseDTO;
import com.solr.clientwrapper.domain.port.api.SolrDocumentServicePort;
import com.solr.clientwrapper.domain.utils.DocumentParserUtil;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.common.SolrInputDocument;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class SolrDocumentService implements SolrDocumentServicePort {


	
   @Value("${base-solr-url}")
    private String baseSolrUrl;

    private final Logger log = LoggerFactory.getLogger(SolrDocumentService.class);

    @Override
    public SolrResponseDTO addDocument(String collectionName, String payload) {

        SolrClient solrClient=new HttpSolrClient.Builder(baseSolrUrl+"/"+collectionName).build();

        SolrResponseDTO solrResponseDTO=new SolrResponseDTO(collectionName);

        Map<String,Map<String, Object>> schemaKeyValuePair= DocumentParserUtil.getSchemaOfCollection(baseSolrUrl,collectionName);
        if(schemaKeyValuePair == null){
            String message="Unable to get the Schema. Please check the collection name again!";
            log.debug(message);
            solrResponseDTO.setMessage(message);
            solrResponseDTO.setStatusCode(400);
            return solrResponseDTO;
        }


//        log.debug(payload);

        JSONObject payloadJSON=null;
        try {
            payloadJSON = new JSONObject(payload);
        }catch (Exception e){
            log.debug(e.toString());
            String message="Invalid input JSON document.";
            log.debug(message);
            solrResponseDTO.setMessage(message);
            solrResponseDTO.setStatusCode(400);
            return solrResponseDTO;
        }

        //CODE COMES HERE ONLY AFTER IT'S VERIFIED THAT THE PAYLOAD AND THE SCHEMA ARE STRUCTURALLY CORRECT

//        Set<String> res=payloadJSON.keySet();
//        log.debug("Key sets : " + res);
//        log.debug("Solr Schema : "+schemaKeyValuePair);


        DocumentParserUtil.DocumentSatisfiesSchemaResponse documentSatisfiesSchemaResponse = DocumentParserUtil.isDocumentSatisfySchema(schemaKeyValuePair,payloadJSON);

        solrResponseDTO.setMessage(documentSatisfiesSchemaResponse.getMessage());

        if(documentSatisfiesSchemaResponse.isObjectSatisfiesSchema()){
            solrResponseDTO.setStatusCode(200);

            SolrInputDocument document = new SolrInputDocument();
            //ITERATE THROUGH EACH KEY IN THE INPUT JSON OBJECT PAYLOAD
            Iterator<String> itr = payloadJSON.keySet().iterator();
            while (itr.hasNext())
            {
                String payloadJsonObjectKey = itr.next();
                Object payloadJsonObjectValue = payloadJSON.get(payloadJsonObjectKey);

                document.addField(payloadJsonObjectKey,payloadJsonObjectValue);
            }

            try {
                solrClient.add(document);
                solrClient.commit();
            } catch (Exception e) {
                log.error(e.toString());
                String message="Error while adding document to Solr.";
                log.debug(message);
                solrResponseDTO.setMessage(message);
                solrResponseDTO.setStatusCode(400);
                return solrResponseDTO;
            }

            solrResponseDTO.setMessage("Successfully Added!");
            solrResponseDTO.setStatusCode(200);
            return solrResponseDTO;

        }else{
            solrResponseDTO.setMessage("The JSON input document doesn't satisfy the Schema. Error: "+ documentSatisfiesSchemaResponse.getMessage());
            solrResponseDTO.setStatusCode(400);
            return solrResponseDTO;
        }

    }

    @Override
    public SolrResponseDTO addDocuments(String collectionName, String payload) {

        SolrClient solrClient=new HttpSolrClient.Builder(baseSolrUrl+"/"+collectionName).build();

        SolrResponseDTO solrResponseDTO=new SolrResponseDTO(collectionName);

        Map<String,Map<String, Object>> schemaKeyValuePair= DocumentParserUtil.getSchemaOfCollection(baseSolrUrl,collectionName);
        if(schemaKeyValuePair == null){
            String message="Unable to get the Schema. Please check the collection name again!";
            log.debug(message);
            solrResponseDTO.setMessage(message);
            solrResponseDTO.setStatusCode(400);
            return solrResponseDTO;
        }

       //log.debug(payload);

        JSONArray payloadJSONArray=null;
        try {
            payloadJSONArray = new JSONArray(payload);
        }catch (Exception e){
            log.debug(e.toString());
            String message="Invalid input JSON array of document.";
            log.debug(message);
            solrResponseDTO.setMessage(message);
            solrResponseDTO.setStatusCode(400);
            return solrResponseDTO;
        }


        //CODE COMES HERE ONLY AFTER IT'S VERIFIED THAT THE PAYLOAD AND THE SCHEMA ARE STRUCTURALLY CORRECT

//        Set<String> res=payloadJSON.keySet();
//        log.debug("Key sets : " + res);
//        log.debug("Solr Schema : "+schemaKeyValuePair);

        SolrInputDocument inputDocumentMaster=new SolrInputDocument();
        List<SolrInputDocument> solrInputDocumentList = new ArrayList<>();

        for(int i=0;i<payloadJSONArray.length();i++){

            JSONObject jsonSingleObject= (JSONObject) payloadJSONArray.get(i);

            DocumentParserUtil.DocumentSatisfiesSchemaResponse documentSatisfiesSchemaResponse =
                    DocumentParserUtil.isDocumentSatisfySchema(schemaKeyValuePair,jsonSingleObject);

            if(documentSatisfiesSchemaResponse.isObjectSatisfiesSchema()){

                SolrInputDocument solrInputSingleDocument=new SolrInputDocument();

                Iterator<String> itr = jsonSingleObject.keySet().iterator();
                while (itr.hasNext())
                {
                    String payloadJsonObjectKey = itr.next();
                    Object payloadJsonObjectValue = jsonSingleObject.get(payloadJsonObjectKey);

                    solrInputSingleDocument.addField(payloadJsonObjectKey,payloadJsonObjectValue);
                }

                solrInputDocumentList.add(solrInputSingleDocument);

            }else{
                //ERROR IN A DOCUMENT STRUCTURE
                solrResponseDTO.setMessage(documentSatisfiesSchemaResponse.getMessage());
                solrResponseDTO.setMessage("The JSON input document in the array doesn't satisfy the Schema. Error: "+ documentSatisfiesSchemaResponse.getMessage());
                solrResponseDTO.setStatusCode(400);
                return solrResponseDTO;
            }

        }

        inputDocumentMaster.addChildDocuments(solrInputDocumentList);


        try {
            solrClient.add(inputDocumentMaster);
            solrClient.commit();
        } catch (Exception e) {
            log.error(e.toString());
            String message="Error while adding document to Solr.";
            log.debug(message);
            solrResponseDTO.setMessage(message);
            solrResponseDTO.setStatusCode(400);
            return solrResponseDTO;
        }

        solrResponseDTO.setMessage("Successfully Added!");
        solrResponseDTO.setStatusCode(200);
        return solrResponseDTO;


    }


}
