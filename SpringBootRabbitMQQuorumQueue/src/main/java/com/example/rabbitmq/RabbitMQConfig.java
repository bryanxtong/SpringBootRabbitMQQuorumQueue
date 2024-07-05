package com.example.rabbitmq;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.AsyncRabbitTemplate;
import org.springframework.amqp.rabbit.config.DirectRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String TOPIC_EXCHANGE_NAME = "spring-boot-exchange";
    public static final String QUEUE_NAME = "spring-boot";

    /**
     * Test Batch messages for rabbit listener, batch messages doesn't support replies
     */
    public static final String QUEUE_NAME_BATCH = "spring-boot-batch";

    public static final String TOPIC_EXCHANGE_NAME_BATCH = "spring-boot-exchange-batch";


    /**
     * Create a quorum queue
     * @return
     */
    @Bean
    Queue queue() {
        /*Map<String,Object> args = new HashMap<>();
        args.put("x-queue-type","quorum");
        return new Queue(queueName, true, false,false,args);*/
        return QueueBuilder.durable(QUEUE_NAME).quorum().build();
    }

    @Bean
    Queue queueForBatch(){
        return QueueBuilder.durable(QUEUE_NAME_BATCH).quorum().build();
    }

    @Bean
    TopicExchange exchange() {
        return new TopicExchange((TOPIC_EXCHANGE_NAME));
    }


    @Bean
    TopicExchange exchangeForBatch() {
        return new TopicExchange((TOPIC_EXCHANGE_NAME_BATCH));
    }

    @Bean
    Binding binding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with("foo.bar.#");
    }

    @Bean
    Binding binding1() {
        return BindingBuilder.bind(queueForBatch()).to(exchangeForBatch()).with("batch.#");
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory){
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setMessageConverter(messageConverter());
        rabbitTemplate.setConnectionFactory(connectionFactory);
        return rabbitTemplate;
    }

    @Bean
    public AsyncRabbitTemplate asyncRabbitTemplate(RabbitTemplate rabbitTemplate){
        AsyncRabbitTemplate asyncRabbitTemplate = new AsyncRabbitTemplate(rabbitTemplate);
        //asyncRabbitTemplate.setEnableConfirms(true);
        return asyncRabbitTemplate;
    }

    @Bean("directContainerFactory")
    public DirectRabbitListenerContainerFactory directRabbitListenerContainerFactory(ConnectionFactory connectionFactory){
        System.out.println(connectionFactory.getClass().getName() + connectionFactory.getPublisherConnectionFactory());
        DirectRabbitListenerContainerFactory factory = new DirectRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter());
        return factory;
    }
    @Bean("batchContainerFactory")
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory){
        System.out.println(connectionFactory.getClass().getName() + connectionFactory.getPublisherConnectionFactory());
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter());
        factory.setBatchListener(true);
        factory.setBatchSize(10);
        factory.setConsumerBatchEnabled(true);
        return factory;
    }
}
