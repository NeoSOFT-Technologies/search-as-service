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
import com.searchservice.app.domain.dto.Response;
import com.searchservice.app.domain.dto.logger.LoggersDTO;
import com.searchservice.app.domain.dto.table.GetCapacityPlan;
import com.searchservice.app.domain.dto.table.ManageTable;
import com.searchservice.app.domain.dto.table.SchemaField;
import com.searchservice.app.domain.dto.table.TableSchema;
import com.searchservice.app.domain.dto.table.TableSchemav2;
import com.searchservice.app.domain.service.ManageTableService;
import com.searchservice.app.domain.service.TableDeleteService;

@IntegrationTest
@AutoConfigureMockMvc
class ManageTableTest {

    @Value("${base-url.api-endpoint.home}")
	private String apiEndpoint;
    private String tableName ="automatedTestCollection";
    private int clientId = 101;

	String schemaName = "default-config";
	SchemaField solr = new SchemaField("testField6","string", "mydefault", true, true, false, true, true);
	//SchemaFieldDTO[] attributes = { solr };
	List<SchemaField> attributes = new ArrayList<>(Arrays.asList(solr));
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
    
    @MockBean
    private TableDeleteService tableDeleteService;
    
    public void setMockitoSuccessResponseForService() {
        Response responseDTO = new Response();

        responseDTO.setStatusCode(200);
        responseDTO.setMessage("Testing");

        Response responseDTOisCollectionExists = new Response();
        responseDTOisCollectionExists.setStatusCode(200);
        responseDTOisCollectionExists.setMessage("true");

        Response getTablesResponseDTO=new Response();
        getTablesResponseDTO.setStatusCode(200);
        getTablesResponseDTO.setMessage("Testing");
        
   
        GetCapacityPlan capacityPlanResponseDTO = new GetCapacityPlan();
        
        Response unodDeleteResponseDTO = new Response();
        unodDeleteResponseDTO.setStatusCode(200);
        unodDeleteResponseDTO.setMessage("Testing");

        Mockito.when(manageTableService.createTableIfNotPresent(Mockito.any(),Mockito.any())).thenReturn(responseDTO);
        Mockito.when(manageTableService.deleteTable(Mockito.any(),Mockito.any())).thenReturn(responseDTO);
        Mockito.when(manageTableService.updateTableSchema(Mockito.any(), Mockito.any(),Mockito.any())).thenReturn(responseDTO);
        //Mockito.when(tableService.rename(Mockito.any(),Mockito.any())).thenReturn(responseDTO);

        Mockito.when(manageTableService.getTables(Mockito.anyInt(),Mockito.any())).thenReturn(getTablesResponseDTO);
//        Mockito.when(manageTableService.getTableSchemaIfPresent(Mockito.any())).thenReturn(tableSchemaResponseDTO);
        Mockito.when(manageTableService.capacityPlans(Mockito.any())).thenReturn(capacityPlanResponseDTO);

        
        Map<Object, Object> finalResponseMap= new HashMap<>();
        finalResponseMap.put("Random message","Data is returned");
        Mockito.when(manageTableService.getTableDetails(Mockito.any(),Mockito.any())).thenReturn(finalResponseMap);
        Mockito.when(tableDeleteService.undoTableDeleteRecord(Mockito.anyInt(),Mockito.any())).thenReturn(unodDeleteResponseDTO);
        Mockito.when(tableDeleteService.initializeTableDelete(Mockito.anyInt(), Mockito.anyString(),Mockito.any())).thenReturn(responseDTO);
        Mockito.when(tableDeleteService.checkTableExistensce(Mockito.anyString())).thenReturn(true);
    }

