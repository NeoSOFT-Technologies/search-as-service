package com.solr.clientwrapper.rest;

import com.solr.clientwrapper.domain.dto.solr.*;
import com.solr.clientwrapper.usecase.solr.core.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.apache.solr.client.solrj.SolrServerException;
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
    public String coreStatus(@PathVariable String name) throws SolrServerException, IOException, URISyntaxException, ParserConfigurationException, InterruptedException, TransformerException, org.xml.sax.SAXException {
        return statusSolrCore.coreStatus(name);
    }

    @PostMapping("/createCore")
    @Operation(summary = "/create-core", security = @SecurityRequirement(name = "bearerAuth"))
    public boolean createCore(@RequestBody SolrCreateCoreDTO solrCreateCoreDTO) throws SolrServerException, IOException, URISyntaxException, ParserConfigurationException, InterruptedException, TransformerException, org.xml.sax.SAXException {
        return createSolrCore.createCore(solrCreateCoreDTO.getCoreName());
    }

    @PutMapping("/renameCore")
    @Operation(summary = "/rename-core", security = @SecurityRequirement(name = "bearerAuth"))
    public boolean renameCore(@RequestBody SolrRenameCoreDTO solrRenameCoreDTO) throws SolrServerException, IOException, URISyntaxException, ParserConfigurationException, InterruptedException, TransformerException, org.xml.sax.SAXException {
        return renameSolrCore.renameCore(solrRenameCoreDTO.getCoreName(), solrRenameCoreDTO.getNewName());
    }

    @DeleteMapping("/deleteCore")
    @Operation(summary = "/delete-core", security = @SecurityRequirement(name = "bearerAuth"))
    public boolean deleteCore(@RequestBody SolrDeleteCoreDTO solrDeleteCoreDTO) throws SolrServerException, IOException, URISyntaxException, ParserConfigurationException, InterruptedException, TransformerException, org.xml.sax.SAXException {
        return deleteSolrCore.deleteCore(solrDeleteCoreDTO.getCoreName(),solrDeleteCoreDTO.isDeleteIndex(),solrDeleteCoreDTO.isDeleteDataDir(),solrDeleteCoreDTO.isDeleteInstanceDir());
    }

    @PutMapping("/swapCore")
    @Operation(summary = "/swap-cores", security = @SecurityRequirement(name = "bearerAuth"))
    public boolean swapCore(@RequestBody SolrSwapCoreDTO solrSwapCoreDTO) throws SolrServerException, IOException, URISyntaxException, ParserConfigurationException, InterruptedException, TransformerException, org.xml.sax.SAXException {
        return swapSolrCore.swapCore(solrSwapCoreDTO.getCoreOne(), solrSwapCoreDTO.getCoreTwo());
    }

    @PostMapping("/reloadCore")
    @Operation(summary = "/reload-core", security = @SecurityRequirement(name = "bearerAuth"))
    public boolean createCore(@RequestBody SolrReloadCoreDTO solrReloadCoreDTO) throws SolrServerException, IOException, URISyntaxException, ParserConfigurationException, InterruptedException, TransformerException, org.xml.sax.SAXException {
        return reloadSolrCore.reloadCore(solrReloadCoreDTO.getCoreName());
    }

}
