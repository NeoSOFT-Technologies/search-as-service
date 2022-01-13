/**
 * 
 */
package com.solr.clientwrapper.SolrParseDoc;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import com.solr.clientwrapper.domain.service.SolrParseDocSerice;
import com.solr.clientwrapper.IntegrationTest;
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class SolrDocParserResourceTest  {
  

    
	  @MockBean
	SolrParseDocSerice SolrParseDocSerice;
    

    @Autowired
    private MockMvc restAMockMvc;

    
	
	@Test
	void whenFileUploaded_thenVerifyStatus() throws Exception {
		
		String text = "Text to be uploaded.";
		MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", text.getBytes());
		restAMockMvc.perform(MockMvcRequestBuilders.multipart("/ingest/upload").file(file).characterEncoding("UTF-8"))
				.andExpect(MockMvcResultMatchers.status().isOk());

	}

}