    public void setMockitoBadResponseForService() {
    	Response responseDTO = new Response();
        responseDTO.setStatusCode(400);
        responseDTO.setMessage("Testing");

        Response responseDTOisCollectionExists = new Response();
        responseDTOisCollectionExists.setStatusCode(400);
        responseDTOisCollectionExists.setMessage("Error!");

        Response getTablesResponseDTO=new Response();
        getTablesResponseDTO.setStatusCode(400);
        getTablesResponseDTO.setMessage("Testing");
        
//        TableSchema tableSchemaExpectedResponse = new TableSchema(
//        		400, 
//        		"Retrieved table schema");
//        TableSchemav2 tableSchemaResponseDTO = new TableSchemav2(tableSchemaExpectedResponse);
        
        GetCapacityPlan capacityPlanResponseDTO = new GetCapacityPlan();
        
        Response unodDeleteResponseDTO = new Response();
        unodDeleteResponseDTO.setStatusCode(400);
        unodDeleteResponseDTO.setMessage("Error!");

        Mockito.when(manageTableService.createTableIfNotPresent(Mockito.any(),Mockito.any())).thenReturn(responseDTO);
        Mockito.when(manageTableService.deleteTable(Mockito.any(),Mockito.any())).thenReturn(responseDTO);
        Mockito.when(manageTableService.updateTableSchema(Mockito.any(),Mockito.any(), Mockito.any())).thenReturn(responseDTO);
        //Mockito.when(tableService.rename(Mockito.any(),Mockito.any())).thenReturn(responseDTO);
        Mockito.when(manageTableService.getTables(Mockito.anyInt(),Mockito.any())).thenReturn(getTablesResponseDTO);
//        Mockito.when(manageTableService.getTableSchemaIfPresent(Mockito.any())).thenReturn(tableSchemaResponseDTO);
        Mockito.when(manageTableService.capacityPlans(Mockito.any())).thenReturn(capacityPlanResponseDTO);
        
        Map<Object, Object> finalResponseMap= new HashMap<>();
        finalResponseMap.put("Error","Error connecting to cluster.");
        Mockito.when(manageTableService.getTableDetails(Mockito.any(),Mockito.any())).thenReturn(finalResponseMap);
        Mockito.when(tableDeleteService.undoTableDeleteRecord(Mockito.anyInt(),Mockito.any())).thenReturn(unodDeleteResponseDTO);
        Mockito.when(tableDeleteService.initializeTableDelete(Mockito.anyInt(), Mockito.anyString(),Mockito.any())).thenReturn(responseDTO);
        Mockito.when(tableDeleteService.checkTableExistensce(Mockito.anyString())).thenReturn(true);
    }

    @Test
    void testCreateTable() throws Exception {

        ManageTable createTableDTO =new ManageTable(
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
        Response deleteTableDTO=new Response();

        setMockitoSuccessResponseForService();
        restAMockMvc.perform(MockMvcRequestBuilders.delete(apiEndpoint + "/manage/table" +"/"+ clientId +"/"+ tableName)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(deleteTableDTO)))
                .andExpect(status().isOk());
    }


    @Test
    void testDeleteTable() throws Exception {

        //DELETE A NON EXISTING COLLECTION
        Response deleteTableResponseDTO=new Response();

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
		TableSchema schemaDTO = new TableSchema(tableName, schemaName, attributes);
		restAMockMvc.perform(MockMvcRequestBuilders.put(apiEndpoint + "/manage/table" +"/"+ clientId +"/"+ tableName)
				.contentType(MediaType.APPLICATION_PROBLEM_JSON)
				.content(TestUtil.convertObjectToJsonBytes(schemaDTO)))
		.andExpect(status().isOk());
		
		// Update Schema for non-existing table
		setMockitoBadResponseForService();
		schemaDTO = new TableSchema(tableName, schemaName, attributes);
		restAMockMvc.perform(MockMvcRequestBuilders.put(apiEndpoint + "/manage/table" +"/"+ clientId +"/"+ tableName)
				.contentType(MediaType.APPLICATION_PROBLEM_JSON)
				.content(TestUtil.convertObjectToJsonBytes(schemaDTO)))
		.andExpect(status().isBadRequest());

	}


    @Test
    void testGetTables() throws Exception {

        setMockitoSuccessResponseForService();
        restAMockMvc.perform(MockMvcRequestBuilders.get(apiEndpoint + "/manage/table/"+clientId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }


    @Test
    void testGetCapacityPlans() throws Exception {

        restAMockMvc.perform(MockMvcRequestBuilders.get(apiEndpoint + "/manage/table" +"/capacity-plans")
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());

    }
    
    
//	@Test
	void testGetTableInfo() throws Exception {
		setMockitoSuccessResponseForService();;
		restAMockMvc.perform(
				MockMvcRequestBuilders
				.get(apiEndpoint + "/manage/table" + "/"+ clientId +"/"+ tableName)
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk());
		
		setMockitoBadResponseForService();
		restAMockMvc.perform(
				MockMvcRequestBuilders
				.get(apiEndpoint + "/manage/table" + "/"+ clientId +"/"+ tableName)
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isBadRequest());
	}

}
