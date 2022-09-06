package com.neosoft.app.rest.user;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.neosoft.app.IntegrationTest;
import com.neosoft.app.TestUtil;
import com.neosoft.app.domain.dto.Response;
import com.neosoft.app.domain.dto.user.User;
import com.neosoft.app.domain.service.UserService;
import com.neosoft.app.rest.errors.HttpStatusCode;

@IntegrationTest
@AutoConfigureMockMvc(addFilters = false)
class UserResourceTest {

	@Autowired
	private MockMvc restMockMvc;
	
	@MockBean
	private UserService userService;
	
	public void setMockitoSuccessResponse() {
		Response tokenResponseDTO = new Response();
		tokenResponseDTO.setStatusCode(200);
		tokenResponseDTO.setMessage("Token Generated Sucessfully");	
		Mockito.when(userService.getToken(Mockito.any())).thenReturn(tokenResponseDTO);
	}
	
	public void setMockitoBadResponse() {
		Response tokenResponseDTO = new Response();
		tokenResponseDTO.setStatusCode(HttpStatusCode.BAD_REQUEST_EXCEPTION.getCode());
		tokenResponseDTO.setMessage("Invalid Credentials");	
		Mockito.when(userService.getToken(Mockito.any())).thenReturn(tokenResponseDTO);
	}
	
	@Test
	void getTokenTest() throws Exception{
		
		User user = new User("testRealm", "test", "123");
		setMockitoSuccessResponse();
		restMockMvc.perform(MockMvcRequestBuilders.post("/user/token")
				.contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(user)))
				.andExpect(status().isOk());
		
		setMockitoBadResponse();
		restMockMvc.perform(MockMvcRequestBuilders.post("/user/token")
				.contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(user)))
				.andExpect(status().isBadRequest());
	}
}
