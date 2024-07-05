package com.example.rabbitmq;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class RabbitMQBatchMessageConsumer {

    private MessageConverter messageConverter;

    public RabbitMQBatchMessageConsumer(RabbitTemplate rabbitTemplate) {
        this.messageConverter = rabbitTemplate.getMessageConverter();
    }

    /**
     * The listener in batch mode does not support replies since there might not be a correlation between messages in the batch and single reply produced
     *
     * @param messages
     * @param channel
     * @throws IOException
     */
    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME_BATCH, containerFactory = "batchContainerFactory")
    public void handleMessage(List<Message> messages, Channel channel) throws IOException {
        System.out.println("Got size " + messages.size());
        messages.forEach(message -> {
            Object o = messageConverter.fromMessage(message);
            if (o instanceof Email email) {
                System.out.println("Received Message From Batch Listener<" + email.toString() + ">");
            } else if (o instanceof String str) {
                System.out.println("Received Message From Batch Listener<" + str + ">");
            } else {
                System.err.println("Unknown type of messages");
            }
        });
    }
}
