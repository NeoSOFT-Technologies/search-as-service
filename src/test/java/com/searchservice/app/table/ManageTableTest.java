package com.searchservice.app.table;


import com.searchservice.app.IntegrationTest;
import com.searchservice.app.TestUtil;
import com.searchservice.app.domain.dto.ApiResponseDTO;
import com.searchservice.app.domain.dto.GetListItemsResponseDTO;
import com.searchservice.app.domain.dto.ResponseDTO;
import com.searchservice.app.domain.dto.table.CreateTableDTO;
import com.searchservice.app.domain.dto.table.DeleteTableDTO;
import com.searchservice.app.domain.dto.table.TableSchemaResponseDTO;
import com.searchservice.app.domain.service.ManageTableService;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@IntegrationTest
@AutoConfigureMockMvc
class ManageTableTest {

    String solrCollectionEndpoint ="/api/table";

    String tableName ="automatedTestCollection";


    @Autowired
    private MockMvc restAMockMvc;

    @MockBean
    private ManageTableService manageTableService;


    public void setMockitoSuccessResponseForService() {
        ApiResponseDTO responseDTO = new ApiResponseDTO();
        responseDTO.setResponseStatusCode(200);
        responseDTO.setResponseMessage("Testing");

        ApiResponseDTO responseDTOisCollectionExists = new ApiResponseDTO();
        responseDTOisCollectionExists.setResponseStatusCode(200);
        responseDTOisCollectionExists.setResponseMessage("true");

        GetListItemsResponseDTO getTablesResponseDTO=new GetListItemsResponseDTO();
        getTablesResponseDTO.setStatusCode(200);
        getTablesResponseDTO.setMessage("Testing");
        
        TableSchemaResponseDTO tableSchemaResponseDTO = new TableSchemaResponseDTO(
        		200, 
        		"Schema couldn't be fetched. Error!", 
        		"", 
        		"", 
        		null); 

        Mockito.when(manageTableService.createTableIfNotPresent(Mockito.any())).thenReturn(responseDTO);
        Mockito.when(manageTableService.deleteTable(Mockito.any())).thenReturn(responseDTO);
        //Mockito.when(tableService.rename(Mockito.any(),Mockito.any())).thenReturn(responseDTO);
        Mockito.when(manageTableService.getTables()).thenReturn(getTablesResponseDTO);
        Mockito.when(manageTableService.getTableSchemaIfPresent(Mockito.any())).thenReturn(tableSchemaResponseDTO);
    }

    public void setMockitoBadResponseForService() {
        ApiResponseDTO responseDTO = new ApiResponseDTO();
        responseDTO.setResponseStatusCode(400);
        responseDTO.setResponseMessage("Testing");

        ApiResponseDTO responseDTOisCollectionExists = new ApiResponseDTO();
        responseDTOisCollectionExists.setResponseStatusCode(400);
        responseDTOisCollectionExists.setResponseMessage("Error!");

        GetListItemsResponseDTO getTablesResponseDTO=new GetListItemsResponseDTO();
        getTablesResponseDTO.setStatusCode(400);
        getTablesResponseDTO.setMessage("Testing");
        
        TableSchemaResponseDTO tableSchemaResponseDTO = new TableSchemaResponseDTO(
        		400, 
        		"Retrieved table schema", 
        		"", 
        		"", 
        		null); 

        Mockito.when(manageTableService.createTableIfNotPresent(Mockito.any())).thenReturn(responseDTO);
        Mockito.when(manageTableService.deleteTable(Mockito.any())).thenReturn(responseDTO);
        //Mockito.when(tableService.rename(Mockito.any(),Mockito.any())).thenReturn(responseDTO);
        Mockito.when(manageTableService.getTables()).thenReturn(getTablesResponseDTO);
        Mockito.when(manageTableService.getTableSchemaIfPresent(Mockito.any())).thenReturn(tableSchemaResponseDTO);
    }

