package com.searchservice.app.core;


import com.searchservice.app.IntegrationTest;
import com.searchservice.app.TestUtil;
import com.searchservice.app.domain.dto.ResponseDTO;
import com.searchservice.app.domain.dto.core.DoubleCoreDTO;
import com.searchservice.app.domain.dto.core.SingleCoreDTO;
import com.searchservice.app.domain.service.CoreService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@IntegrationTest
@AutoConfigureMockMvc
public class CoreTest {

//    String solrCoreEndpoint ="/solr-core";
//
//    String coreName="automatedTestCore";
//
//    @Autowired
//    private MockMvc restAMockMvc;
//
//
//    @MockBean
//    private CoreService coreService;
//
//
//    public void setMockitoSuccessResponseForService() {
//        ResponseDTO responseDTO = new ResponseDTO(coreName);
//        responseDTO.setStatusCode(200);
//        responseDTO.setMessage("Testing");
//
//        Mockito.when(coreService.create(Mockito.any())).thenReturn(responseDTO);
//        Mockito.when(coreService.delete(Mockito.any())).thenReturn(responseDTO);
//        Mockito.when(coreService.rename(Mockito.any(),Mockito.any())).thenReturn(responseDTO);
//        Mockito.when(coreService.swap(Mockito.any(),Mockito.any())).thenReturn(responseDTO);
//        Mockito.when(coreService.reload(Mockito.any())).thenReturn(responseDTO);
//        Mockito.when(coreService.status(Mockito.any())).thenReturn("{message={this is a sample response greater than 150 characters},responseHeader={status=0,QTime=23},initFailures={},status={karthik1={name=karthik1,instanceDir=C:\\solr-8.10.1\\solr-8.10.1\\server\\solr\\karthik1,dataDir=C:\\solr-8.10.1\\solr-8.10.1\\server\\solr\\karthik1\\data\\,config=solrconfig.xml,schema=managed-schema,startTime=Mon Dec 06 17:30:12 IST 2021,uptime=177894,index={numDocs=0,maxDoc=0,deletedDocs=0,indexHeapUsageBytes=0,version=2,segmentCount=0,current=true,hasDeletions=false,directory=org.apache.lucene.store.NRTCachingDirectory:NRTCachingDirectory(MMapDirectory@C:\\solr-8.10.1\\solr-8.10.1\\server\\solr\\karthik1\\data\\index lockFactory=org.apache.lucene.store.NativeFSLockFactory@4bc5dac8; maxCacheMB=48.0 maxMergeSizeMB=4.0),segmentsFile=segments_1,segmentsFileSizeInBytes=69,userData={},sizeInBytes=69,size=69 bytes}}}}");
//    }
//
//    public void setMockitoBadResponseForService() {
//        ResponseDTO responseDTO = new ResponseDTO(coreName);
//        responseDTO.setStatusCode(400);
//        responseDTO.setMessage("Testing");
//
//        Mockito.when(coreService.create(Mockito.any())).thenReturn(responseDTO);
//        Mockito.when(coreService.delete(Mockito.any())).thenReturn(responseDTO);
//        Mockito.when(coreService.rename(Mockito.any(),Mockito.any())).thenReturn(responseDTO);
//        Mockito.when(coreService.swap(Mockito.any(),Mockito.any())).thenReturn(responseDTO);
//        Mockito.when(coreService.reload(Mockito.any())).thenReturn(responseDTO);
//        Mockito.when(coreService.status(Mockito.any())).thenReturn("{message={this is a sample response lesser than 150 characters}");
//    }
//
//    @Test
//    void testCreateSolrCore() throws Exception {
//
//        SingleCoreDTO singleCoreDTO =new SingleCoreDTO(coreName);
//
//        //CREATE CORE
//        setMockitoSuccessResponseForService();
//        restAMockMvc.perform(MockMvcRequestBuilders.post(solrCoreEndpoint +"/create")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(TestUtil.convertObjectToJsonBytes(singleCoreDTO)))
//                .andExpect(status().isOk());
//
//
//        //CREATE CORE WITH SAME NAME AND TEST
//        setMockitoBadResponseForService();
//        restAMockMvc.perform(MockMvcRequestBuilders.post(solrCoreEndpoint +"/create")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(TestUtil.convertObjectToJsonBytes(singleCoreDTO)))
//                .andExpect(status().isBadRequest());
//
//        //DELETE THE CREATED CORE
//        singleCoreDTO=new SingleCoreDTO(coreName);
//
//        setMockitoSuccessResponseForService();
//        restAMockMvc.perform(MockMvcRequestBuilders.delete(solrCoreEndpoint +"/delete/"+coreName)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk());
//    }
//
//
//    @Test
//    void testDeleteSolrCore() throws Exception {
//
//        //DELETE A NON EXISTING CORE
//        SingleCoreDTO singleCoreDTO=new SingleCoreDTO(coreName);
//        setMockitoBadResponseForService();
//        restAMockMvc.perform(MockMvcRequestBuilders.delete(solrCoreEndpoint +"/delete/"+coreName)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isBadRequest());
//
//        //CREATE CORE
//        setMockitoSuccessResponseForService();
//        restAMockMvc.perform(MockMvcRequestBuilders.post(solrCoreEndpoint +"/create")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(TestUtil.convertObjectToJsonBytes(singleCoreDTO)))
//                .andExpect(status().isOk());
//
//
//        //DELETE THE CREATED CORE
//        setMockitoSuccessResponseForService();
//        restAMockMvc.perform(MockMvcRequestBuilders.delete(solrCoreEndpoint +"/delete/"+coreName)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk());
//    }
//
//
//    @Test
//    void testRenameSolrCore() throws Exception {
//
//        SingleCoreDTO singleCoreDTO =new SingleCoreDTO(coreName);
//
//        //CREATE CORE
//        setMockitoSuccessResponseForService();
//        restAMockMvc.perform(MockMvcRequestBuilders.post(solrCoreEndpoint +"/create")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(TestUtil.convertObjectToJsonBytes(singleCoreDTO)))
//                .andExpect(status().isOk());
//
//        //RENAME THE CORE
//        setMockitoSuccessResponseForService();
//        DoubleCoreDTO doubleCoreDTO=new DoubleCoreDTO(coreName,coreName+"2");
//        restAMockMvc.perform(MockMvcRequestBuilders.put(solrCoreEndpoint +"/rename")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(TestUtil.convertObjectToJsonBytes(doubleCoreDTO)))
//                .andExpect(status().isOk());
//
//        //TRY TO DELETE USING THE OLD CORE NAME
//        setMockitoBadResponseForService();
//        restAMockMvc.perform(MockMvcRequestBuilders.delete(solrCoreEndpoint +"/delete/"+coreName)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isBadRequest());
//
//        //TRY TO DELETE USING THE NEW CORE NAME
//        setMockitoSuccessResponseForService();
//        singleCoreDTO =new SingleCoreDTO(coreName+"2");
//        restAMockMvc.perform(MockMvcRequestBuilders.delete(solrCoreEndpoint +"/delete/"+coreName)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    void testSwapSolrCore() throws Exception {
//
//        //CREATE CORE 1
//        SingleCoreDTO singleCoreDTO=new SingleCoreDTO(coreName);
//        setMockitoSuccessResponseForService();
//        restAMockMvc.perform(MockMvcRequestBuilders.post(solrCoreEndpoint +"/create")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(TestUtil.convertObjectToJsonBytes(singleCoreDTO)))
//                .andExpect(status().isOk());
//
//        //TRY SWAPPING EXISTING CORE WITH NON-EXISTING CORE
//        DoubleCoreDTO doubleCoreDTO=new DoubleCoreDTO(coreName,coreName+"2");
//        setMockitoBadResponseForService();
//        restAMockMvc.perform(MockMvcRequestBuilders.put(solrCoreEndpoint +"/swap")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(TestUtil.convertObjectToJsonBytes(doubleCoreDTO)))
//                .andExpect(status().isBadRequest());
//
//        //CREATE CORE 2
//        singleCoreDTO=new SingleCoreDTO(coreName+"2");
//        setMockitoSuccessResponseForService();
//        restAMockMvc.perform(MockMvcRequestBuilders.post(solrCoreEndpoint +"/create")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(TestUtil.convertObjectToJsonBytes(singleCoreDTO)))
//                .andExpect(status().isOk());
//
//        //SWAP THE 2 CORES
//        doubleCoreDTO=new DoubleCoreDTO(coreName,coreName+"2");
//        setMockitoSuccessResponseForService();
//        restAMockMvc.perform(MockMvcRequestBuilders.put(solrCoreEndpoint +"/swap")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(TestUtil.convertObjectToJsonBytes(doubleCoreDTO)))
//                .andExpect(status().isOk());
//
//        //DELETE THE CREATED CORE 1
//        singleCoreDTO=new SingleCoreDTO(coreName);
//        setMockitoSuccessResponseForService();
//        restAMockMvc.perform(MockMvcRequestBuilders.delete(solrCoreEndpoint +"/delete/"+coreName)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk());
//
//        //DELETE THE CREATED CORE 2
//        singleCoreDTO=new SingleCoreDTO(coreName+"2");
//        setMockitoSuccessResponseForService();
//        restAMockMvc.perform(MockMvcRequestBuilders.delete(solrCoreEndpoint +"/delete/"+coreName)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk());
//    }
//
//
//    @Test
//    void testReloadSolrCore() throws Exception {
//
//        //RELOAD NON EXISTING CORE
//        SingleCoreDTO singleCoreDTO=new SingleCoreDTO(coreName);
//        setMockitoBadResponseForService();
//        restAMockMvc.perform(MockMvcRequestBuilders.post(solrCoreEndpoint +"/reload")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(TestUtil.convertObjectToJsonBytes(singleCoreDTO)))
//                .andExpect(status().isBadRequest());
//
//        //CREATE CORE
//        singleCoreDTO=new SingleCoreDTO(coreName);
//        setMockitoSuccessResponseForService();
//        restAMockMvc.perform(MockMvcRequestBuilders.post(solrCoreEndpoint +"/create")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(TestUtil.convertObjectToJsonBytes(singleCoreDTO)))
//                .andExpect(status().isOk());
//
//        //RELOAD THE CREATED CORE
//        singleCoreDTO=new SingleCoreDTO(coreName);
//        setMockitoSuccessResponseForService();
//        restAMockMvc.perform(MockMvcRequestBuilders.post(solrCoreEndpoint +"/reload")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(TestUtil.convertObjectToJsonBytes(singleCoreDTO)))
//                .andExpect(status().isOk());
//
//        //DELETE THE CREATED CORE
//        singleCoreDTO=new SingleCoreDTO(coreName);
//        setMockitoSuccessResponseForService();
//        restAMockMvc.perform(MockMvcRequestBuilders.delete(solrCoreEndpoint +"/delete/"+coreName)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk());
//    }
//
//
//    @Test
//    void testSolrCoreStatus() throws Exception {
//
//        //GET STATUS OF NON EXISTING CORE
//        setMockitoBadResponseForService();
//        restAMockMvc.perform(MockMvcRequestBuilders.get(solrCoreEndpoint +"/status/"+"nonExistingCore")
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isBadRequest());
//
//        //CREATE CORE
//        SingleCoreDTO singleCoreDTO=new SingleCoreDTO(coreName);
//        setMockitoSuccessResponseForService();
//        restAMockMvc.perform(MockMvcRequestBuilders.post(solrCoreEndpoint +"/create")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(TestUtil.convertObjectToJsonBytes(singleCoreDTO)))
//                .andExpect(status().isOk());
//
//        //GET STATUS OF CREATED CORE
//        setMockitoSuccessResponseForService();
//        restAMockMvc.perform(MockMvcRequestBuilders.get(solrCoreEndpoint +"/status/"+coreName)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk());
//
//        //DELETE THE CREATED CORE
//        singleCoreDTO=new SingleCoreDTO(coreName);
//        setMockitoSuccessResponseForService();
//        restAMockMvc.perform(MockMvcRequestBuilders.delete(solrCoreEndpoint +"/delete/"+coreName)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk());
//    }
//

}
