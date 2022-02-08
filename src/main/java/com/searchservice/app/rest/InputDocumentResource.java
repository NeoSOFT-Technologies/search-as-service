package com.searchservice.app.rest;


import com.searchservice.app.domain.dto.ResponseDTO;
import com.searchservice.app.domain.dto.logger.CorrelationID;
import com.searchservice.app.domain.dto.logger.IPAddress;
import com.searchservice.app.domain.port.api.InputDocumentServicePort;
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
	

    private final Logger log = LoggerFactory.getLogger(InputDocumentResource.class);
    
    CorrelationID correlationID=new CorrelationID();
    
    @Autowired
    HttpServletRequest request;
    
    ZonedDateTime utc = ZonedDateTime.now(ZoneOffset.UTC);
    
    private String servicename = "Input_Document_Resource";
    
    private String username = "Username";

    public final InputDocumentServicePort inputDocumentServicePort;

    public InputDocumentResource(InputDocumentServicePort inputDocumentServicePort) {
        this.inputDocumentServicePort = inputDocumentServicePort;
    }

    @PostMapping("/documents/{tableName}")
    @Operation(summary = "/ For add documents we have to pass the tableName and isNRT and it will return statusCode and message.", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ResponseDTO> documents(@PathVariable String tableName, @RequestBody String payload, @RequestParam boolean isNRT) {

        log.debug("Solr documents add");

        String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
		String correlationid = correlationID.generateUniqueCorrelationId();
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.set(CorrelationID.CORRELATION_ID_HEADER_NAME, correlationid); 	
		String ipaddress=request.getRemoteAddr();
		String timestamp=utc.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        log.info("Started request of Service Name : {} , Username : {}, Corrlation Id : {} , IP Address : {},TimeStamp : {}, Method name : {}",servicename,username,correlationid,ipaddress,timestamp,nameofCurrMethod);

        Instant start = Instant.now();
        ResponseDTO solrResponseDTO= inputDocumentServicePort.addDocuments(tableName, payload, isNRT,correlationid);
        Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start, end);
        String result="Time taken: "+timeElapsed.toMillis()+" milliseconds";
        log.debug(result);

        if(solrResponseDTO.getStatusCode()==200){
        	log.info("Successfully Response Username : {}, Corrlation Id : {} , IP Address : {}, TimeStamp : {}, Method name : {}",username,correlationid,ipaddress,timestamp,nameofCurrMethod);
            return ResponseEntity.status(HttpStatus.OK).body(solrResponseDTO);
        }else{
        	log.info("Failed Response Username : {}, Corrlation Id : {} , IP Address : {}, TimeStamp : {}, Method name : {}",username,correlationid,ipaddress,timestamp,nameofCurrMethod);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(solrResponseDTO);
        }

    }
}
