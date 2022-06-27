package com.searchservice.app.domain.utils.documentparser;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import com.searchservice.app.IntegrationTest;
import com.searchservice.app.domain.utils.DocumentParserUtil;

@IntegrationTest
@AutoConfigureMockMvc(addFilters = false)
class DocumentParserUtilTest {

	Map<String, Map<String, Object>> getSchemaKeyValuePair() {
		Map<String, Map<String, Object>> schemaKeyValuePair = new HashMap<>();

		Map<String, Object> id = new HashMap<>() {
			{
				put("name", "id");
				put("type", "string");
				put("multiValued", "false");
				put("uninvertible", "true");
				put("indexed", "true");
				put("stored", "true");
			}
		};
		schemaKeyValuePair.put("id", id);

		Map<String, Object> custom_field_string = new HashMap<>() {
			{
				put("name", "custom_field_string");
				put("type", "string");
				put("uninvertible", "true");
				put("indexed", "true");
				put("stored", "true");
			}
		};
		schemaKeyValuePair.put("custom_field_string", custom_field_string);

		Map<String, Object> custom_field_strings = new HashMap<>() {
			{
				put("name", "custom_field_strings");
				put("type", "strings");
				put("uninvertible", "true");
				put("multiValued", "true");
				put("indexed", "true");
				put("stored", "true");
			}
		};
		schemaKeyValuePair.put("custom_field_strings", custom_field_strings);

		Map<String, Object> custom_field_boolean = new HashMap<>() {
			{
				put("name", "custom_field_boolean");
				put("type", "boolean");
				put("uninvertible", "true");
				put("indexed", "true");
				put("stored", "true");
			}
		};
		schemaKeyValuePair.put("custom_field_boolean", custom_field_boolean);

		Map<String, Object> custom_field_booleans = new HashMap<>() {
			{
				put("name", "custom_field_booleans");
				put("type", "boolean");
				put("uninvertible", "true");
				put("multiValued", "true");
				put("indexed", "true");
				put("stored", "true");
			}
		};
		schemaKeyValuePair.put("custom_field_booleans", custom_field_booleans);

		Map<String, Object> custom_field_plong = new HashMap<>() {
			{
				put("name", "custom_field_plong");
				put("type", "plong");
				put("uninvertible", "true");
				put("indexed", "true");
				put("stored", "true");
			}
		};
		schemaKeyValuePair.put("custom_field_plong", custom_field_plong);

		Map<String, Object> custom_field_plongs = new HashMap<>() {
			{
				put("name", "custom_field_plongs");
				put("type", "plong");
				put("uninvertible", "true");
				put("multiValued", "true");
				put("indexed", "true");
				put("stored", "true");
			}
		};
		schemaKeyValuePair.put("custom_field_plongs", custom_field_plongs);

		Map<String, Object> custom_required_field_boolean = new HashMap<>() {
			{
				put("name", "custom_required_field_boolean");
				put("type", "boolean");
				put("uninvertible", "true");
				put("required", "true");
				put("indexed", "true");
				put("stored", "true");
			}
		};
		schemaKeyValuePair.put("custom_required_field_boolean", custom_required_field_boolean);

		return schemaKeyValuePair;
	}

	@Test
	void correctInputJSONObject() throws Exception {

		String inputString = "{\n" + "\"id\":1," + "\"custom_field_boolean\":\"1\","
				+ "\"custom_field_booleans\":[1,0,\"1\"]," + "\"custom_field_plong\":60,"
				+ "\"custom_field_plongs\":[22,56,59]," + "\"custom_field_string\":karthik,"
				+ "\"custom_field_strings\":[\"kar\",\"mani\",\"sara\"],"
				+ "\"custom_required_field_boolean\":\"true\"," + "}";
		JSONObject payloadJSON = new JSONObject(inputString);

		Map<String, Map<String, Object>> schemaKeyValuePair = getSchemaKeyValuePair();
		DocumentParserUtil.DocumentSatisfiesSchemaResponse documentSatisfiesSchemaResponse = DocumentParserUtil
				.isDocumentSatisfySchema(schemaKeyValuePair, payloadJSON);

		assertThat(documentSatisfiesSchemaResponse.isObjectSatisfiesSchema()).isTrue();
	}