    @Test
    void testCreateTable() throws Exception {

        CreateTableDTO createTableDTO =new CreateTableDTO(tableName,"B");

        //CREATE COLLECTION
        setMockitoSuccessResponseForService();
        restAMockMvc.perform(MockMvcRequestBuilders.post(solrCollectionEndpoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(createTableDTO)))
                .andExpect(status().isOk());


        //CREATE COLLECTION WITH SAME NAME AND TEST
        setMockitoBadResponseForService();
        restAMockMvc.perform(MockMvcRequestBuilders.post(solrCollectionEndpoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(createTableDTO)))
                .andExpect(status().isBadRequest());

        //DELETE THE CREATED COLLECTION
        DeleteTableDTO deleteTableDTO=new DeleteTableDTO();
        deleteTableDTO.setTableName(tableName);

        setMockitoSuccessResponseForService();
        restAMockMvc.perform(MockMvcRequestBuilders.delete(solrCollectionEndpoint +"/"+ tableName)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(deleteTableDTO)))
                .andExpect(status().isOk());
    }


    @Test
    void testDeleteTable() throws Exception {

        //DELETE A NON EXISTING COLLECTION
        DeleteTableDTO deleteTableDTO=new DeleteTableDTO();
        deleteTableDTO.setTableName(tableName);

        setMockitoBadResponseForService();
        restAMockMvc.perform(MockMvcRequestBuilders.delete(solrCollectionEndpoint +"/"+ tableName)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(deleteTableDTO)))
                .andExpect(status().isBadRequest());


        //CREATE COLLECTION
        setMockitoSuccessResponseForService();
        restAMockMvc.perform(MockMvcRequestBuilders.post(solrCollectionEndpoint )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(deleteTableDTO)))
                .andExpect(status().isOk());


        //DELETE THE CREATED COLLECTION
        setMockitoSuccessResponseForService();
        restAMockMvc.perform(MockMvcRequestBuilders.delete(solrCollectionEndpoint +"/"+ tableName)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(deleteTableDTO)))
                .andExpect(status().isOk());
    }


//    @Test
//    void testRenameTable() throws Exception {
//
//        CreateTableDTO createTableDTO=new CreateTableDTO(tableName,"B");
//
//        //CREATE COLLECTION
//        setMockitoSuccessResponseForService();
//        restAMockMvc.perform(MockMvcRequestBuilders.post(solrCollectionEndpoint)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(TestUtil.convertObjectToJsonBytes(createTableDTO)))
//                .andExpect(status().isOk());
//
//        //RENAME THE COLLECTION
//        setMockitoSuccessResponseForService();
//        SolrRenameCollectionDTO solrRenameCollectionDTO=new SolrRenameCollectionDTO(tableName, tableName +"2");
//        restAMockMvc.perform(MockMvcRequestBuilders.put(solrCollectionEndpoint +"/rename")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(TestUtil.convertObjectToJsonBytes(solrRenameCollectionDTO)))
//                .andExpect(status().isOk());
//
//        //TRY TO DELETE USING THE OLD COLLECTION NAME
//        setMockitoBadResponseForService();
//        DeleteTableDTO deleteTableDTO=new DeleteTableDTO(tableName);
//        restAMockMvc.perform(MockMvcRequestBuilders.delete(solrCollectionEndpoint +"/"+ tableName)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(TestUtil.convertObjectToJsonBytes(deleteTableDTO)))
//                .andExpect(status().isBadRequest());
//
//        //TRY TO DELETE USING THE NEW COLLECTION NAME
//        setMockitoSuccessResponseForService();
//        deleteTableDTO =new DeleteTableDTO(tableName +"2");
//        restAMockMvc.perform(MockMvcRequestBuilders.delete(solrCollectionEndpoint +"/"+ tableName)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(TestUtil.convertObjectToJsonBytes(deleteTableDTO)))
//                .andExpect(status().isOk());
//    }


    @Test
    void testGetTables() throws Exception {

        setMockitoSuccessResponseForService();
        restAMockMvc.perform(MockMvcRequestBuilders.get(solrCollectionEndpoint )
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        setMockitoBadResponseForService();
        restAMockMvc.perform(MockMvcRequestBuilders.get(solrCollectionEndpoint)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

    }


    @Test
    void testIsTableExists() throws Exception {

        setMockitoSuccessResponseForService();
        restAMockMvc.perform(MockMvcRequestBuilders.get(solrCollectionEndpoint +"/isTableExists/"+ tableName)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

//        setMockitoBadResponseForService();
//        restAMockMvc.perform(MockMvcRequestBuilders.get(solrCollectionEndpoint +"/isCollectionExists/"+collectionName)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isBadRequest());

    }


    @Test
    void testGetCapacityPlans() throws Exception {

        restAMockMvc.perform(MockMvcRequestBuilders.get(solrCollectionEndpoint +"/capacity-plans")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }


    @Test
    void testGetTableDetails() throws Exception {

        setMockitoSuccessResponseForService();
        restAMockMvc.perform(MockMvcRequestBuilders.get(solrCollectionEndpoint+"/details/testTable" )
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        setMockitoBadResponseForService();
        restAMockMvc.perform(MockMvcRequestBuilders.get(solrCollectionEndpoint+"/details/testTable")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

    }


}
