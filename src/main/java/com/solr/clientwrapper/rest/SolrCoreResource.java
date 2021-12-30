package com.solr.clientwrapper.rest;

import com.solr.clientwrapper.domain.dto.solr.SolrResponseDTO;
import com.solr.clientwrapper.domain.dto.solr.core.SolrDoubleCoreDTO;
import com.solr.clientwrapper.domain.dto.solr.core.SolrSingleCoreDTO;
import com.solr.clientwrapper.usecase.solr.core.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/solr-core")
public class SolrCoreResource {

    private final Logger log = LoggerFactory.getLogger(SolrCoreResource.class);

    private final CreateSolrCore createSolrCore;
    private final RenameSolrCore renameSolrCore;
    private final DeleteSolrCore deleteSolrCore;
    private final SwapSolrCore swapSolrCore;
    private final ReloadSolrCore reloadSolrCore;
    private final StatusSolrCore statusSolrCore;

    public SolrCoreResource(CreateSolrCore createSolrCore, RenameSolrCore renameSolrCore, DeleteSolrCore deleteSolrCore, SwapSolrCore swapSolrCore, ReloadSolrCore reloadSolrCore, StatusSolrCore statusSolrCore) {
        this.createSolrCore = createSolrCore;
        this.renameSolrCore = renameSolrCore;
        this.deleteSolrCore = deleteSolrCore;
        this.swapSolrCore=swapSolrCore;
        this.reloadSolrCore=reloadSolrCore;
        this.statusSolrCore=statusSolrCore;

    }

    @GetMapping("/status/{name}")
    @Operation(summary = "/core-status", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<String> status(@PathVariable String name)  {

        log.debug("Solr Core status");

        String responseString=statusSolrCore.status(name);

        if(responseString.length()>=150){
            //CORE EXISTS
            return ResponseEntity.status(HttpStatus.OK).body(responseString);
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseString);
        }

    }

    @PostMapping("/create")
    @Operation(summary = "/create-core", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<SolrResponseDTO> create(@RequestBody SolrSingleCoreDTO solrSingleCoreDTO)  {

        log.debug("Solr Core create");

        SolrResponseDTO solrResponseDTO=createSolrCore.create(solrSingleCoreDTO.getCoreName());

        if(solrResponseDTO.getStatusCode()==200){
            return ResponseEntity.status(HttpStatus.OK).body(solrResponseDTO);
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(solrResponseDTO);
        }

    }

    @PutMapping("/rename")
    @Operation(summary = "/rename-core", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<SolrResponseDTO> rename(@RequestBody SolrDoubleCoreDTO solrDoubleCoreDTO)  {

        log.debug("Solr Core rename");

        SolrResponseDTO solrResponseDTO= renameSolrCore.rename(solrDoubleCoreDTO.getCoreOne(), solrDoubleCoreDTO.getCoreTwo());

        if(solrResponseDTO.getStatusCode()==200){
            return ResponseEntity.status(HttpStatus.OK).body(solrResponseDTO);
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(solrResponseDTO);
        }

    }

    @DeleteMapping("/delete/{coreName}")
    @Operation(summary = "/delete-core", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<SolrResponseDTO> delete(@PathVariable String coreName)  {

        log.debug("Solr Core delete");

        SolrResponseDTO solrResponseDTO=deleteSolrCore.delete(coreName);

        if(solrResponseDTO.getStatusCode()==200){
            return ResponseEntity.status(HttpStatus.OK).body(solrResponseDTO);
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(solrResponseDTO);
        }

    }

    @PutMapping("/swap")
    @Operation(summary = "/swap-cores", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<SolrResponseDTO> swap(@RequestBody SolrDoubleCoreDTO solrDoubleCoreDTO)  {

        log.debug("Solr Core swap");

        SolrResponseDTO solrResponseDTO= swapSolrCore.swap(solrDoubleCoreDTO.getCoreOne(), solrDoubleCoreDTO.getCoreTwo());

        if(solrResponseDTO.getStatusCode()==200){
            return ResponseEntity.status(HttpStatus.OK).body(solrResponseDTO);
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(solrResponseDTO);
        }

    }

    @PostMapping("/reload")
    @Operation(summary = "/reload-core", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<SolrResponseDTO> reload(@RequestBody SolrSingleCoreDTO solrSingleCoreDTO)  {

        log.debug("Solr Core reload");

        SolrResponseDTO solrResponseDTO= reloadSolrCore.reload(solrSingleCoreDTO.getCoreName());

        if(solrResponseDTO.getStatusCode()==200){
            return ResponseEntity.status(HttpStatus.OK).body(solrResponseDTO);
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(solrResponseDTO);
        }
    }

}
