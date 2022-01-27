package com.searchservice.app.dataingestion;


import com.searchservice.app.IntegrationTest;
import com.searchservice.app.domain.service.DataIngectionService;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

@IntegrationTest
@AutoConfigureMockMvc
class DataIngectionResourceTest {

	@Autowired
	DataIngectionService dataIngectionService;

	String data = "\r\n" + "{\r\n" + "\"books\" :[\r\n" + "  {\r\n" + "    \"id\" : 1,\r\n"
			+ "    \"color\" : \"The Lightning Thief\",\r\n" + "    \"author\" : \"Rick Riordan\",\r\n"
			+ "    \"price\" : 123\r\n" + "  }\r\n" + "]\r\n" + "}";

	String batchdata = "{\r\n" + "\"batch\":\r\n" + "[\r\n" + "  {\r\n" + "        \"books\" :[\r\n" + "  {\r\n"
			+ "    \"id\" : 1,\r\n" + "    \"color\" : \"The Lightning Thief\",\r\n"
			+ "    \"author\" : \"Rick Riordan\",\r\n" + "    \"price\" : 123\r\n" + "  },\r\n" + "  {\r\n"
			+ "    \"id\" : 2,\r\n" + "    \"color\" : \"The Lightning Thief\",\r\n"
			+ "    \"author\" : \"Rick Riordan\",\r\n" + "    \"price\" : 123\r\n" + "  }\r\n" + "],\r\n"
			+ "\"movie\" :[\r\n" + "  {\r\n" + "    \"id\" : 1,\r\n" + "    \"movie\" : \"The Lightning Thief\",\r\n"
			+ "    \"actor\" : \"Rick Riordan\",\r\n" + "    \"price\" : 123\r\n" + "  },\r\n" + "  {\r\n"
			+ "    \"id\" : 2,\r\n" + "     \"movie\" : \"The Thief\",\r\n" + "    \"actor\" : \"Rick\",\r\n"
			+ "    \"price\" : 623\r\n" + "  }\r\n" + "],\r\n" + "\"hero\" :[\r\n" + "  {\r\n" + "    \"id\" : 1,\r\n"
			+ "    \"movie\" : \"The Lightning Thief\",\r\n" + "    \"actor\" : \"Rick Riordan\",\r\n"
			+ "    \"price\" : 123\r\n" + "  },\r\n" + "  {\r\n" + "    \"id\" : 2,\r\n"
			+ "     \"movie\" : \"The Thief\",\r\n" + "    \"actor\" : \"Rick\",\r\n" + "    \"price\" : 623\r\n"
			+ "  }\r\n" + "]\r\n" + "  }\r\n" + "]\r\n" + "}";

	@Test
	void parseSolrSchemaArray() throws JSONException {
		System.out.println("data" + data);
		final String data1 = dataIngectionService.parseSolrSchemaArray("docparse", data);
		System.out.println("data1" + data1);
		String expected = "[{\"color\":\"The Lightning Thief\",\"author\":\"Rick Riordan\",\"price\":123,\"id\":1}]";
		JSONAssert.assertEquals(expected, data1, true);
	}

	@Test
	void parseSolrSchemaArrayfalse() throws JSONException {
		System.out.println("data2" + data);
		final String data1 = dataIngectionService.parseSolrSchemaArray("docparse", data);
		System.out.println("data3" + data1);
		String expected = "{\"color\":\"The Lightning Thief\",\"author\":\"Rick Riordan\",\"price\":123,\"id\":1}";
		JSONAssert.assertNotEquals(expected, data1, true);
	}

	@Test
	void parseSolrSchemaBatch() throws JSONException {

		System.out.println("data11" + data);
		final String data11 = dataIngectionService.parseSolrSchemaBatch("docparse", batchdata);
		System.out.println("data11" + data11);
		String expected = "[[{\"color\":\"The Lightning Thief\",\"author\":\"Rick Riordan\",\"price\":123,\"id\":1},{\"color\":\"The Lightning Thief\",\"author\":\"Rick Riordan\",\"price\":123,\"id\":2}],[{\"actor\":\"Rick Riordan\",\"movie\":\"The Lightning Thief\",\"price\":123,\"id\":1},{\"actor\":\"Rick\",\"movie\":\"The Thief\",\"price\":623,\"id\":2}],[{\"actor\":\"Rick Riordan\",\"movie\":\"The Lightning Thief\",\"price\":123,\"id\":1},{\"actor\":\"Rick\",\"movie\":\"The Thief\",\"price\":623,\"id\":2}]]";
		JSONAssert.assertEquals(expected, data11, true);
	}

	@Test
	void parseSolrSchemaBatchfalse() throws JSONException {
		System.out.println("data2" + batchdata);
		final String data1 = dataIngectionService.parseSolrSchemaBatch("docparse", batchdata);
		System.out.println("data3" + data1);
		String expected = "[{\"color\":\"The Lightning Thief\",\"author\":\"Rick Riordan\",\"price\":123,\"id\":1},{\"color\":\"The Lightning Thief\",\"author\":\"Rick Riordan\",\"price\":123,\"id\":2}],[{\"actor\":\"Rick Riordan\",\"movie\":\"The Lightning Thief\",\"price\":123,\"id\":1},{\"actor\":\"Rick\",\"movie\":\"The Thief\",\"price\":623,\"id\":2}],[{\"actor\":\"Rick Riordan\",\"movie\":\"The Lightning Thief\",\"price\":123,\"id\":1},{\"actor\":\"Rick\",\"movie\":\"The Thief\",\"price\":623,\"id\":2}]";
		JSONAssert.assertNotEquals(expected, data1, true);
	}
}