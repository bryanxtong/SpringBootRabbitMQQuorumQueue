package com.example.rabbitmq;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RabbitMQConsumer {
    private MessageConverter messageConverter;

    public RabbitMQConsumer(RabbitTemplate rabbitTemplate) {
        this.messageConverter = rabbitTemplate.getMessageConverter();
    }

    @RabbitListener(queues = RabbitMQConfig.queueName)
    public void handleMessage(List<Message> messages) {
        System.out.println("Got size " + messages.size());
        messages.forEach(message -> {
            Object o = messageConverter.fromMessage(message);
            if (o instanceof Email email) {
                System.out.println("Received Message<" + email.toString() + ">");
            } else if (o instanceof String str) {
                System.out.println("Received Message<" + str + ">");
            } else {
                System.err.println("Unknown type of messages");
            }
        });
    }
}
