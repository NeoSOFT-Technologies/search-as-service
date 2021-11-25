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


    @Test
    @Transactional
    void testCreateSolrCore() throws Exception {

        SolrCreateCoreDTO solrCreateCoreDTO=new SolrCreateCoreDTO(coreName);

        //CREATE CORE
        restAMockMvc.perform(MockMvcRequestBuilders.post(solrEndpoint+"/createCore/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.convertObjectToJsonBytes(solrCreateCoreDTO)))
                .andExpect(status().isOk());

        //CREATE CORE WITH SAME NAME AND TEST
        restAMockMvc.perform(MockMvcRequestBuilders.post(solrEndpoint+"/createCore/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(solrCreateCoreDTO)))
                .andExpect(status().isBadRequest());

        //DELETE THE CREATED CORE
        SolrDeleteCoreDTO solrDeleteCoreDTO=new SolrDeleteCoreDTO(coreName,true,true,true);

        restAMockMvc.perform(MockMvcRequestBuilders.delete(solrEndpoint+"/deleteCore/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(solrDeleteCoreDTO)))
                .andExpect(status().isOk());
    }


    @Test
    @Transactional
    void testDeleteSolrCore() throws Exception {

        //DELETE A NON EXISTING CORE
        SolrDeleteCoreDTO solrDeleteCoreDTO=new SolrDeleteCoreDTO(coreName,true,true,true);
        restAMockMvc.perform(MockMvcRequestBuilders.delete(solrEndpoint+"/deleteCore/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(solrDeleteCoreDTO)))
                .andExpect(status().isBadRequest());


        //CREATE CORE
        SolrCreateCoreDTO solrCreateCoreDTO=new SolrCreateCoreDTO(coreName);
        restAMockMvc.perform(MockMvcRequestBuilders.post(solrEndpoint+"/createCore/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(solrCreateCoreDTO)))
                .andExpect(status().isOk());


        //DELETE THE CREATED CORE
        restAMockMvc.perform(MockMvcRequestBuilders.delete(solrEndpoint+"/deleteCore/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(solrDeleteCoreDTO)))
                .andExpect(status().isOk());
    }


    @Test
    @Transactional
    void testRenameSolrCore() throws Exception {

        SolrCreateCoreDTO solrCreateCoreDTO=new SolrCreateCoreDTO(coreName);

        //CREATE CORE
        restAMockMvc.perform(MockMvcRequestBuilders.post(solrEndpoint+"/createCore/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(solrCreateCoreDTO)))
                .andExpect(status().isOk());

        //RENAME THE CORE
        SolrRenameCoreDTO solrRenameCoreDTO=new SolrRenameCoreDTO(coreName,coreName+"2");
        restAMockMvc.perform(MockMvcRequestBuilders.put(solrEndpoint+"/renameCore/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(solrRenameCoreDTO)))
                .andExpect(status().isOk());

        //TRY TO DELETE USING THE OLD CORE NAME
        SolrDeleteCoreDTO solrDeleteCoreDTO=new SolrDeleteCoreDTO(coreName,true,true,true);
        restAMockMvc.perform(MockMvcRequestBuilders.delete(solrEndpoint+"/deleteCore/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(solrDeleteCoreDTO)))
                .andExpect(status().isBadRequest());

        //TRY TO DELETE USING THE NEW CORE NAME
        solrDeleteCoreDTO=new SolrDeleteCoreDTO(coreName+"2",true,true,true);
        restAMockMvc.perform(MockMvcRequestBuilders.delete(solrEndpoint+"/deleteCore/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(solrDeleteCoreDTO)))
                .andExpect(status().isOk());
    }




}
