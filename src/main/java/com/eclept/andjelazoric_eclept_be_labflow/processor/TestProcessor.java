package com.eclept.andjelazoric_eclept_be_labflow.processor;

import com.eclept.andjelazoric_eclept_be_labflow.config.RabbitMQConfig;
import com.eclept.andjelazoric_eclept_be_labflow.entity.Technician;
import com.eclept.andjelazoric_eclept_be_labflow.entity.TestRequest;
import com.eclept.andjelazoric_eclept_be_labflow.enums.TestStatus;
import com.eclept.andjelazoric_eclept_be_labflow.queue.TestRequestProducer;
import com.eclept.andjelazoric_eclept_be_labflow.service.TechnicianService;
import com.eclept.andjelazoric_eclept_be_labflow.service.TestRequestService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

@Component
public class TestProcessor {

    private final TechnicianService technicianService;
    private final TestRequestService testRequestService;
    private final TestRequestProducer producer;
    private final Logger logger = LoggerFactory.getLogger(TestProcessor.class);


    @Value(value = "${labflow.reagentReplacementTimeMinutes}")
    private int reagentReplacementTimeMinutes;

    public TestProcessor(TechnicianService technicianService, TestRequestService testRequestService, TestRequestProducer producer) {
        this.technicianService = technicianService;
        this.testRequestService = testRequestService;
        this.producer = producer;
    }

    @RabbitListener(queues = RabbitMQConfig.TEST_QUEUE, concurrency = "5")
    public void processTest(Long testRequestId) {
        testRequestService.getProcessableTestRequest(testRequestId).ifPresent(testRequest -> {
            technicianService.findAvailableTechnician(testRequestId).ifPresent(tech -> {
                testRequest.setAssignedTechnician(tech);
                tech.setAvailable(false);
                technicianService.save(tech);
                try {
                    processTestSimulation(testRequest, tech);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    logger.error("Processing of test ID {} was aborted", testRequestId, e);
                } catch (Exception e) {
                    producer.sendError(testRequestId);
                    logger.error("Error processing test ID {}: {}", testRequestId, e.getMessage(), e);
                } finally {
                    tech.setAvailable(true);
                    technicianService.save(tech);
                    testRequestService.getFirstByStatusOrderByReceivedAt();
                }
            });
        });
    }

    private void processTestSimulation(TestRequest testRequest, Technician tech) throws InterruptedException {

        testRequestService.setProcessingStatus(testRequest, null, TestStatus.PROCESSING);
       technicianService.ensureSufficientReagents(tech, testRequest.getTestType());
        Thread.sleep(testRequest.getTestType().getProcessingTimeSeconds() * 1000L);

        testRequestService.setProcessingStatus(testRequest, LocalDateTime.now(), TestStatus.COMPLETED);

        logger.info("Test ID {} was processed successfully", testRequest.getId());
    }

}
