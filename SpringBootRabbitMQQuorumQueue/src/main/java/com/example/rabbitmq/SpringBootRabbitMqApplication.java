package com.example.rabbitmq;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SpringBootRabbitMqApplication {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Bean
    CommandLineRunner commandLineRunner() {
        return args -> {
            rabbitTemplate.convertAndSend(RabbitMQConfig.tipicExchangeName, "foo.bar.baz", new Email("bryanxtong@gmail.com", "info@google.com", "Hello googlers"));

            Email googler = new Email("bryanxtong@gmail.com", "info@google.com", "Hello googlers");
            MessageConverter messageConverter = rabbitTemplate.getMessageConverter();
            Message message = messageConverter.toMessage(googler, new MessageProperties());
            for(int i = 0;i< 100;i++){
                rabbitTemplate.send(RabbitMQConfig.tipicExchangeName, "foo.bar.baz", message);
            }
        };
    }

    public static void main(String[] args) {
        SpringApplication.run(SpringBootRabbitMqApplication.class, args);
    }

}
