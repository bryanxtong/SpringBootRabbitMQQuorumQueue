package com.example.rabbitmq;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQSingleMessageConsumer {

    private MessageConverter messageConverter;

    public RabbitMQSingleMessageConsumer(RabbitTemplate rabbitTemplate) {
        this.messageConverter = rabbitTemplate.getMessageConverter();
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME, containerFactory = "directContainerFactory")
    @SendTo
    public String handleMessage(Message message) {
        Object o = messageConverter.fromMessage(message);
        if (o instanceof Email email) {
            System.out.println("Received Message<" + email.toString() + ">");
        } else if (o instanceof String str) {
            System.out.println("Received Message<" + str + ">");
        } else {
            System.err.println("Unknown type of messages");
        }
        return ((Email) o).getFrom();
    }
}
