package com.neosoft.app.domain.utils;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neosoft.app.rest.errors.CustomException;
import com.neosoft.app.rest.errors.HttpStatusCode;

public class HttpGatewayUtil {
	private final Logger log = LoggerFactory.getLogger(HttpGatewayUtil.class);
    private static final String CONTENT_TYPE = "Content-type";
    private static final String ACCEPT = "Accept";
    private static final String AUTHORIZATION = "Authorization";
    private static final String APPLICATION_JSON = "application/json";
    private static final String API_ENDPOINT = "API Endpoint : {}";
	private String apiEndpoint;
	private Object requestBodyDTO;
	
	public String postRequestWithStringBody(String jwtToken) {

		String jsonObject = null;
		log.debug("Post Request String Method Called in HttpGateway");
		log.debug(API_ENDPOINT ,apiEndpoint);
		log.debug("Request Body - {}",requestBodyDTO);
		isTokenNullOrValid(jwtToken);
		try(CloseableHttpClient client = HttpClients.createDefault();){
		    HttpPost http = new HttpPost(apiEndpoint);
			String objJackson = requestBodyDTO.toString();
			StringEntity entity = new StringEntity(objJackson);
			http.setEntity(entity);
			http.setHeader(ACCEPT, APPLICATION_JSON );
	        http.setHeader(CONTENT_TYPE, APPLICATION_JSON );
	        http.setHeader(AUTHORIZATION, jwtToken);
			CloseableHttpResponse response = client.execute(http);
			HttpEntity entityResponse = response.getEntity();
			String result = EntityUtils.toString(entityResponse);
			jsonObject = result;
		} catch (Exception e) {
			handleException(e);
			log.error(e.toString());

		}

		return jsonObject;

	}

	public String postRequest(String jwtToken) {

		log.debug("Post Request Method Called in HttpGateway");
		log.debug(API_ENDPOINT ,apiEndpoint);
		log.debug("REQUEST BODY : {}", requestBodyDTO);
		isTokenNullOrValid(jwtToken);
		String result = "{}";
		try(CloseableHttpClient client = HttpClients.createDefault();){
		    HttpPost http = new HttpPost(apiEndpoint);
		    ObjectMapper objectMapper = new ObjectMapper();
			String objJackson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(requestBodyDTO);
			StringEntity entity = new StringEntity(objJackson);
			http.setEntity(entity);
			http.setHeader(ACCEPT, APPLICATION_JSON );
	        http.setHeader(CONTENT_TYPE, APPLICATION_JSON );
	        http.setHeader(AUTHORIZATION, jwtToken);
			log.debug("Sending POST request");
			CloseableHttpResponse response = client.execute(http);
			HttpEntity entityResponse = response.getEntity();
			 result = EntityUtils.toString(entityResponse);
		} catch (Exception e) {
			handleException(e);
			log.error(e.toString());

		}

		return result;

	}

	public String putRequest(String jwtToken) {
		log.debug("Put Request Method Called in HttpGateway");
		log.debug(API_ENDPOINT, apiEndpoint);
		isTokenNullOrValid(jwtToken);
		String result = "{}";

		try(CloseableHttpClient client = HttpClients.createDefault();){
		    HttpPut http = new HttpPut(apiEndpoint);
	     	ObjectMapper objectMapper = new ObjectMapper();
			String objJackson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(requestBodyDTO);
			StringEntity entity = new StringEntity(objJackson);
			http.setEntity(entity);
			http.setHeader(ACCEPT, APPLICATION_JSON );
	        http.setHeader(CONTENT_TYPE, APPLICATION_JSON );
	        http.setHeader(AUTHORIZATION, jwtToken);
			log.debug("Sending DELETE request");
			CloseableHttpResponse response = client.execute(http);
			HttpEntity entityResponse = response.getEntity();
			result = EntityUtils.toString(entityResponse);
		} catch (Exception e) {
			handleException(e);
			log.error(e.toString());

		}

		return result;

	}

	public String deleteRequest(String jwtToken) {
		log.debug("Delete Request Method Called in HttpGateway");
		log.debug(API_ENDPOINT, apiEndpoint);
		isTokenNullOrValid(jwtToken);
		String result = "{}";
		try(CloseableHttpClient client = HttpClients.createDefault();){
		    HttpDelete http = new HttpDelete(apiEndpoint);
			http.setHeader(ACCEPT, APPLICATION_JSON );
	        http.setHeader(CONTENT_TYPE, APPLICATION_JSON );
	        http.setHeader(AUTHORIZATION, jwtToken);
			CloseableHttpResponse response = client.execute(http);
			HttpEntity entityResponse = response.getEntity();
			 result = EntityUtils.toString(entityResponse);
		} catch (Exception e) {
			handleException(e);
			log.error(e.toString());

		}

		return result;


	}

	public JSONObject getRequest(String jwtToken) {

		JSONObject jsonObject = null;
		isTokenNullOrValid(jwtToken);
		try(CloseableHttpClient client = HttpClients.createDefault();){
		    HttpGet http = new HttpGet(apiEndpoint);
			http.setHeader(ACCEPT, APPLICATION_JSON );
            http.setHeader(CONTENT_TYPE, APPLICATION_JSON );
            http.setHeader(AUTHORIZATION, jwtToken);
			CloseableHttpResponse response = client.execute(http);
			HttpEntity entityResponse = response.getEntity();
			String result = EntityUtils.toString(entityResponse);
			jsonObject = new JSONObject(result);


		} catch (Exception e) {
			handleException(e);
			log.error(e.toString());
		}

		return jsonObject;
	}
	
	public String getRequestV2(String jwtToken) {

        String result = "{}";
        isTokenNullOrValid(jwtToken);
        try(CloseableHttpClient client = HttpClients.createDefault();){
            HttpGet http = new HttpGet(apiEndpoint);
            http.setHeader(ACCEPT, APPLICATION_JSON );
            http.setHeader(CONTENT_TYPE, APPLICATION_JSON );
            http.setHeader(AUTHORIZATION, jwtToken);
            CloseableHttpResponse response = client.execute(http);
            HttpEntity entityResponse = response.getEntity();
            result = EntityUtils.toString(entityResponse);
        } catch (Exception e) {
        	handleException(e);
            log.error(e.toString());

        }
        return result;

    }

	private void handleException(Exception exception) {
		if(exception instanceof HttpHostConnectException) {
			throw new CustomException(HttpStatusCode.SERVER_UNAVAILABLE.getCode(),
					HttpStatusCode.SERVER_UNAVAILABLE ,"Unable to Connect With the Server");
		}
	}

	public void isTokenNullOrValid(String jwtToken) {
    	if((null == jwtToken || jwtToken.isEmpty() || jwtToken.isBlank() || !jwtToken.startsWith("Bearer ")))
    		throw new CustomException(HttpStatusCode.FORBIDDEN_EXCEPTION.getCode(), 
    				HttpStatusCode.FORBIDDEN_EXCEPTION, "Provide valid token");
    }
}
