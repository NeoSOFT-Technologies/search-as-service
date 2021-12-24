package com.solr.clientwrapper.domain.service;

import com.solr.clientwrapper.domain.dto.solr.SolrResponseDTO;
import com.solr.clientwrapper.domain.port.api.SolrDocumentServicePort;
import lombok.Data;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.request.schema.SchemaRequest;
import org.apache.solr.client.solrj.response.schema.SchemaResponse;
import org.apache.solr.common.SolrInputDocument;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class SolrDocumentService implements SolrDocumentServicePort {


	
   @Value("${base-solr-url}")
    private String baseSolrUrl;

    private final Logger log = LoggerFactory.getLogger(SolrDocumentService.class);

    public boolean isNumeric(String string) {
        long longValue;

        log.debug(String.format("Parsing string: \"%s\"", string));

        if(string == null || string.equals("")) {
            log.debug("String cannot be parsed, it is null or empty.");
            return false;
        }
        try {
            longValue = Long.parseLong(string);
            return true;
        } catch (NumberFormatException e) {
            log.debug("Input String cannot be parsed to Integer.");
        }
        return false;
    }

    public ObjectSatisfiesSchemaResponse isObjectSatisfySchema(Map<String,Map<String, Object>> schemaKeyValuePair, JSONObject payloadJSON){

        Iterator<String> itr = payloadJSON.keySet().iterator();

        //ITERATE THROUGH EACH KEY IN THE INPUT JSON OBJECT PAYLOAD
        while (itr.hasNext())
        {
            String payloadJsonObjectKey = itr.next();
            Object payloadJsonObjectValue = payloadJSON.get(payloadJsonObjectKey);

            System.out.println(payloadJsonObjectKey + "=" + payloadJsonObjectValue);
            System.out.println(schemaKeyValuePair.get(payloadJsonObjectKey));

            if(schemaKeyValuePair.containsKey(payloadJsonObjectKey)){

                Map<String, Object> fieldValueForTheKey = schemaKeyValuePair.get(payloadJsonObjectKey);
                String fieldTypeDefinedInSchema=fieldValueForTheKey.get("type").toString();

                //System.out.println(fieldTypeDefinedInSchema);

                switch (fieldTypeDefinedInSchema){
                    case "string":
                        if(!payloadJsonObjectValue.getClass().equals(String.class)){
                            System.out.println("Not a String");
                            return new ObjectSatisfiesSchemaResponse(false,"Not a string");
                        }
                        break;
                    case "strings":
                        if(!payloadJsonObjectValue.getClass().equals(JSONArray.class)){
                            System.out.println("Not a JSONArray");
                            return new ObjectSatisfiesSchemaResponse(false,"Not a JSONArray");
                        }
                        break;
                    case "plong":
                        if(!isNumeric(payloadJsonObjectValue.toString())){
                            System.out.println("Not a Long Number");
                            return new ObjectSatisfiesSchemaResponse(false,"Not a Long Number");
                        }
                        break;
                    case "plongs":
                        if(!payloadJsonObjectValue.getClass().equals(JSONArray.class)){
                            System.out.println("Not a JSONArray of Long Numbers");
                            return new ObjectSatisfiesSchemaResponse(false,"Not a JSONArray of Long Numbers");
                        }else{
                            System.out.println("JSONArray of Long Numbers/Strings");
                            JSONArray jsonArrayOfLongOrStrings=(JSONArray) payloadJsonObjectValue;

                            for(int i=0;i<jsonArrayOfLongOrStrings.length();i++){
                                if(!isNumeric(jsonArrayOfLongOrStrings.getString(i))){
                                    System.out.println("JSONArray of Long Numbers contains a string and not long");
                                    return new ObjectSatisfiesSchemaResponse(false,"JSONArray of Long Numbers doesn't contains a string and not long");
                                }
                            }
                        }
                        break;
                    case "boolean":
                        if(!payloadJsonObjectValue.getClass().equals(Boolean.class)){
                            System.out.println("Not a Boolean");
                            return new ObjectSatisfiesSchemaResponse(false,"Not a Boolean");
                        }
                        break;
                    case "booleans":
                        if(!payloadJsonObjectValue.getClass().equals(JSONArray.class)){
                            System.out.println("Not a JSONArray of Booleans");
                            return new ObjectSatisfiesSchemaResponse(false,"Not a JSONArray of Booleans");
                        }else{
                            System.out.println("JSONArray of Booleans/Strings");
                            JSONArray jsonArrayOfBooleanOrStrings=(JSONArray) payloadJsonObjectValue;

                            for(int i=0;i<jsonArrayOfBooleanOrStrings.length();i++){
                                if(!jsonArrayOfBooleanOrStrings.get(i).getClass().equals(Boolean.class)){
                                    System.out.println("JSONArray of Booleans contains a string and not boolean");
                                    return new ObjectSatisfiesSchemaResponse(false,"JSONArray of Booleans contains a string and not boolean");
                                }
                            }
                        }
                        break;
                    default:
                        return new ObjectSatisfiesSchemaResponse(false,"Code unable to handle the schema data type. Contact the developer!");
                }

            }else{
                log.debug("Input JSON Object's key doesn't exists in the Schema");
                return new ObjectSatisfiesSchemaResponse(false,"Input JSON Object's key doesn't exists in the Schema");
            }
        }

        return new ObjectSatisfiesSchemaResponse(true,"Success!");
    }

    @Override
    public SolrResponseDTO create(String collectionName, String payload) {

        SolrClient solrClient=new HttpSolrClient.Builder(baseSolrUrl+"/"+collectionName).build();

        SolrResponseDTO solrResponseDTO=new SolrResponseDTO(collectionName);

        SchemaRequest schemaRequest = new SchemaRequest();
        SchemaResponse schemaResponse = null;
        try {
            schemaResponse = schemaRequest.process(solrClient);
        } catch (Exception e) {
            log.error(e.toString());
            log.debug("Unable to get the Schema");
            solrResponseDTO.setMessage("Unable to get the Schema");
            solrResponseDTO.setStatusCode(400);
            return solrResponseDTO;
        }

        List<Map<String, Object>> schemaResponseFields= schemaResponse.getSchemaRepresentation().getFields();

        // Converting response schema from Solr to HashMap for quick access
        //Key contains the field name and value contains the object which has schema description of that key eg. multivalued etc
        Map<String,Map<String, Object>> schemaKeyValuePair=new HashMap<>();
        schemaResponseFields.forEach((fieldObject)->schemaKeyValuePair.put(fieldObject.get("name").toString(),fieldObject));

        System.out.println(payload);
       
        JSONObject payloadJSON=null;
        try {
            payloadJSON = new JSONObject(payload);
        }catch (Exception e){
            log.debug(e.toString());
            log.debug("Invalid JSON Object");
            solrResponseDTO.setMessage("Invalid JSON Object");
            solrResponseDTO.setStatusCode(400);
            return solrResponseDTO;
        }

        //CODE COMES HERE ONLY AFTER IT'S VERIFIED THAT THE PAYLOAD AND THE SCHEMA ARE STRUCTURALLY CORRECT

        Set<String> res=payloadJSON.keySet();
        System.out.println("Key sets : " + res);

        System.out.println(schemaKeyValuePair);
        ObjectSatisfiesSchemaResponse objectSatisfiesSchemaResponse = isObjectSatisfySchema(schemaKeyValuePair,payloadJSON);

        solrResponseDTO.setMessage(objectSatisfiesSchemaResponse.getMessage());

        if(objectSatisfiesSchemaResponse.isObjectSatisfiesSchema()){
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
                log.debug("Error while adding document to Solr");
                solrResponseDTO.setMessage("Error while adding document to Solr");
                solrResponseDTO.setStatusCode(400);
                return solrResponseDTO;
            }

            solrResponseDTO.setMessage("Successfully Added!");
            solrResponseDTO.setStatusCode(200);
            return solrResponseDTO;

        }else{
            solrResponseDTO.setMessage("The JSON Object doesn't satisfies the Schema. "+objectSatisfiesSchemaResponse.getMessage());
            solrResponseDTO.setStatusCode(400);
            return solrResponseDTO;
        }

    }


    @Data
    class ObjectSatisfiesSchemaResponse {
        boolean isObjectSatisfiesSchema;
        String message;
        public ObjectSatisfiesSchemaResponse(boolean isObjectSatisfiesSchema, String message) {
            this.isObjectSatisfiesSchema = isObjectSatisfiesSchema;
            this.message = message;
        }
    }

}