	@Test
	void incorrectBooleanInputJSONObject() throws Exception {

		String inputString = "{\n" + "\"id\":1," + "\"custom_field_boolean\":\"not-a-bool\","
				+ "\"custom_field_booleans\":[1,0,\"1\"]," + "\"custom_field_plong\":60,"
				+ "\"custom_field_plongs\":[22,56,59]," + "\"custom_field_string\":karthik,"
				+ "\"custom_field_strings\":[\"kar\",\"mani\",\"sara\"],"
				+ "\"custom_required_field_boolean\":\"true\"," + "}";

		JSONObject payloadJSON = new JSONObject(inputString);

		Map<String, Map<String, Object>> schemaKeyValuePair = getSchemaKeyValuePair();
		DocumentParserUtil.DocumentSatisfiesSchemaResponse documentSatisfiesSchemaResponse = DocumentParserUtil
				.isDocumentSatisfySchema(schemaKeyValuePair, payloadJSON);

		assertThat(documentSatisfiesSchemaResponse.isObjectSatisfiesSchema()).isFalse();
	}

	@Test
	void incorrectBooleanArrayInputJSONObject() throws Exception {

		String inputString = "{\n" + "\"id\":1," + "\"custom_field_boolean\":\"true\","
				+ "\"custom_field_booleans\":[\"incorrect-bool\",\"false\",\"1\"]," + "\"custom_field_plong\":60,"
				+ "\"custom_field_plongs\":[22,56,59]," + "\"custom_field_string\":karthik,"
				+ "\"custom_field_strings\":[\"kar\",\"mani\",\"sara\"],"
				+ "\"custom_required_field_boolean\":\"true\"," + "}";
		JSONObject payloadJSON = new JSONObject(inputString);

		Map<String, Map<String, Object>> schemaKeyValuePair = getSchemaKeyValuePair();
		DocumentParserUtil.DocumentSatisfiesSchemaResponse documentSatisfiesSchemaResponse = DocumentParserUtil
				.isDocumentSatisfySchema(schemaKeyValuePair, payloadJSON);

		assertThat(documentSatisfiesSchemaResponse.isObjectSatisfiesSchema()).isFalse();
	}

	@Test
	void incorrectPlongInputJSONObject() throws Exception {

		String inputString = "{\n" + "\"id\":1," + "\"custom_field_boolean\":\"1\","
				+ "\"custom_field_booleans\":[1,0,\"1\"]," + "\"custom_field_plong\":abc60,"
				+ "\"custom_field_plongs\":[22,56,59]," + "\"custom_field_string\":karthik,"
				+ "\"custom_field_strings\":[\"kar\",\"mani\",\"sara\"],"
				+ "\"custom_required_field_boolean\":\"true\"," + "}";
		JSONObject payloadJSON = new JSONObject(inputString);

		Map<String, Map<String, Object>> schemaKeyValuePair = getSchemaKeyValuePair();
		DocumentParserUtil.DocumentSatisfiesSchemaResponse documentSatisfiesSchemaResponse = DocumentParserUtil
				.isDocumentSatisfySchema(schemaKeyValuePair, payloadJSON);

		assertThat(documentSatisfiesSchemaResponse.isObjectSatisfiesSchema()).isFalse();
	}

	@Test
	void incorrectPlongArrayInputJSONObject() throws Exception {

		String inputString = "{\n" + "\"id\":1," + "\"custom_field_boolean\":\"1\","
				+ "\"custom_field_booleans\":[1,0,\"1\"]," + "\"custom_field_plong\":60,"
				+ "\"custom_field_plongs\":[22,56,\"abc\"]," + "\"custom_field_string\":karthik,"
				+ "\"custom_field_strings\":[\"kar\",\"mani\",\"sara\"],"
				+ "\"custom_required_field_boolean\":\"true\"," + "}";
		JSONObject payloadJSON = new JSONObject(inputString);

		Map<String, Map<String, Object>> schemaKeyValuePair = getSchemaKeyValuePair();
		DocumentParserUtil.DocumentSatisfiesSchemaResponse documentSatisfiesSchemaResponse = DocumentParserUtil
				.isDocumentSatisfySchema(schemaKeyValuePair, payloadJSON);

		assertThat(documentSatisfiesSchemaResponse.isObjectSatisfiesSchema()).isFalse();
	}

