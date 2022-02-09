package com.searchservice.app.rest;


import com.searchservice.app.domain.dto.ResponseDTO;
import com.searchservice.app.domain.dto.table.TableSchemaDTO;
import com.searchservice.app.domain.dto.throttler.ThrottlerResponseDTO;
import com.searchservice.app.domain.port.api.InputDocumentServicePort;
import com.searchservice.app.domain.port.api.ThrottlerServicePort;
import com.searchservice.app.rest.errors.InputDocumentException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.Instant;

@RestController
@RequestMapping("${base-url.api-endpoint.versioned-home}")
public class VersionedInputDocumentResource {

    private final Logger log = LoggerFactory.getLogger(VersionedInputDocumentResource.class);

    public final InputDocumentServicePort inputDocumentServicePort;
    public final ThrottlerServicePort throttlerServicePort;
    public VersionedInputDocumentResource(
    		InputDocumentServicePort inputDocumentServicePort, 
    		ThrottlerServicePort throttlerServicePort) {
        this.inputDocumentServicePort = inputDocumentServicePort;
        this.throttlerServicePort = throttlerServicePort;
    }


    @PostMapping("/ingest-nrt/{clientid}/{tableName}")
    @Operation(summary = "/ For add documents we have to pass the tableName and isNRT and it will return statusCode and message.", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseDTO documents(
    		@PathVariable String tableName, 
    		@PathVariable int clientid, 
    		@RequestBody String payload){

        log.debug("Solr documents add");
        tableName = tableName+"_"+clientid;
        Instant start = Instant.now();
        ResponseDTO solrResponseDTO= inputDocumentServicePort.addDocuments(tableName, payload);
        Instant end = Instant.now();      
        Duration timeElapsed = Duration.between(start, end);
        String result="Time taken: "+timeElapsed.toMillis()+" milliseconds";
        log.debug(result);

        return solrResponseDTO;

    }
    

	@PostMapping("/ingest/{clientid}/{tableName}")
    @Operation(summary = "/ For add documents we have to pass the tableName and isNRT and it will return statusCode and message.", security = @SecurityRequirement(name = "bearerAuth"))

    public ResponseDTO document(
    		@PathVariable String tableName, 
    		@PathVariable int clientid, 
    		@RequestBody String payload) {

        log.debug("Solr documents add");

        tableName = tableName+"_"+clientid;
        Instant start = Instant.now();
        ResponseDTO solrResponseDTO= inputDocumentServicePort.addDocument(tableName, payload);
        Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start, end);
        String result="Time taken: "+timeElapsed.toMillis()+" milliseconds";
        log.debug(result);

        if(solrResponseDTO.getResponseStatusCode()==200){
            return solrResponseDTO;
        }else{
        	throw new InputDocumentException(solrResponseDTO.getResponseStatusCode(),solrResponseDTO.getResponseMessage());
        }
    }

	
    @GetMapping("/testMRS")
    @Operation(summary = "/ Applying max request size throttling  on data injection.", security = @SecurityRequirement(name = "bearerAuth"))
    public Object demoMRSThrottler(@RequestParam String data) {
    	/*
    	 * MRS stands for- Max RequestBody Size
    	 */
    	ThrottlerResponseDTO throttlerMaxRequestSizeResponseDTO
    		= throttlerServicePort.documentInjectionRequestSizeLimiter(data, true);
    	if(throttlerMaxRequestSizeResponseDTO.getStatusCode() == 406)
    		return new TableSchemaDTO(1000, "Testing version Mapper with Object!");
    	return throttlerMaxRequestSizeResponseDTO;
    }

}
