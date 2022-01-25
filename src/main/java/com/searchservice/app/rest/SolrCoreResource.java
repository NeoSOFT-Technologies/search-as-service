package com.searchservice.app.rest;


import com.searchservice.app.domain.dto.ResponseDTO;
import com.searchservice.app.domain.dto.core.SolrDoubleCoreDTO;
import com.searchservice.app.domain.dto.core.SolrSingleCoreDTO;
import com.searchservice.app.domain.port.api.SolrCoreServicePort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

//@RestController
@RequestMapping("/solr-core")
public class SolrCoreResource {

    private final Logger log = LoggerFactory.getLogger(SolrCoreResource.class);

    private final SolrCoreServicePort solrCoreServicePort;

    public SolrCoreResource(SolrCoreServicePort solrCoreServicePort) {
        this.solrCoreServicePort = solrCoreServicePort;
    }


    @GetMapping("/status/{name}")
    @Operation(summary = "/core-status", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<String> status(@PathVariable String name)  {

        log.debug("Solr Core status");

        String responseString=solrCoreServicePort.status(name);

        if(responseString.length()>=150){
            //CORE EXISTS
            return ResponseEntity.status(HttpStatus.OK).body(responseString);
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseString);
        }

    }

    @PostMapping("/create")
    @Operation(summary = "/create-core", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ResponseDTO> create(@RequestBody SolrSingleCoreDTO solrSingleCoreDTO)  {

        log.debug("Solr Core create");

        ResponseDTO responseDTO=solrCoreServicePort.create(solrSingleCoreDTO.getCoreName());

        if(responseDTO.getStatusCode()==200){
            return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseDTO);
        }

    }

    @PutMapping("/rename")
    @Operation(summary = "/rename-core", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ResponseDTO> rename(@RequestBody SolrDoubleCoreDTO solrDoubleCoreDTO)  {

        log.debug("Solr Core rename");

        ResponseDTO responseDTO= solrCoreServicePort.rename(solrDoubleCoreDTO.getCoreOne(), solrDoubleCoreDTO.getCoreTwo());

        if(responseDTO.getStatusCode()==200){
            return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseDTO);
        }

    }

    @DeleteMapping("/delete/{coreName}")
    @Operation(summary = "/delete-core", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ResponseDTO> delete(@PathVariable String coreName)  {

        log.debug("Solr Core delete");

        ResponseDTO responseDTO=solrCoreServicePort.delete(coreName);

        if(responseDTO.getStatusCode()==200){
            return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseDTO);
        }

    }

    @PutMapping("/swap")
    @Operation(summary = "/swap-cores", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ResponseDTO> swap(@RequestBody SolrDoubleCoreDTO solrDoubleCoreDTO)  {

        log.debug("Solr Core swap");

        ResponseDTO responseDTO= solrCoreServicePort.swap(solrDoubleCoreDTO.getCoreOne(), solrDoubleCoreDTO.getCoreTwo());

        if(responseDTO.getStatusCode()==200){
            return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseDTO);
        }

    }

    @PostMapping("/reload")
    @Operation(summary = "/reload-core", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ResponseDTO> reload(@RequestBody SolrSingleCoreDTO solrSingleCoreDTO)  {

        log.debug("Solr Core reload");

        ResponseDTO responseDTO= solrCoreServicePort.reload(solrSingleCoreDTO.getCoreName());

        if(responseDTO.getStatusCode()==200){
            return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseDTO);
        }
    }

}
