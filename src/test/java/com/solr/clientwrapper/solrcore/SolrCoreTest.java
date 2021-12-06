package com.solr.clientwrapper.solrcore;

import com.solr.clientwrapper.IntegrationTest;
import com.solr.clientwrapper.domain.dto.solr.SolrResponseDTO;
import com.solr.clientwrapper.domain.dto.solr.core.SolrDoubleCoreDTO;
import com.solr.clientwrapper.domain.dto.solr.core.SolrSingleCoreDTO;
import com.solr.clientwrapper.domain.service.SolrCoreService;
import com.solr.clientwrapper.rest.TestUtil;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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
@SpringBootTest(properties = { "base-solr-url=http://localhost:8983/solr" })
public class SolrCoreTest {

    String solrCoreEndpoint ="/solr-core";

    String coreName="automatedTestCore";

    @Autowired
    private MockMvc restAMockMvc;


    @MockBean
    private SolrCoreService solrCoreService;


    public void setMockitoSuccessResponseForService() {
        SolrResponseDTO solrResponseDTO = new SolrResponseDTO(coreName);
        solrResponseDTO.setStatusCode(200);
        solrResponseDTO.setMessage("Testing");

        Mockito.when(solrCoreService.create(Mockito.any())).thenReturn(solrResponseDTO);
        Mockito.when(solrCoreService.delete(Mockito.any())).thenReturn(solrResponseDTO);
        Mockito.when(solrCoreService.rename(Mockito.any(),Mockito.any())).thenReturn(solrResponseDTO);
        Mockito.when(solrCoreService.swap(Mockito.any(),Mockito.any())).thenReturn(solrResponseDTO);
        Mockito.when(solrCoreService.reload(Mockito.any())).thenReturn(solrResponseDTO);
        Mockito.when(solrCoreService.status(Mockito.any())).thenReturn("{message={this is a sample response greater than 150 characters},responseHeader={status=0,QTime=23},initFailures={},status={karthik1={name=karthik1,instanceDir=C:\\solr-8.10.1\\solr-8.10.1\\server\\solr\\karthik1,dataDir=C:\\solr-8.10.1\\solr-8.10.1\\server\\solr\\karthik1\\data\\,config=solrconfig.xml,schema=managed-schema,startTime=Mon Dec 06 17:30:12 IST 2021,uptime=177894,index={numDocs=0,maxDoc=0,deletedDocs=0,indexHeapUsageBytes=0,version=2,segmentCount=0,current=true,hasDeletions=false,directory=org.apache.lucene.store.NRTCachingDirectory:NRTCachingDirectory(MMapDirectory@C:\\solr-8.10.1\\solr-8.10.1\\server\\solr\\karthik1\\data\\index lockFactory=org.apache.lucene.store.NativeFSLockFactory@4bc5dac8; maxCacheMB=48.0 maxMergeSizeMB=4.0),segmentsFile=segments_1,segmentsFileSizeInBytes=69,userData={},sizeInBytes=69,size=69 bytes}}}}");
    }

    public void setMockitoBadResponseForService() {
        SolrResponseDTO solrResponseDTO = new SolrResponseDTO(coreName);
        solrResponseDTO.setStatusCode(400);
        solrResponseDTO.setMessage("Testing");

        Mockito.when(solrCoreService.create(Mockito.any())).thenReturn(solrResponseDTO);
        Mockito.when(solrCoreService.delete(Mockito.any())).thenReturn(solrResponseDTO);
        Mockito.when(solrCoreService.rename(Mockito.any(),Mockito.any())).thenReturn(solrResponseDTO);
    }

