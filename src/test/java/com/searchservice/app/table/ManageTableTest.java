package com.searchservice.app.table;


import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.searchservice.app.IntegrationTest;
import com.searchservice.app.TestUtil;
import com.searchservice.app.domain.dto.ResponseDTO;
import com.searchservice.app.domain.dto.table.GetCapacityPlanDTO;
import com.searchservice.app.domain.dto.table.ManageTableDTO;
import com.searchservice.app.domain.dto.table.SchemaFieldDTO;
import com.searchservice.app.domain.dto.table.TableSchemaDTO;
import com.searchservice.app.domain.service.ManageTableService;

@IntegrationTest
@AutoConfigureMockMvc
class ManageTableTest {

    @Value("${base-url.api-endpoint.home}")
	private String apiEndpoint;
    private String tableName ="automatedTestCollection";
    private int clientId = 101;

	String schemaName = "default-config";
	SchemaFieldDTO solr = new SchemaFieldDTO("testField6","string", "mydefault", true, true, false, true, true);
	//SchemaFieldDTO[] attributes = { solr };
	List<SchemaFieldDTO> attributes = new ArrayList<>(Arrays.asList(solr));
	String expectedGetResponse = "{\n"
			  +"\"tableName\": \"gettingstarted3\",\n"
			  +"\"name\": \"default-config\",\n"
			  +"\"attributes\": [{\n"
		      +"\"name\": \"testField6\",\n"
		      +"\"type\": \"_nest_path_\",\n"
		      +"\"default_\": \"mydefault\",\n"
		      +"\"storable\": false,\n"
		      +"\"filterable\": true,"
		      +"\"required\": true,"
		      +"\"sortable\": true,\n"
		      +"\"multiValue\": true,\n"
		      +"}],\n"
		      +"\"statusCode\": 200\n"
		      +"}";
	String expectedCreateResponse400 = "{\n"
			  +"\"tableName\": \"gettingstarted3\",\n"
			  +"\"name\": \"default-config\",\n"
			  +"\"attributes\": [{\n"
		      +"\"name\": \"testField6\",\n"
		      +"\"type\": \"_nest_path_\",\n"
		      +"\"default_\": \"mydefault\",\n"
		      +"\"storable\": false,\n"
		      +"\"filterable\": true,"
		      +"\"required\": true,"
		      +"\"sortable\": true,\n"
		      +"\"multiValue\": true,\n"
		      +"}],\n"
		      +"\"statusCode\": 400\n"
		      +"}";

    @Autowired
    private MockMvc restAMockMvc;

    @MockBean
    private ManageTableService manageTableService;


    public void setMockitoSuccessResponseForService() {
        ResponseDTO responseDTO = new ResponseDTO();
        responseDTO.setResponseStatusCode(200);
        responseDTO.setResponseMessage("Testing");

        ResponseDTO responseDTOisCollectionExists = new ResponseDTO();
        responseDTOisCollectionExists.setResponseStatusCode(200);
        responseDTOisCollectionExists.setResponseMessage("true");
//        
//        TableSchemaDTO tableSchemaDTO = new TableSchemaDTO(
//        		tableName, schemaName, attributes);

        ResponseDTO getTablesResponseDTO=new ResponseDTO();
        getTablesResponseDTO.setResponseStatusCode(200);
        getTablesResponseDTO.setResponseMessage("Testing");
        
        TableSchemaDTO tableSchemaResponseDTO = new TableSchemaDTO(
        		200, 
        		"Schema couldn't be fetched. Error!", 
        		"", 
        		"", 
        		null);
        
        GetCapacityPlanDTO capacityPlanResponseDTO = new GetCapacityPlanDTO();

        Mockito.when(manageTableService.createTableIfNotPresent(Mockito.any())).thenReturn(responseDTO);
        Mockito.when(manageTableService.deleteTable(Mockito.any())).thenReturn(responseDTO);
        Mockito.when(manageTableService.updateTableSchema(Mockito.any(), Mockito.any())).thenReturn(responseDTO);
        //Mockito.when(tableService.rename(Mockito.any(),Mockito.any())).thenReturn(responseDTO);
        Mockito.when(manageTableService.getTables()).thenReturn(getTablesResponseDTO);
        Mockito.when(manageTableService.getTableSchemaIfPresent(Mockito.any())).thenReturn(tableSchemaResponseDTO);
        Mockito.when(manageTableService.capacityPlans()).thenReturn(capacityPlanResponseDTO);
        
        Map finalResponseMap= new HashMap();
        finalResponseMap.put("Random message","Data is returned");
        Mockito.when(manageTableService.getTableDetails(Mockito.any())).thenReturn(finalResponseMap);
    }

