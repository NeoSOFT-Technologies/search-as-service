package com.solr.clientwrapper.solrcore;

import com.solr.clientwrapper.IntegrationTest;
import com.solr.clientwrapper.domain.dto.solr.*;
import com.solr.clientwrapper.rest.TestUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
public class SolrCoreTest {

    String solrEndpoint="/solr";

    String coreName="automatedTestCore";

    @Autowired
    private MockMvc restAMockMvc;


   // @Test
    @Transactional
    void testCreateSolrCore() throws Exception {

        SolrSingleCoreDTO solrSingleCoreDTO =new SolrSingleCoreDTO(coreName);

        //CREATE CORE
        restAMockMvc.perform(MockMvcRequestBuilders.post(solrEndpoint+"/create/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.convertObjectToJsonBytes(solrSingleCoreDTO)))
                .andExpect(status().isOk());

        //CREATE CORE WITH SAME NAME AND TEST
        restAMockMvc.perform(MockMvcRequestBuilders.post(solrEndpoint+"/create/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(solrSingleCoreDTO)))
                .andExpect(status().isBadRequest());

        //DELETE THE CREATED CORE
        solrSingleCoreDTO=new SolrSingleCoreDTO(coreName);

        restAMockMvc.perform(MockMvcRequestBuilders.delete(solrEndpoint+"/delete/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(solrSingleCoreDTO)))
                .andExpect(status().isOk());
    }


   // @Test
    @Transactional
    void testDeleteSolrCore() throws Exception {

        //DELETE A NON EXISTING CORE
        SolrSingleCoreDTO solrSingleCoreDTO=new SolrSingleCoreDTO(coreName);
        restAMockMvc.perform(MockMvcRequestBuilders.delete(solrEndpoint+"/delete/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(solrSingleCoreDTO)))
                .andExpect(status().isBadRequest());


        //CREATE CORE
        restAMockMvc.perform(MockMvcRequestBuilders.post(solrEndpoint+"/create/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(solrSingleCoreDTO)))
                .andExpect(status().isOk());


        //DELETE THE CREATED CORE
        restAMockMvc.perform(MockMvcRequestBuilders.delete(solrEndpoint+"/delete/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(solrSingleCoreDTO)))
                .andExpect(status().isOk());
    }


   // @Test
    @Transactional
    void testRenameSolrCore() throws Exception {

        SolrSingleCoreDTO solrSingleCoreDTO =new SolrSingleCoreDTO(coreName);

        //CREATE CORE
        restAMockMvc.perform(MockMvcRequestBuilders.post(solrEndpoint+"/create/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(solrSingleCoreDTO)))
                .andExpect(status().isOk());

        //RENAME THE CORE
        SolrDoubleCoreDTO solrDoubleCoreDTO=new SolrDoubleCoreDTO(coreName,coreName+"2");
        restAMockMvc.perform(MockMvcRequestBuilders.put(solrEndpoint+"/rename/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(solrDoubleCoreDTO)))
                .andExpect(status().isOk());

        //TRY TO DELETE USING THE OLD CORE NAME
        restAMockMvc.perform(MockMvcRequestBuilders.delete(solrEndpoint+"/delete/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(solrSingleCoreDTO)))
                .andExpect(status().isBadRequest());

        //TRY TO DELETE USING THE NEW CORE NAME
        solrSingleCoreDTO =new SolrSingleCoreDTO(coreName+"2");
        restAMockMvc.perform(MockMvcRequestBuilders.delete(solrEndpoint+"/delete/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(solrSingleCoreDTO)))
                .andExpect(status().isOk());
    }

   // @Test
    @Transactional
    void testSwapSolrCore() throws Exception {

        //CREATE CORE 1
        SolrSingleCoreDTO solrSingleCoreDTO=new SolrSingleCoreDTO(coreName);
        restAMockMvc.perform(MockMvcRequestBuilders.post(solrEndpoint+"/create/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(solrSingleCoreDTO)))
                .andExpect(status().isOk());

        //TRY SWAPPING EXISTING CORE WITH NON-EXISTING CORE
        SolrDoubleCoreDTO solrDoubleCoreDTO=new SolrDoubleCoreDTO(coreName,coreName+"2");
        restAMockMvc.perform(MockMvcRequestBuilders.put(solrEndpoint+"/swap/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(solrDoubleCoreDTO)))
                .andExpect(status().isBadRequest());

        //CREATE CORE 2
        solrSingleCoreDTO=new SolrSingleCoreDTO(coreName+"2");
        restAMockMvc.perform(MockMvcRequestBuilders.post(solrEndpoint+"/create/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(solrSingleCoreDTO)))
                .andExpect(status().isOk());

        //SWAP THE 2 CORES
        solrDoubleCoreDTO=new SolrDoubleCoreDTO(coreName,coreName+"2");
        restAMockMvc.perform(MockMvcRequestBuilders.put(solrEndpoint+"/swap/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(solrDoubleCoreDTO)))
                .andExpect(status().isOk());

        //DELETE THE CREATED CORE 1
        solrSingleCoreDTO=new SolrSingleCoreDTO(coreName);
        restAMockMvc.perform(MockMvcRequestBuilders.delete(solrEndpoint+"/delete/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(solrSingleCoreDTO)))
                .andExpect(status().isOk());

        //DELETE THE CREATED CORE 2
        solrSingleCoreDTO=new SolrSingleCoreDTO(coreName+"2");
        restAMockMvc.perform(MockMvcRequestBuilders.delete(solrEndpoint+"/delete/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(solrSingleCoreDTO)))
                .andExpect(status().isOk());
    }


   // @Test
    @Transactional
    void testReloadSolrCore() throws Exception {

        //RELOAD NON EXISTING CORE
        SolrSingleCoreDTO solrSingleCoreDTO=new SolrSingleCoreDTO(coreName);
        restAMockMvc.perform(MockMvcRequestBuilders.post(solrEndpoint+"/reload/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(solrSingleCoreDTO)))
                .andExpect(status().isBadRequest());

        //CREATE CORE
        solrSingleCoreDTO=new SolrSingleCoreDTO(coreName);
        restAMockMvc.perform(MockMvcRequestBuilders.post(solrEndpoint+"/create/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(solrSingleCoreDTO)))
                .andExpect(status().isOk());

        //RELOAD THE CREATED CORE
        solrSingleCoreDTO=new SolrSingleCoreDTO(coreName);
        restAMockMvc.perform(MockMvcRequestBuilders.post(solrEndpoint+"/reload/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(solrSingleCoreDTO)))
                .andExpect(status().isOk());

        //DELETE THE CREATED CORE
        solrSingleCoreDTO=new SolrSingleCoreDTO(coreName);
        restAMockMvc.perform(MockMvcRequestBuilders.delete(solrEndpoint+"/delete/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(solrSingleCoreDTO)))
                .andExpect(status().isOk());
    }


   // @Test
    @Transactional
    void testSolrCoreStatus() throws Exception {

        //GET STATUS OF NON EXISTING CORE
        restAMockMvc.perform(MockMvcRequestBuilders.get(solrEndpoint+"/status/"+"nonExistingCore")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        //CREATE CORE
        SolrSingleCoreDTO solrSingleCoreDTO=new SolrSingleCoreDTO(coreName);
        restAMockMvc.perform(MockMvcRequestBuilders.post(solrEndpoint+"/create/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(solrSingleCoreDTO)))
                .andExpect(status().isOk());

        //GET STATUS OF CREATED CORE
        restAMockMvc.perform(MockMvcRequestBuilders.get(solrEndpoint+"/status/"+coreName)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        //DELETE THE CREATED CORE
        solrSingleCoreDTO=new SolrSingleCoreDTO(coreName);
        restAMockMvc.perform(MockMvcRequestBuilders.delete(solrEndpoint+"/delete/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(solrSingleCoreDTO)))
                .andExpect(status().isOk());
    }


}
