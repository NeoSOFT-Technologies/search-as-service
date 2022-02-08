package com.searchservice.app.rest;


import com.searchservice.app.domain.dto.ResponseDTO;
import com.searchservice.app.domain.dto.logger.CorrelationID;
import com.searchservice.app.domain.port.api.InputDocumentServicePort;
import com.searchservice.app.rest.errors.InputDocumentException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api-versioned")
public class VersionedInputDocumentResource {

    private final Logger log = LoggerFactory.getLogger(VersionedInputDocumentResource.class);

    public final InputDocumentServicePort inputDocumentServicePort;
    
    CorrelationID correlationID = new CorrelationID();

	@Autowired
	HttpServletRequest request;

	ZonedDateTime utc = ZonedDateTime.now(ZoneOffset.UTC);

	private String servicename = "Manage_Table_Resource";

	private String username = "Username";

    public VersionedInputDocumentResource(InputDocumentServicePort inputDocumentServicePort) {
        this.inputDocumentServicePort = inputDocumentServicePort;
    }



    @PostMapping("/v1/ingest-nrt/{clientid}/{tableName}")
    @Operation(summary = "/ For add documents we have to pass the tableName and isNRT and it will return statusCode and message.", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseDTO documents(@PathVariable String tableName,@PathVariable int clientid, @RequestBody String payload){

        log.debug("Solr documents add");
        String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
		String correlationid = correlationID.generateUniqueCorrelationId();
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.set(CorrelationID.CORRELATION_ID_HEADER_NAME, correlationid);
		String ipaddress = request.getRemoteAddr();
		String timestamp = utc.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		log.info("--------Started Request of Service Name : {} , Username : {}, Corrlation Id : {}, IP Address : {}, TimeStamp : {}, Method name : {}",servicename, username, correlationid, ipaddress, timestamp, nameofCurrMethod);
		
        tableName = tableName+"_"+clientid;
        Instant start = Instant.now();
        ResponseDTO solrResponseDTO= inputDocumentServicePort.addDocuments(tableName, payload,correlationid,ipaddress);
        Instant end = Instant.now();      
        Duration timeElapsed = Duration.between(start, end);
        String result="Time taken: "+timeElapsed.toMillis()+" milliseconds";
        log.debug(result);
        log.info("-----------Successfully response Username : {}, Corrlation Id : {}, IP Address : {} , TimeStamp : {},  Method name : {}",username,correlationid,ipaddress,timestamp,nameofCurrMethod);
        return solrResponseDTO;

    }
    

	@PostMapping("/v1/ingest/{clientid}/{tableName}")
    @Operation(summary = "/ For add documents we have to pass the tableName and isNRT and it will return statusCode and message.", security = @SecurityRequirement(name = "bearerAuth"))

    public ResponseDTO document(@PathVariable String tableName,@PathVariable int clientid, @RequestBody String payload) {
      

        log.debug("Solr documents add");
        String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
		String correlationid = correlationID.generateUniqueCorrelationId();
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.set(CorrelationID.CORRELATION_ID_HEADER_NAME, correlationid);
		String ipaddress = request.getRemoteAddr();
		String timestamp = utc.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		log.info("--------Started Request of Service Name : {} , Username : {}, Corrlation Id : {}, IP Address : {}, TimeStamp : {}, Method name : {}",servicename, username, correlationid, ipaddress, timestamp, nameofCurrMethod);
		
        tableName = tableName+"_"+clientid;
        Instant start = Instant.now();
        ResponseDTO solrResponseDTO= inputDocumentServicePort.addDocument(tableName, payload,correlationid,ipaddress);
        Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start, end);
        String result="Time taken: "+timeElapsed.toMillis()+" milliseconds";
       log.debug(result);

        if(solrResponseDTO.getResponseStatusCode()==200){
        	log.info("-----------Successfully response Username : {}, Corrlation Id : {}, IP Address : {} , TimeStamp : {},  Method name : {}",username,correlationid,ipaddress,timestamp,nameofCurrMethod);
            return solrResponseDTO;
        }else{
        	log.info("-----------Failed response Username : {}, Corrlation Id : {}, IP Address : {} , TimeStamp : {},  Method name : {}",username,correlationid,ipaddress,timestamp,nameofCurrMethod);
        	throw new InputDocumentException(solrResponseDTO.getResponseStatusCode(),solrResponseDTO.getResponseMessage());
        }
    }


}
