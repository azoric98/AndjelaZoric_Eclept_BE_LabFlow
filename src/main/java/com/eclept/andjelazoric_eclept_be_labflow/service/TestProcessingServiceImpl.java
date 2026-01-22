package com.eclept.andjelazoric_eclept_be_labflow.service;

import com.eclept.andjelazoric_eclept_be_labflow.config.RabbitMQConfig;
import com.eclept.andjelazoric_eclept_be_labflow.entity.Technician;
import com.eclept.andjelazoric_eclept_be_labflow.entity.TestRequest;
import com.eclept.andjelazoric_eclept_be_labflow.enums.TestStatus;
import com.eclept.andjelazoric_eclept_be_labflow.entity.TestType;
import com.eclept.andjelazoric_eclept_be_labflow.queue.TestRequestProducer;
import com.eclept.andjelazoric_eclept_be_labflow.repository.TechnicianRepository;
import com.eclept.andjelazoric_eclept_be_labflow.repository.TestRequestRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class TestProcessingServiceImpl {

    private final TechnicianRepository technicianRepository;
    private final TestRequestRepository testRequestRepository;
    private final TestRequestProducer producer;
    private final Logger logger = LoggerFactory.getLogger(TestProcessingServiceImpl.class);


    @Value(value = "${labflow.reagentReplacementTimeMinutes}")
    private int reagentReplacementTimeMinutes;

    public TestProcessingServiceImpl(TechnicianRepository technicianRepository, TestRequestRepository testRequestRepository, TestRequestProducer producer) {
        this.technicianRepository = technicianRepository;
        this.testRequestRepository = testRequestRepository;
        this.producer = producer;
    }

    @RabbitListener(queues = RabbitMQConfig.TEST_QUEUE, concurrency = "5")
    public void processTest(Long testRequestId) {
        getValidTestRequest(testRequestId).ifPresent(testRequest -> {
            findAvailableTechnician(testRequestId).ifPresent(tech -> {
                testRequest.setAssignedTechnician(tech);
                tech.setAvailable(false);
                technicianRepository.saveAndFlush(tech);
                try {
                    processTestSimulation(testRequest, tech);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    logger.error("Processing of test ID {} was aborted", testRequestId, e);
                } catch (Exception e) {
                    logger.error("Error processing test ID {}: {}", testRequestId, e.getMessage(), e);
                } finally {
                    tech.setAvailable(true);
                    technicianRepository.save(tech);
                    testRequestRepository.findFirstByStatusOrderByReceivedAt(TestStatus.RECEIVED)
                            .ifPresent(next -> producer.sendTest(next.getId()));
                }
            });
        });
    }
    private Optional<TestRequest> getValidTestRequest(Long testRequestId) {
        Optional<TestRequest> optionalRequest = testRequestRepository.findById(testRequestId);
        if (optionalRequest.isEmpty()) {
            logger.warn("Test request does not exist for ID: {}", testRequestId);
        } else if (optionalRequest.get().getStatus() != TestStatus.RECEIVED) {
            logger.info("Test ID {} is already processed or in progress", testRequestId);
            return Optional.empty();
        }
        return optionalRequest;
    }
    private Optional<Technician> findAvailableTechnician(Long testRequestId) {
        Optional<Technician> optionalTech = technicianRepository.findFirstByAvailableTrue();
        if (optionalTech.isEmpty()) {
            logger.info("No available technician for test ID {}, keeping it in queue", testRequestId);
        }
        return optionalTech;
    }
    private void ensureSufficientReagents(Technician tech, TestType testType) throws InterruptedException {
        if (tech.getAvailableReagents() < testType.getReagentUnits()) {
            logger.info("""
                {} does not have enough reagents
                Available: {}
                Needed: {}""",
                    tech.getName(), tech.getAvailableReagents(), testType.getReagentUnits());

            tech.setReplacingReagents(true);
            technicianRepository.saveAndFlush(tech);

            logger.info("{} is replacing reagents at {}", tech.getName(), LocalDateTime.now());
            Thread.sleep(reagentReplacementTimeMinutes * 60 * 1000L);

            tech.setAvailableReagents(500);
            tech.setReplacingReagents(false);
            logger.info("{} finished replacing reagents at {}", tech.getName(), LocalDateTime.now());
        }
        tech.setAvailableReagents(tech.getAvailableReagents() - testType.getReagentUnits());
        technicianRepository.save(tech);
    }
    private void processTestSimulation(TestRequest testRequest, Technician tech) throws InterruptedException {
        testRequest.setStatus(TestStatus.PROCESSING);
        testRequestRepository.save(testRequest);

        ensureSufficientReagents(tech, testRequest.getTestType());

        Thread.sleep(testRequest.getTestType().getProcessingTimeSeconds() * 1000L);

        testRequest.setStatus(TestStatus.COMPLETED);
        testRequest.setCompletedAt(LocalDateTime.now());
        testRequestRepository.save(testRequest);

        logger.info("Test ID {} was processed successfully", testRequest.getId());
    }




}