    public void setMockitoBadResponseForService() {
    	ResponseDTO responseDTO = new ResponseDTO();
        responseDTO.setResponseStatusCode(400);
        responseDTO.setResponseMessage("Testing");

        ResponseDTO responseDTOisCollectionExists = new ResponseDTO();
        responseDTOisCollectionExists.setResponseStatusCode(400);
        responseDTOisCollectionExists.setResponseMessage("Error!");

        ResponseDTO getTablesResponseDTO=new ResponseDTO();
        getTablesResponseDTO.setResponseStatusCode(400);
        getTablesResponseDTO.setResponseMessage("Testing");
        
        TableSchemaDTO tableSchemaResponseDTO = new TableSchemaDTO(
        		400, 
        		"Retrieved table schema");
        
        GetCapacityPlanDTO capacityPlanResponseDTO = new GetCapacityPlanDTO();

        Mockito.when(manageTableService.createTableIfNotPresent(Mockito.any())).thenReturn(responseDTO);
        Mockito.when(manageTableService.deleteTable(Mockito.any())).thenReturn(responseDTO);
        Mockito.when(manageTableService.updateTableSchema(Mockito.any(), Mockito.any())).thenReturn(responseDTO);
        //Mockito.when(tableService.rename(Mockito.any(),Mockito.any())).thenReturn(responseDTO);
        Mockito.when(manageTableService.getTables()).thenReturn(getTablesResponseDTO);
        Mockito.when(manageTableService.getTableSchemaIfPresent(Mockito.any())).thenReturn(tableSchemaResponseDTO);
        Mockito.when(manageTableService.capacityPlans()).thenReturn(capacityPlanResponseDTO);
        
        Map finalResponseMap= new HashMap();
        finalResponseMap.put("Error","Error connecting to cluster.");
        Mockito.when(manageTableService.getTableDetails(Mockito.any())).thenReturn(finalResponseMap);
    }

