package com.eclept.andjelazoric_eclept_be_labflow.service;

import com.eclept.andjelazoric_eclept_be_labflow.config.RabbitMQConfig;
import com.eclept.andjelazoric_eclept_be_labflow.entity.Technician;
import com.eclept.andjelazoric_eclept_be_labflow.entity.TestRequest;
import com.eclept.andjelazoric_eclept_be_labflow.enums.TestStatus;
import com.eclept.andjelazoric_eclept_be_labflow.queue.TestRequestProducer;
import com.eclept.andjelazoric_eclept_be_labflow.repository.TestRequestRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

@Service
public class TestProcessor {

    private final TestRequestRepository testRequestRepository;
    private final TechnicianService technicianService;
    private final TestRequestService testRequestService;
    private final TestRequestProducer producer;
    private final Logger logger = LoggerFactory.getLogger(TestProcessor.class);


    @Value(value = "${labflow.reagentReplacementTimeMinutes}")
    private int reagentReplacementTimeMinutes;

    public TestProcessor(TechnicianService technicianService, TestRequestService testRequestService, TestRequestRepository testRequestRepository, TestRequestProducer producer) {
        this.technicianService = technicianService;
        this.testRequestService = testRequestService;
        this.testRequestRepository = testRequestRepository;
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
                    logger.error("Error processing test ID {}: {}", testRequestId, e.getMessage(), e);
                } finally {
                    tech.setAvailable(true);
                    technicianService.save(tech);
                    testRequestRepository.findFirstByStatusOrderByReceivedAt(TestStatus.RECEIVED)
                            .ifPresent(next -> producer.sendTest(next.getId()));
                }
            });
        });
    }

    private void processTestSimulation(TestRequest testRequest, Technician tech) throws InterruptedException {
        testRequest.setStatus(TestStatus.PROCESSING);
        testRequestRepository.save(testRequest);
       technicianService.ensureSufficientReagents(tech, testRequest.getTestType());
        Thread.sleep(testRequest.getTestType().getProcessingTimeSeconds() * 1000L);

        testRequest.setStatus(TestStatus.COMPLETED);
        testRequest.setCompletedAt(LocalDateTime.now());
        testRequestRepository.save(testRequest);

        logger.info("Test ID {} was processed successfully", testRequest.getId());
    }




}
