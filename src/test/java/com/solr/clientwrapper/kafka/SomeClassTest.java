package com.solr.clientwrapper.kafka;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;

import com.solr.clientwrapper.IntegrationTest;
import com.solr.clientwrapper.config.RabbitMQConfiguration;

@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
@EnableRabbit
public class SomeClassTest {
	
	@MockBean
	private RabbitTemplate template;


	@MockBean
    private RabbitMQConfiguration config;




	 @Test
	    public void testConfigurationSends() {
	      this.template.convertAndSend("message_queue", "message_queue","omkar");
			Assertions.assertEquals("message_queue", config.QUEUES);
			Assertions.assertEquals("message_exchange", config.EXCHANGES);
			Assertions.assertEquals("message_routingKey", config.ROUTING_KEY);

	    }
	 @Test
	    public void testConfiguration() {
	      this.template.convertAndSend("message_queue", "message_queue","omkar");
			Assertions.assertNotEquals("message_queuss", config.QUEUES);
			Assertions.assertNotEquals("message_exchanges", config.EXCHANGES);
			Assertions.assertNotEquals("message_routingKeyss", config.ROUTING_KEY);

	    }
}