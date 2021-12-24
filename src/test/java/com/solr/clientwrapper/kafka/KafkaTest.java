package com.solr.clientwrapper.kafka;



import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;

import com.solr.clientwrapper.IntegrationTest;
import com.solr.clientwrapper.domain.service.KafkaReciever;
import com.solr.clientwrapper.domain.service.KafkaSender;


@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
public class KafkaTest {
	
	
	
	private static final String payload = "\r\n"
			+ "         \"id\": \"01\",\r\n"
			+ "         \"language\": \"Java\",\r\n"
			+ "         \"edition\": \"third\",\r\n"
			+ "         \"author\": \"Herbert Schildt\"";

	private static final String message = "\r\n"
			+ "         \"id\": \"01\",\r\n"
			+ "         \"language\": \"Java\",\r\n"
			+ "         \"edition\": \"third\",\r\n"
			+ "         \"author\": \"Herbert Schildt\"";

	@MockBean
	private KafkaSender sender;

	@MockBean
	private KafkaReciever consumer;

	@Test
	public void AddToQueue() {
		String s = sender.AddToQueue(payload);
		Assertions.assertEquals(payload, "\r\n"
				+ "         \"id\": \"01\",\r\n"
				+ "         \"language\": \"Java\",\r\n"
				+ "         \"edition\": \"third\",\r\n"
				+ "         \"author\": \"Herbert Schildt\"");
	}

	@Test
	public void recieveData() {
		consumer.recieveData(message);
		Assertions.assertEquals(message, "\r\n"
				+ "         \"id\": \"01\",\r\n"
				+ "         \"language\": \"Java\",\r\n"
				+ "         \"edition\": \"third\",\r\n"
				+ "         \"author\": \"Herbert Schildt\"");
	}
}