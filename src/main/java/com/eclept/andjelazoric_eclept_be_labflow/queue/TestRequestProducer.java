package com.eclept.andjelazoric_eclept_be_labflow.queue;


import com.eclept.andjelazoric_eclept_be_labflow.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class TestRequestProducer {

    private final RabbitTemplate rabbitTemplate;

    public TestRequestProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendTest(Long testRequestId) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.TEST_EXCHANGE,
                RabbitMQConfig.TEST_ROUTING_KEY,
                testRequestId
        );
    }

    public void sendError(Long testRequestId) {
        rabbitTemplate.convertAndSend(RabbitMQConfig.TEST_EXCHANGE,
                RabbitMQConfig.ERROR_ROUTING_KEY,
                testRequestId);
    }
}
