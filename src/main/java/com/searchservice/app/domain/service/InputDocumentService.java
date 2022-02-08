package com.searchservice.app.domain.service;

import com.searchservice.app.domain.dto.ResponseDTO;
import com.searchservice.app.domain.dto.logger.CorrelationID;
import com.searchservice.app.domain.dto.logger.IPAddress;
import com.searchservice.app.domain.port.api.InputDocumentServicePort;
import com.searchservice.app.domain.utils.DocumentParserUtil;
import com.searchservice.app.domain.utils.UploadDocumentUtil;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

@Service
public class InputDocumentService implements InputDocumentServicePort {


	
   @Value("${base-solr-url}")
    private String baseSolrUrl;

    private final Logger logger = LoggerFactory.getLogger(InputDocumentService.class);
    
    CorrelationID correlationID=new CorrelationID();
    
    @Autowired
    HttpServletRequest request;
    
    ZonedDateTime utc = ZonedDateTime.now(ZoneOffset.UTC);
    
    private String servicename = "Input_Document_Service";
    
    private String username = "Username";

    @Override
    public ResponseDTO addDocuments(String collectionName, String payload, boolean isNRT,String correlationid) {
    	
    	String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.set(CorrelationID.CORRELATION_ID_HEADER_NAME, correlationid); 	
		String ipaddress=request.getRemoteAddr();
		String timestamp=utc.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        logger.info("Started request of Service Name : {} , Username : {}, Corrlation Id : {} , IP Address : {}, TimeStamp : {}, Method name : {}",servicename,username,correlationid,ipaddress,timestamp,nameofCurrMethod);


        ResponseDTO responseDTO=new ResponseDTO(collectionName);

        Map<String,Map<String, Object>> schemaKeyValuePair= DocumentParserUtil.getSchemaOfCollection(baseSolrUrl,collectionName);
        if(schemaKeyValuePair == null){
            String message="Unable to get the Schema. Please check the collection name again!";
            logger.debug(message);
            responseDTO.setMessage(message);
            responseDTO.setStatusCode(400);
            return responseDTO;
        }

        JSONArray payloadJSONArray=null;
        try {
            payloadJSONArray = new JSONArray(payload);
        }catch (Exception e){

            //TRY BY REMOVING THE QUOTES FROM THE STRING
            try{

                payload=payload.substring(1, payload.length() - 1);
                payloadJSONArray = new JSONArray(payload);

            }catch (Exception e1){

                logger.debug(e.toString());
                logger.debug(e1.toString());

                String message="Invalid input JSON array of document.";
                logger.debug(message);
                responseDTO.setMessage(message);
                responseDTO.setStatusCode(400);
                return responseDTO;

            }
        }


        //CODE COMES HERE ONLY AFTER IT'S VERIFIED THAT THE PAYLOAD AND THE SCHEMA ARE STRUCTURALLY CORRECT

        UploadDocumentUtil uploadDocumentUtil =new UploadDocumentUtil();

        uploadDocumentUtil.setBaseSolrUrl(baseSolrUrl);
        uploadDocumentUtil.setCollectionName(collectionName);
        uploadDocumentUtil.setContent(payload);
        uploadDocumentUtil.setCommit(isNRT);

        UploadDocumentUtil.UploadDocumentSolrUtilRespnse response = uploadDocumentUtil.commit();

        if(response.isDocumentUploaded()){
            responseDTO.setMessage("Successfully Added!");
            responseDTO.setStatusCode(200);
            logger.info("Successfully Response Username : {}, Corrlation Id : {} , IP Address : {}, TimeStamp : {}, Method name : {}",username,correlationid,ipaddress,timestamp,nameofCurrMethod);
        }else{
            responseDTO.setMessage(response.getMessage());
            responseDTO.setStatusCode(400);
            logger.info("Failed Response Username : {}, Corrlation Id : {} , IP Address : {}, TimeStamp : {}, Method name : {}",username,correlationid,ipaddress,timestamp,nameofCurrMethod);
        }

        return responseDTO;

    }
}