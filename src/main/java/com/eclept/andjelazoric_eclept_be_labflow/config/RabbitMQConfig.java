package com.eclept.andjelazoric_eclept_be_labflow.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    public static final String TEST_QUEUE = "labflow-tests";
    public static final String ERROR_QUEUE = "labflow-tests-errors";
    public static final String TEST_EXCHANGE = "labflow-exchange";
    public static final String TEST_ROUTING_KEY = "labflow-routing-key";
    public static final String ERROR_ROUTING_KEY = "labflow-error-routing-key";

    @Bean
    public Queue testQueue() {
        return new Queue(TEST_QUEUE, true);
    }

    @Bean
    public Queue errorQueue() {
        return new Queue(ERROR_QUEUE, true);
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

    @Bean
    public Binding errorBinding(Queue errorQueue, DirectExchange errorExchange) {
        return BindingBuilder.bind(errorQueue)
                .to(errorExchange)
                .with(ERROR_ROUTING_KEY);
    }
}
