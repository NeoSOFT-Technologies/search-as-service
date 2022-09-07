package com.neosoft.app.rest.product;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.neosoft.app.IntegrationTest;
import com.neosoft.app.TestUtil;
import com.neosoft.app.domain.dto.Response;
import com.neosoft.app.domain.dto.Response.ProductResponse;
import com.neosoft.app.domain.port.api.ProductServicePort;
import com.neosoft.app.infrastructure.entity.Product;

@IntegrationTest
@AutoConfigureMockMvc	//(addFilters = false)
class ProductResourceTest {

	@Value("${custom-mock.jwt-token}")
	private String accessToken;

	@Value("${base-url.api-endpoint.home}")
	private String apiEndpoint;
	
	@Autowired
	private MockMvc restMvcMock;

	@MockBean
	private ProductServicePort productServicePort;

	// Input Stubs
	private int productId;
	private Product createProductDTO = null;
	private Product updateProductDTO = null;
	
	// Response Stubs
	private Response getResponseDTO = null;
	private Response createResponseDTO = null;
	private Response updateResponseDTO = null;
	private Response deleteResponseDTO = null;
	
	@BeforeEach
	void setUp() {
		apiEndpoint += "/product";
		productId = 1;
		
		getResponseDTO = new Response();
		getResponseDTO.setStatusCode(200);
		List<ProductResponse> productsResponse = new ArrayList<>();
		productsResponse.add(new ProductResponse(1, "TestProduct1"));
		productsResponse.add(new ProductResponse(2, "TestProduct2"));
		getResponseDTO.setProductList(productsResponse);
		getResponseDTO.setData(Arrays.asList("product1", "product2"));
		
		createResponseDTO = new Response();
		createResponseDTO.setStatusCode(200);
		
		updateResponseDTO = new Response();
		updateResponseDTO.setStatusCode(200);
		
		deleteResponseDTO = new Response();
		deleteResponseDTO.setStatusCode(200);
		
		createProductDTO = new Product();
		updateProductDTO = new Product();
		
		Mockito.when(productServicePort.getAllProducts()).thenReturn(getResponseDTO);
		Mockito.when(productServicePort.getProduct(Mockito.anyInt())).thenReturn(getResponseDTO);
		Mockito.when(productServicePort.createProduct(Mockito.any())).thenReturn(createResponseDTO);
		Mockito.when(productServicePort.updateProduct(Mockito.anyInt(), Mockito.any())).thenReturn(updateResponseDTO);
		Mockito.when(productServicePort.deleteProduct(Mockito.anyInt())).thenReturn(deleteResponseDTO);
	}


	@Test
	void testGetAllProducts() throws Exception {
		restMvcMock
				.perform(MockMvcRequestBuilders.get(apiEndpoint + "/")
						.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	@Test
	void testGetProduct() throws Exception {
		restMvcMock
				.perform(MockMvcRequestBuilders.get(apiEndpoint + "/" + productId)
						.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	@Test
	void testCreateProduct() throws Exception {
		restMvcMock.perform(MockMvcRequestBuilders.post(apiEndpoint + "/")
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken).contentType(MediaType.APPLICATION_JSON)
				.content(TestUtil.convertObjectToJsonBytes(createProductDTO))).andExpect(status().isOk());
	}
	
	@Test
	void testUpdateProduct() throws Exception {
		restMvcMock.perform(MockMvcRequestBuilders.put(apiEndpoint + "/" + productId)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken).contentType(MediaType.APPLICATION_JSON)
				.content(TestUtil.convertObjectToJsonBytes(updateProductDTO))).andExpect(status().isOk());
	}

	@Test
	void testDeleteProduct() throws Exception {
		Response deleteProductResponseDTO = new Response();
		restMvcMock.perform(MockMvcRequestBuilders.delete(apiEndpoint + "/" + productId)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken).contentType(MediaType.APPLICATION_JSON)
				.content(TestUtil.convertObjectToJsonBytes(deleteProductResponseDTO))).andExpect(status().isOk());
	}

}
