package com.solr.clientwrapper.rest;

import com.solr.clientwrapper.domain.dto.solr.*;
import com.solr.clientwrapper.usecase.solr.core.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.net.URISyntaxException;

@RestController
@RequestMapping("/solr")
public class SolrCoreResource {

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

    @GetMapping("/coreStatus/{name}")
    @Operation(summary = "/core-status", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<String> coreStatus(@PathVariable String name) throws SolrServerException, IOException, URISyntaxException, ParserConfigurationException, InterruptedException, TransformerException, org.xml.sax.SAXException {
        String responseString=statusSolrCore.coreStatus(name);

        if(responseString.length()>=150){
            //CORE EXISTS
            return ResponseEntity.status(HttpStatus.OK).body(responseString);
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseString);
        }

    }

    @PostMapping("/createCore")
    @Operation(summary = "/create-core", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<SolrResponseDTO> createCore(@RequestBody SolrCreateCoreDTO solrCreateCoreDTO) throws SolrServerException, IOException, URISyntaxException, ParserConfigurationException, InterruptedException, TransformerException, org.xml.sax.SAXException {

        SolrResponseDTO solrResponseDTO=createSolrCore.createCore(solrCreateCoreDTO.getCoreName());

        if(solrResponseDTO.getStatusCode()==200){
            return ResponseEntity.status(HttpStatus.OK).body(solrResponseDTO);
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(solrResponseDTO);
        }

    }

    @PutMapping("/renameCore")
    @Operation(summary = "/rename-core", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<SolrResponseDTO> renameCore(@RequestBody SolrRenameCoreDTO solrRenameCoreDTO) throws SolrServerException, IOException, URISyntaxException, ParserConfigurationException, InterruptedException, TransformerException, org.xml.sax.SAXException {
        SolrResponseDTO solrResponseDTO= renameSolrCore.renameCore(solrRenameCoreDTO.getCoreName(), solrRenameCoreDTO.getNewName());

        if(solrResponseDTO.getStatusCode()==200){
            return ResponseEntity.status(HttpStatus.OK).body(solrResponseDTO);
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(solrResponseDTO);
        }

    }

    @DeleteMapping("/deleteCore")
    @Operation(summary = "/delete-core", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<SolrResponseDTO> deleteCore(@RequestBody SolrDeleteCoreDTO solrDeleteCoreDTO) throws SolrServerException, IOException, URISyntaxException, ParserConfigurationException, InterruptedException, TransformerException, org.xml.sax.SAXException {
        SolrResponseDTO solrResponseDTO=deleteSolrCore.deleteCore(solrDeleteCoreDTO.getCoreName(),solrDeleteCoreDTO.isDeleteIndex(),solrDeleteCoreDTO.isDeleteDataDir(),solrDeleteCoreDTO.isDeleteInstanceDir());

        if(solrResponseDTO.getStatusCode()==200){
            return ResponseEntity.status(HttpStatus.OK).body(solrResponseDTO);
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(solrResponseDTO);
        }

    }

    @PutMapping("/swapCore")
    @Operation(summary = "/swap-cores", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<SolrResponseDTO> swapCore(@RequestBody SolrSwapCoreDTO solrSwapCoreDTO) throws SolrServerException, IOException, URISyntaxException, ParserConfigurationException, InterruptedException, TransformerException, org.xml.sax.SAXException {
        SolrResponseDTO solrResponseDTO= swapSolrCore.swapCore(solrSwapCoreDTO.getCoreOne(), solrSwapCoreDTO.getCoreTwo());

        if(solrResponseDTO.getStatusCode()==200){
            return ResponseEntity.status(HttpStatus.OK).body(solrResponseDTO);
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(solrResponseDTO);
        }

    }

    @PostMapping("/reloadCore")
    @Operation(summary = "/reload-core", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<SolrResponseDTO> createCore(@RequestBody SolrReloadCoreDTO solrReloadCoreDTO) throws SolrServerException, IOException, URISyntaxException, ParserConfigurationException, InterruptedException, TransformerException, org.xml.sax.SAXException {
        SolrResponseDTO solrResponseDTO= reloadSolrCore.reloadCore(solrReloadCoreDTO.getCoreName());

        if(solrResponseDTO.getStatusCode()==200){
            return ResponseEntity.status(HttpStatus.OK).body(solrResponseDTO);
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(solrResponseDTO);
        }
    }

}