    @Test
    @Transactional
    void testCreateSolrCore() throws Exception {

        SolrSingleCoreDTO solrSingleCoreDTO =new SolrSingleCoreDTO(coreName);

        //CREATE CORE
        setMockitoSuccessResponseForService();
        restAMockMvc.perform(MockMvcRequestBuilders.post(solrCoreEndpoint +"/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.convertObjectToJsonBytes(solrSingleCoreDTO)))
                .andExpect(status().isOk());


        //CREATE CORE WITH SAME NAME AND TEST
        setMockitoBadResponseForService();
        restAMockMvc.perform(MockMvcRequestBuilders.post(solrCoreEndpoint +"/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(solrSingleCoreDTO)))
                .andExpect(status().isBadRequest());

        //DELETE THE CREATED CORE
        solrSingleCoreDTO=new SolrSingleCoreDTO(coreName);

        setMockitoSuccessResponseForService();
        restAMockMvc.perform(MockMvcRequestBuilders.delete(solrCoreEndpoint +"/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(solrSingleCoreDTO)))
                .andExpect(status().isOk());
    }


    @Test
    @Transactional
    void testDeleteSolrCore() throws Exception {

        //DELETE A NON EXISTING CORE
        SolrSingleCoreDTO solrSingleCoreDTO=new SolrSingleCoreDTO(coreName);
        setMockitoBadResponseForService();
        restAMockMvc.perform(MockMvcRequestBuilders.delete(solrCoreEndpoint +"/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(solrSingleCoreDTO)))
                .andExpect(status().isBadRequest());


        //CREATE CORE
        setMockitoSuccessResponseForService();
        restAMockMvc.perform(MockMvcRequestBuilders.post(solrCoreEndpoint +"/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(solrSingleCoreDTO)))
                .andExpect(status().isOk());


        //DELETE THE CREATED CORE
        setMockitoSuccessResponseForService();
        restAMockMvc.perform(MockMvcRequestBuilders.delete(solrCoreEndpoint +"/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(solrSingleCoreDTO)))
                .andExpect(status().isOk());
    }


    @Test
    @Transactional
    void testRenameSolrCore() throws Exception {

        SolrSingleCoreDTO solrSingleCoreDTO =new SolrSingleCoreDTO(coreName);

        //CREATE CORE
        setMockitoSuccessResponseForService();
        restAMockMvc.perform(MockMvcRequestBuilders.post(solrCoreEndpoint +"/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(solrSingleCoreDTO)))
                .andExpect(status().isOk());

        //RENAME THE CORE
        setMockitoSuccessResponseForService();
        SolrDoubleCoreDTO solrDoubleCoreDTO=new SolrDoubleCoreDTO(coreName,coreName+"2");
        restAMockMvc.perform(MockMvcRequestBuilders.put(solrCoreEndpoint +"/rename")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(solrDoubleCoreDTO)))
                .andExpect(status().isOk());

        //TRY TO DELETE USING THE OLD CORE NAME
        setMockitoBadResponseForService();
        restAMockMvc.perform(MockMvcRequestBuilders.delete(solrCoreEndpoint +"/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(solrSingleCoreDTO)))
                .andExpect(status().isBadRequest());

        //TRY TO DELETE USING THE NEW CORE NAME
        setMockitoSuccessResponseForService();
        solrSingleCoreDTO =new SolrSingleCoreDTO(coreName+"2");
        restAMockMvc.perform(MockMvcRequestBuilders.delete(solrCoreEndpoint +"/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(solrSingleCoreDTO)))
                .andExpect(status().isOk());
    }

    @Test
    @Transactional
    void testSwapSolrCore() throws Exception {

        //CREATE CORE 1
        SolrSingleCoreDTO solrSingleCoreDTO=new SolrSingleCoreDTO(coreName);
        setMockitoSuccessResponseForService();
        restAMockMvc.perform(MockMvcRequestBuilders.post(solrCoreEndpoint +"/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(solrSingleCoreDTO)))
                .andExpect(status().isOk());

        //TRY SWAPPING EXISTING CORE WITH NON-EXISTING CORE
        SolrDoubleCoreDTO solrDoubleCoreDTO=new SolrDoubleCoreDTO(coreName,coreName+"2");
        setMockitoBadResponseForService();
        restAMockMvc.perform(MockMvcRequestBuilders.put(solrCoreEndpoint +"/swap")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(solrDoubleCoreDTO)))
                .andExpect(status().isBadRequest());

        //CREATE CORE 2
        solrSingleCoreDTO=new SolrSingleCoreDTO(coreName+"2");
        setMockitoSuccessResponseForService();
        restAMockMvc.perform(MockMvcRequestBuilders.post(solrCoreEndpoint +"/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(solrSingleCoreDTO)))
                .andExpect(status().isOk());

        //SWAP THE 2 CORES
        solrDoubleCoreDTO=new SolrDoubleCoreDTO(coreName,coreName+"2");
        setMockitoSuccessResponseForService();
        restAMockMvc.perform(MockMvcRequestBuilders.put(solrCoreEndpoint +"/swap")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(solrDoubleCoreDTO)))
                .andExpect(status().isOk());

        //DELETE THE CREATED CORE 1
        solrSingleCoreDTO=new SolrSingleCoreDTO(coreName);
        setMockitoSuccessResponseForService();
        restAMockMvc.perform(MockMvcRequestBuilders.delete(solrCoreEndpoint +"/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(solrSingleCoreDTO)))
                .andExpect(status().isOk());

        //DELETE THE CREATED CORE 2
        solrSingleCoreDTO=new SolrSingleCoreDTO(coreName+"2");
        setMockitoSuccessResponseForService();
        restAMockMvc.perform(MockMvcRequestBuilders.delete(solrCoreEndpoint +"/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(solrSingleCoreDTO)))
                .andExpect(status().isOk());
    }


    @Test
    @Transactional
    void testReloadSolrCore() throws Exception {

        //RELOAD NON EXISTING CORE
        SolrSingleCoreDTO solrSingleCoreDTO=new SolrSingleCoreDTO(coreName);
        setMockitoBadResponseForService();
        restAMockMvc.perform(MockMvcRequestBuilders.post(solrCoreEndpoint +"/reload")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(solrSingleCoreDTO)))
                .andExpect(status().isBadRequest());

        //CREATE CORE
        solrSingleCoreDTO=new SolrSingleCoreDTO(coreName);
        setMockitoSuccessResponseForService();
        restAMockMvc.perform(MockMvcRequestBuilders.post(solrCoreEndpoint +"/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(solrSingleCoreDTO)))
                .andExpect(status().isOk());

        //RELOAD THE CREATED CORE
        solrSingleCoreDTO=new SolrSingleCoreDTO(coreName);
        setMockitoSuccessResponseForService();
        restAMockMvc.perform(MockMvcRequestBuilders.post(solrCoreEndpoint +"/reload")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(solrSingleCoreDTO)))
                .andExpect(status().isOk());

        //DELETE THE CREATED CORE
        solrSingleCoreDTO=new SolrSingleCoreDTO(coreName);
        setMockitoSuccessResponseForService();
        restAMockMvc.perform(MockMvcRequestBuilders.delete(solrCoreEndpoint +"/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(solrSingleCoreDTO)))
                .andExpect(status().isOk());
    }


    @Test
    @Transactional
    void testSolrCoreStatus() throws Exception {

        //GET STATUS OF NON EXISTING CORE
        setMockitoBadResponseForService();
        restAMockMvc.perform(MockMvcRequestBuilders.get(solrCoreEndpoint +"/status/"+"nonExistingCore")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        //CREATE CORE
        SolrSingleCoreDTO solrSingleCoreDTO=new SolrSingleCoreDTO(coreName);
        setMockitoSuccessResponseForService();
        restAMockMvc.perform(MockMvcRequestBuilders.post(solrCoreEndpoint +"/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(solrSingleCoreDTO)))
                .andExpect(status().isOk());

        //GET STATUS OF CREATED CORE
        setMockitoSuccessResponseForService();
        restAMockMvc.perform(MockMvcRequestBuilders.get(solrCoreEndpoint +"/status/"+coreName)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        //DELETE THE CREATED CORE
        solrSingleCoreDTO=new SolrSingleCoreDTO(coreName);
        setMockitoSuccessResponseForService();
        restAMockMvc.perform(MockMvcRequestBuilders.delete(solrCoreEndpoint +"/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(solrSingleCoreDTO)))
                .andExpect(status().isOk());
    }


}
