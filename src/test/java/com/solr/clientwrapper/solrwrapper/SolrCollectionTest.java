package com.solr.clientwrapper.solrwrapper;

import com.solr.clientwrapper.IntegrationTest;
import com.solr.clientwrapper.domain.dto.solr.SolrResponseDTO;
import com.solr.clientwrapper.domain.dto.solr.collection.SolrCreateCollectionDTO;
import com.solr.clientwrapper.domain.dto.solr.collection.SolrDeleteCollectionDTO;
import com.solr.clientwrapper.domain.dto.solr.collection.SolrGetCollectionsResponseDTO;
import com.solr.clientwrapper.domain.dto.solr.collection.SolrRenameCollectionDTO;
import com.solr.clientwrapper.domain.service.SolrCollectionService;
import com.solr.clientwrapper.solrcore.TestUtil;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class SolrCollectionTest {

    String solrCollectionEndpoint ="/searchservice/table";

    String collectionName ="automatedTestCollection";


    @Autowired
    private MockMvc restAMockMvc;

    @MockBean
    private SolrCollectionService solrCollectionService;


    public void setMockitoSuccessResponseForService() {
        SolrResponseDTO solrResponseDTO = new SolrResponseDTO(collectionName);
        solrResponseDTO.setStatusCode(200);
        solrResponseDTO.setMessage("Testing");

        SolrGetCollectionsResponseDTO solrGetCollectionsResponseDTO=new SolrGetCollectionsResponseDTO();
        solrGetCollectionsResponseDTO.setStatusCode(200);
        solrGetCollectionsResponseDTO.setMessage("Testing");

        Mockito.when(solrCollectionService.create(Mockito.any(),Mockito.any())).thenReturn(solrResponseDTO);
        Mockito.when(solrCollectionService.delete(Mockito.any())).thenReturn(solrResponseDTO);
        Mockito.when(solrCollectionService.rename(Mockito.any(),Mockito.any())).thenReturn(solrResponseDTO);
        Mockito.when(solrCollectionService.getCollections()).thenReturn(solrGetCollectionsResponseDTO);
        Mockito.when(solrCollectionService.isCollectionExists(Mockito.any())).thenReturn(true);

    }

    public void setMockitoBadResponseForService() {
        SolrResponseDTO solrResponseDTO = new SolrResponseDTO(collectionName);
        solrResponseDTO.setStatusCode(400);
        solrResponseDTO.setMessage("Testing");

        SolrGetCollectionsResponseDTO solrGetCollectionsResponseDTO=new SolrGetCollectionsResponseDTO();
        solrGetCollectionsResponseDTO.setStatusCode(400);
        solrGetCollectionsResponseDTO.setMessage("Testing");

        Mockito.when(solrCollectionService.create(Mockito.any(),Mockito.any())).thenReturn(solrResponseDTO);
        Mockito.when(solrCollectionService.delete(Mockito.any())).thenReturn(solrResponseDTO);
        Mockito.when(solrCollectionService.rename(Mockito.any(),Mockito.any())).thenReturn(solrResponseDTO);
        Mockito.when(solrCollectionService.getCollections()).thenReturn(solrGetCollectionsResponseDTO);
        Mockito.when(solrCollectionService.isCollectionExists(Mockito.any())).thenReturn(false);
    }

    @Test
    @Transactional
    void testCreateSolrCollection() throws Exception {

        SolrCreateCollectionDTO solrCreateCollectionDTO =new SolrCreateCollectionDTO(collectionName,"B");

        //CREATE COLLECTION
        setMockitoSuccessResponseForService();
        restAMockMvc.perform(MockMvcRequestBuilders.post(solrCollectionEndpoint +"/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(solrCreateCollectionDTO)))
                .andExpect(status().isOk());


        //CREATE COLLECTION WITH SAME NAME AND TEST
        setMockitoBadResponseForService();
        restAMockMvc.perform(MockMvcRequestBuilders.post(solrCollectionEndpoint +"/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(solrCreateCollectionDTO)))
                .andExpect(status().isBadRequest());

        //DELETE THE CREATED COLLECTION
        SolrDeleteCollectionDTO solrDeleteCollectionDTO=new SolrDeleteCollectionDTO();
        solrDeleteCollectionDTO.setCollectionName(collectionName);

        setMockitoSuccessResponseForService();
        restAMockMvc.perform(MockMvcRequestBuilders.delete(solrCollectionEndpoint +"/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(solrDeleteCollectionDTO)))
                .andExpect(status().isOk());
    }


    @Test
    @Transactional
    void testDeleteSolrCollection() throws Exception {

        //DELETE A NON EXISTING COLLECTION
        SolrDeleteCollectionDTO solrDeleteCollectionDTO=new SolrDeleteCollectionDTO();
        solrDeleteCollectionDTO.setCollectionName(collectionName);

        setMockitoBadResponseForService();
        restAMockMvc.perform(MockMvcRequestBuilders.delete(solrCollectionEndpoint +"/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(solrDeleteCollectionDTO)))
                .andExpect(status().isBadRequest());


        //CREATE COLLECTION
        setMockitoSuccessResponseForService();
        restAMockMvc.perform(MockMvcRequestBuilders.post(solrCollectionEndpoint +"/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(solrDeleteCollectionDTO)))
                .andExpect(status().isOk());


        //DELETE THE CREATED COLLECTION
        setMockitoSuccessResponseForService();
        restAMockMvc.perform(MockMvcRequestBuilders.delete(solrCollectionEndpoint +"/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(solrDeleteCollectionDTO)))
                .andExpect(status().isOk());
    }


    @Test
    @Transactional
    void testRenameSolrCollection() throws Exception {

        SolrCreateCollectionDTO solrCreateCollectionDTO=new SolrCreateCollectionDTO(collectionName,"B");

        //CREATE COLLECTION
        setMockitoSuccessResponseForService();
        restAMockMvc.perform(MockMvcRequestBuilders.post(solrCollectionEndpoint +"/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(solrCreateCollectionDTO)))
                .andExpect(status().isOk());

        //RENAME THE COLLECTION
        setMockitoSuccessResponseForService();
        SolrRenameCollectionDTO solrRenameCollectionDTO=new SolrRenameCollectionDTO(collectionName,collectionName+"2");
        restAMockMvc.perform(MockMvcRequestBuilders.put(solrCollectionEndpoint +"/rename")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(solrRenameCollectionDTO)))
                .andExpect(status().isOk());

        //TRY TO DELETE USING THE OLD COLLECTION NAME
        setMockitoBadResponseForService();
        SolrDeleteCollectionDTO solrDeleteCollectionDTO=new SolrDeleteCollectionDTO(collectionName);
        restAMockMvc.perform(MockMvcRequestBuilders.delete(solrCollectionEndpoint +"/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(solrDeleteCollectionDTO)))
                .andExpect(status().isBadRequest());

        //TRY TO DELETE USING THE NEW COLLECTION NAME
        setMockitoSuccessResponseForService();
        solrDeleteCollectionDTO =new SolrDeleteCollectionDTO(collectionName +"2");
        restAMockMvc.perform(MockMvcRequestBuilders.delete(solrCollectionEndpoint +"/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(solrDeleteCollectionDTO)))
                .andExpect(status().isOk());
    }


    @Test
    @Transactional
    void testGetSolrCollections() throws Exception {

        setMockitoSuccessResponseForService();
        restAMockMvc.perform(MockMvcRequestBuilders.get(solrCollectionEndpoint +"/collections")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        setMockitoBadResponseForService();
        restAMockMvc.perform(MockMvcRequestBuilders.get(solrCollectionEndpoint +"/collections")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

    }


    @Test
    @Transactional
    void testIsCollectionExists() throws Exception {

        setMockitoSuccessResponseForService();
        restAMockMvc.perform(MockMvcRequestBuilders.get(solrCollectionEndpoint +"/isCollectionExists/"+collectionName)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        setMockitoBadResponseForService();
        restAMockMvc.perform(MockMvcRequestBuilders.get(solrCollectionEndpoint +"/isCollectionExists/"+collectionName)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

    }


    @Test
    @Transactional
    void testCapacityPlans() throws Exception {

        restAMockMvc.perform(MockMvcRequestBuilders.get(solrCollectionEndpoint +"/capacity-plans")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }




}
