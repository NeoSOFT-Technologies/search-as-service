package com.solr.clientwrapper.domain.service;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.solr.clientwrapper.config.RabbitMQConfiguration;

@Component
public class MessageListener {

    @RabbitListener(queues = RabbitMQConfiguration.QUEUE)
    public void listener(String  payloads) {
        System.out.println("ssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss"+payloads);
    }

}