	@Test
	void incorrectStringInputJSONObject() throws Exception {

		String inputString = "{\n" + "\"id\":1," + "\"custom_field_boolean\":\"1\","
				+ "\"custom_field_booleans\":[1,0,\"1\"]," + "\"custom_field_plong\":60,"
				+ "\"custom_field_plongs\":[22,56,59]," + "\"custom_field_string\":[\"karthik\"],"
				+ "\"custom_field_strings\":[\"kar\",\"mani\",\"sara\"],"
				+ "\"custom_required_field_boolean\":\"true\"," + "}";
		JSONObject payloadJSON = new JSONObject(inputString);

		Map<String, Map<String, Object>> schemaKeyValuePair = getSchemaKeyValuePair();
		DocumentParserUtil.DocumentSatisfiesSchemaResponse documentSatisfiesSchemaResponse = DocumentParserUtil
				.isDocumentSatisfySchema(schemaKeyValuePair, payloadJSON);

		assertThat(documentSatisfiesSchemaResponse.isObjectSatisfiesSchema()).isFalse();
	}

	@Test
	void incorrectStringArrayInputJSONObject() throws Exception {

		String inputString = "{\n" + "\"id\":1," + "\"custom_field_boolean\":\"1\","
				+ "\"custom_field_booleans\":[1,0,\"1\"]," + "\"custom_field_plong\":60,"
				+ "\"custom_field_plongs\":[22,56,59]," + "\"custom_field_string\":karthik,"
				+ "\"custom_field_strings\":\"karthik\"," + "\"custom_required_field_boolean\":\"true\"," + "}";
		JSONObject payloadJSON = new JSONObject(inputString);

		Map<String, Map<String, Object>> schemaKeyValuePair = getSchemaKeyValuePair();
		DocumentParserUtil.DocumentSatisfiesSchemaResponse documentSatisfiesSchemaResponse = DocumentParserUtil
				.isDocumentSatisfySchema(schemaKeyValuePair, payloadJSON);

		assertThat(documentSatisfiesSchemaResponse.isObjectSatisfiesSchema()).isFalse();
	}

	@Test
	void invalidNewFieldInInputJSONObject() throws Exception {

		String inputString = "{\n" + "\"id\":1," + "\"custom_field_boolean\":\"1\","
				+ "\"custom_field_booleans\":[1,0,\"1\"]," + "\"custom_field_plong\":60,"
				+ "\"custom_field_plongs\":[22,56,59]," + "\"custom_field_string\":karthik,"
				+ "\"custom_field_strings\":\"karthik\"," + "\"invalid_new_field\":\"random_value\","
				+ "\"custom_required_field_boolean\":\"true\"," + "}";
		JSONObject payloadJSON = new JSONObject(inputString);

		Map<String, Map<String, Object>> schemaKeyValuePair = getSchemaKeyValuePair();
		DocumentParserUtil.DocumentSatisfiesSchemaResponse documentSatisfiesSchemaResponse = DocumentParserUtil
				.isDocumentSatisfySchema(schemaKeyValuePair, payloadJSON);

		assertThat(documentSatisfiesSchemaResponse.isObjectSatisfiesSchema()).isFalse();
	}

	@Test
	void missingRequiredFieldInInputJSONObject() throws Exception {

		String inputString = "{\n" + "\"id\":1," + "\"custom_field_boolean\":\"1\","
				+ "\"custom_field_booleans\":[1,0,\"1\"]," + "\"custom_field_plong\":60,"
				+ "\"custom_field_plongs\":[22,56,59]," + "\"custom_field_string\":karthik,"
				+ "\"custom_field_strings\":\"karthik\"," + "}";
		JSONObject payloadJSON = new JSONObject(inputString);

		Map<String, Map<String, Object>> schemaKeyValuePair = getSchemaKeyValuePair();
		DocumentParserUtil.DocumentSatisfiesSchemaResponse documentSatisfiesSchemaResponse = DocumentParserUtil
				.isDocumentSatisfySchema(schemaKeyValuePair, payloadJSON);

		assertThat(documentSatisfiesSchemaResponse.isObjectSatisfiesSchema()).isFalse();
	}

}
