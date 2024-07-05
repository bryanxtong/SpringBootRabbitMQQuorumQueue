package com.example.rabbitmq;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.AsyncRabbitTemplate;
import org.springframework.amqp.rabbit.RabbitConverterFuture;
import org.springframework.amqp.rabbit.RabbitMessageFuture;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class SpringBootRabbitMqApplication {
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private AsyncRabbitTemplate asyncRabbitTemplate;

    /**
     * Send messages to queue [spring-boot-batch] for consumer to do batch processing
     * @return
     */
    @Bean
    CommandLineRunner commandLineRunner() {
        return args -> {
            Email googler = new Email("bryanxtong@gmail.com", "info@google.com", "Hello googlers");
            MessageConverter messageConverter = rabbitTemplate.getMessageConverter();
            Message message = messageConverter.toMessage(googler, new MessageProperties());

            for (int i = 0; i < 5; i++) {
                rabbitTemplate.send(RabbitMQConfig.TOPIC_EXCHANGE_NAME_BATCH, "batch.baz", message);
            }
        };
    }

    /**
     * Send messages and wait for a reply
     * @return
     */
    @Bean
    CommandLineRunner commandLineRunner1() {
        return args -> {
            List<Message> messages = new ArrayList<>();
            MessageConverter messageConverter = asyncRabbitTemplate.getMessageConverter();
            for (int i = 0; i < 2; i++) {
                Email googler = new Email("clent1@gmail.com", "info@google.com", "Hello googlers 11");
                messages.add(messageConverter.toMessage(googler, new MessageProperties()));
            }
            messages.forEach(message -> {
                RabbitMessageFuture rabbitMessageFuture = asyncRabbitTemplate.sendAndReceive(RabbitMQConfig.TOPIC_EXCHANGE_NAME, "foo.bar.baz", message);
                rabbitMessageFuture.whenComplete((message1, e) -> {
                    if (e == null) {
                        System.out.println("client1 response received==>" + messageConverter.fromMessage(message1));
                    } else {
                        e.printStackTrace();
                    }
                });

            });
        };
    }

    /**
     * Send messages and wait for a reply
     * @return
     */
    @Bean
    CommandLineRunner commandLineRunner2() {
        return args -> {
            List<Email> messages = new ArrayList<>();
            for (int i = 0; i < 2; i++) {
                Email googler = new Email("client2@gmail.com", "info@google.com", "Hello googlers");
                messages.add(googler);
            }
            messages.forEach(message -> {
                RabbitConverterFuture<Object> objectRabbitConverterFuture = asyncRabbitTemplate.convertSendAndReceive(RabbitMQConfig.TOPIC_EXCHANGE_NAME, "foo.bar.baz", message);
                objectRabbitConverterFuture.whenComplete((m, e) -> {
                    if (e == null) {
                        System.out.println("client2 response received==>" + m);
                    } else {
                        e.printStackTrace();
                    }
                });
            });
        };
    }

    public static void main(String[] args) {
        SpringApplication.run(SpringBootRabbitMqApplication.class, args);
    }

}
