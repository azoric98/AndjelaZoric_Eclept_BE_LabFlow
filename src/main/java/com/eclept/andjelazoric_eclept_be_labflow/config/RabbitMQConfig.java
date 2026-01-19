package com.eclept.andjelazoric_eclept_be_labflow.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.core.Queue;

@Configuration
public class RabbitMQConfig {
    public static final String TEST_QUEUE = "labflow-tests";
    public static final String TEST_EXCHANGE = "labflow-exchange";
    public static final String TEST_ROUTING_KEY = "labflow-routing-key";

    @Bean
    public Queue testQueue() {
        return new Queue(TEST_QUEUE, true); // durable
    }

    @Bean
    public DirectExchange testExchange() {
        return new DirectExchange(TEST_EXCHANGE);
    }

    @Bean
    public Binding testBinding(Queue testQueue, DirectExchange testExchange) {
        return BindingBuilder.bind(testQueue)
                .to(testExchange)
                .with(TEST_ROUTING_KEY);
    }
}
