package com.solr.clientwrapper.rabbitmq;


import com.solr.clientwrapper.IntegrationTest;
import com.solr.clientwrapper.config.RabbitMQConfiguration;
import com.solr.clientwrapper.domain.service.RabbitMQReceiverService;
import com.solr.clientwrapper.domain.service.RabbitMQSenderService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;

@IntegrationTest
@AutoConfigureMockMvc
@EnableRabbit
public class RabbitMQTest {

	@MockBean
	private RabbitTemplate template;

	@MockBean
	private RabbitMQConfiguration config;

	@Autowired
	RabbitMQSenderService rabbitMQSenderService;

	@Autowired
	RabbitMQReceiverService rabbitMQReceiverService;

	private final String payload = "String";

	@Test
	public void testSendMessage() {

		rabbitMQSenderService.Sender(payload);
		Assertions.assertEquals("String", rabbitMQSenderService.message());
		Assertions.assertNotEquals("string", rabbitMQSenderService.message());

	}

	@Test
	public void MessageRecive() {

		rabbitMQReceiverService.listener(payload);
		Assertions.assertNotEquals("strings", rabbitMQReceiverService.message());
		Assertions.assertEquals("String", rabbitMQReceiverService.message());
	}

	@Test
	public void testConfigurationSends() {
		Assertions.assertEquals("message_queue", RabbitMQConfiguration.QUEUES);
		Assertions.assertEquals("message_exchange", RabbitMQConfiguration.EXCHANGES);
		Assertions.assertEquals("message_routingKey", RabbitMQConfiguration.ROUTING_KEY);
		Assertions.assertNotEquals("message_queuss", RabbitMQConfiguration.QUEUES);
		Assertions.assertNotEquals("message_exchanges", RabbitMQConfiguration.EXCHANGES);
		Assertions.assertNotEquals("message_routingKeyss", RabbitMQConfiguration.ROUTING_KEY);

	}
}