    @Test
    void testCreateTable() throws Exception {

        ManageTableDTO createTableDTO =new ManageTableDTO(
        		tableName, 
        		"B", 
        		"default-schema", 
        		attributes);

        //CREATE COLLECTION
        setMockitoSuccessResponseForService();
        restAMockMvc.perform(MockMvcRequestBuilders.post(apiEndpoint + "/manage/table" +"/"+ clientId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(createTableDTO)))
                .andExpect(status().isOk());


        //CREATE COLLECTION WITH SAME NAME AND TEST
        setMockitoBadResponseForService();
        restAMockMvc.perform(MockMvcRequestBuilders.post(apiEndpoint + "/manage/table" +"/"+ clientId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(createTableDTO)))
                .andExpect(status().isBadRequest());

        //DELETE THE CREATED COLLECTION
        ResponseDTO deleteTableDTO=new ResponseDTO();

        setMockitoSuccessResponseForService();
        restAMockMvc.perform(MockMvcRequestBuilders.delete(apiEndpoint + "/manage/table" +"/"+ clientId +"/"+ tableName)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(deleteTableDTO)))
                .andExpect(status().isOk());
    }


    @Test
    void testDeleteTable() throws Exception {

        //DELETE A NON EXISTING COLLECTION
        ResponseDTO deleteTableResponseDTO=new ResponseDTO();

        setMockitoBadResponseForService();
        restAMockMvc.perform(MockMvcRequestBuilders.delete(apiEndpoint + "/manage/table" +"/"+ clientId +"/"+ tableName)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(deleteTableResponseDTO)))
                .andExpect(status().isBadRequest());


        //CREATE COLLECTION
        setMockitoSuccessResponseForService();
        restAMockMvc.perform(MockMvcRequestBuilders.post(apiEndpoint + "/manage/table" +"/"+ clientId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(deleteTableResponseDTO)))
                .andExpect(status().isOk());


        //DELETE THE CREATED COLLECTION
        setMockitoSuccessResponseForService();
        restAMockMvc.perform(MockMvcRequestBuilders.delete(apiEndpoint + "/manage/table" +"/"+ clientId +"/"+ tableName)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(deleteTableResponseDTO)))
                .andExpect(status().isOk());
    }
    
    
	@Test
	void testUpdateTableSchema() throws Exception {

		// Update Schema
		setMockitoSuccessResponseForService();
		TableSchemaDTO schemaDTO = new TableSchemaDTO(tableName, schemaName, attributes);
		restAMockMvc.perform(MockMvcRequestBuilders.put(apiEndpoint + "/manage/table" +"/"+ clientId +"/"+ tableName)
				.contentType(MediaType.APPLICATION_PROBLEM_JSON)
				.content(TestUtil.convertObjectToJsonBytes(schemaDTO)))
		.andExpect(status().isOk());
		
		// Update Schema for non-existing table
		setMockitoBadResponseForService();
		schemaDTO = new TableSchemaDTO(tableName, schemaName, attributes);
		restAMockMvc.perform(MockMvcRequestBuilders.put(apiEndpoint + "/manage/table" +"/"+ clientId +"/"+ tableName)
				.contentType(MediaType.APPLICATION_PROBLEM_JSON)
				.content(TestUtil.convertObjectToJsonBytes(schemaDTO)))
		.andExpect(status().isBadRequest());

	}


    @Test
    void testGetTables() throws Exception {

        setMockitoSuccessResponseForService();
        restAMockMvc.perform(MockMvcRequestBuilders.get(apiEndpoint + "/manage/table" )
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        setMockitoBadResponseForService();
        restAMockMvc.perform(MockMvcRequestBuilders.get(apiEndpoint + "/manage/table")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

    }


    @Test
    void testGetCapacityPlans() throws Exception {

        restAMockMvc.perform(MockMvcRequestBuilders.get(apiEndpoint + "/manage/table" +"/capacity-plans")
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());

    }


    @Test
    void testGetTableDetails() throws Exception {

        setMockitoSuccessResponseForService();
        restAMockMvc.perform(MockMvcRequestBuilders.get(apiEndpoint + "/manage/table" +"/details/testTable" )
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        setMockitoBadResponseForService();
        restAMockMvc.perform(MockMvcRequestBuilders.get(apiEndpoint + "/manage/table"+"/details/testTable")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

    }
    
    
	@Test
	void testGetSchema() throws Exception {
		setMockitoSuccessResponseForService();;
		restAMockMvc.perform(
				MockMvcRequestBuilders
				.get(apiEndpoint + "/manage/table" + "/schema" +"/"+ clientId +"/"+ tableName)
				.accept(MediaType.APPLICATION_JSON))
		//.andExpect(content().json(expectedGetResponse))
		.andExpect(status().isOk());
		
		setMockitoBadResponseForService();
		restAMockMvc.perform(
				MockMvcRequestBuilders
				.get(apiEndpoint + "/manage/table" + "/schema" +"/"+ clientId +"/"+ tableName)
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isBadRequest());
	}

}
