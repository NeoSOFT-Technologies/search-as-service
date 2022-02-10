package com.searchservice.app.rest;


import com.searchservice.app.domain.dto.ResponseDTO;
import com.searchservice.app.domain.dto.logger.CorrelationID;
import com.searchservice.app.domain.dto.logger.LoggersDTO;
import com.searchservice.app.domain.port.api.InputDocumentServicePort;
import com.searchservice.app.domain.utils.LoggerUtils;
import com.searchservice.app.rest.errors.InputDocumentException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api")
public class InputDocumentResource {

	ZonedDateTime utc = ZonedDateTime.now(ZoneOffset.UTC);

	private String servicename = "Manage_Table_Resource";

	private String username = "Username";
	
    private final Logger log = LoggerFactory.getLogger(InputDocumentResource.class);

    public final InputDocumentServicePort inputDocumentServicePort;

    public InputDocumentResource(InputDocumentServicePort inputDocumentServicePort) {
        this.inputDocumentServicePort = inputDocumentServicePort;
    }

    @PostMapping("/v1/ingest-nrt/{clientid}/{tableName}")
    @Operation(summary = "/ For add documents we have to pass the tableName and isNRT and it will return statusCode and message.", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ResponseDTO> documents(@PathVariable String tableName,@PathVariable int clientid, @RequestBody String payload){

        log.debug("Solr documents add");

		String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
		String timestamp = utc.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		LoggersDTO loggersDTO = LoggerUtils.getRequestLoggingInfo(servicename, username,nameofCurrMethod,timestamp);
		LoggerUtils.Printlogger(loggersDTO,true,false);
		loggersDTO.setCorrelationid(loggersDTO.getCorrelationid());
		loggersDTO.setIpaddress(loggersDTO.getIpaddress());
		
        tableName = tableName+"_"+clientid;
        Instant start = Instant.now();
        ResponseDTO solrResponseDTO= inputDocumentServicePort.addDocuments(tableName, payload,loggersDTO);
        Instant end = Instant.now();      
        Duration timeElapsed = Duration.between(start, end);
        String result="Time taken: "+timeElapsed.toMillis()+" milliseconds";
       log.debug(result);

        if(solrResponseDTO.getResponseStatusCode()==200){
        	LoggerUtils.Printlogger(loggersDTO,false,false);
            return ResponseEntity.status(HttpStatus.OK).body(solrResponseDTO);
        }else{
        	LoggerUtils.Printlogger(loggersDTO,false,true);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(solrResponseDTO);
        }

    }
    

	@PostMapping("/v1/ingest/{clientid}/{tableName}")
    @Operation(summary = "/ For add documents we have to pass the tableName and isNRT and it will return statusCode and message.", security = @SecurityRequirement(name = "bearerAuth"))

    public ResponseEntity<ResponseDTO> document(@PathVariable String tableName,@PathVariable int clientid, @RequestBody String payload) {
      

        log.debug("Solr documents add");

		String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
		String timestamp = utc.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		LoggersDTO loggersDTO = LoggerUtils.getRequestLoggingInfo(servicename, username,nameofCurrMethod,timestamp);
		LoggerUtils.Printlogger(loggersDTO,true,false);
		loggersDTO.setCorrelationid(loggersDTO.getCorrelationid());
		loggersDTO.setIpaddress(loggersDTO.getIpaddress());
		
        tableName = tableName+"_"+clientid;
        Instant start = Instant.now();
        ResponseDTO solrResponseDTO= inputDocumentServicePort.addDocument(tableName, payload,loggersDTO);
        Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start, end);
        String result="Time taken: "+timeElapsed.toMillis()+" milliseconds";
		log.debug(result);

		loggersDTO.setServicename(servicename);
		loggersDTO.setUsername(username);
		loggersDTO.setNameofmethod(nameofCurrMethod);
		loggersDTO.setTimestamp(utc.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        if(solrResponseDTO.getResponseStatusCode()==200){
        	LoggerUtils.Printlogger(loggersDTO,false,false);
            return ResponseEntity.status(HttpStatus.OK).body(solrResponseDTO);
            
        }else{
        	LoggerUtils.Printlogger(loggersDTO,false,true);
        	throw new InputDocumentException(solrResponseDTO.getResponseStatusCode(),solrResponseDTO.getResponseMessage());
        }
    }


}
