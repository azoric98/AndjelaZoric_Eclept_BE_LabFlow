package com.eclept.andjelazoric_eclept_be_labflow.service;

import com.eclept.andjelazoric_eclept_be_labflow.config.RabbitMQConfig;
import com.eclept.andjelazoric_eclept_be_labflow.entity.Technician;
import com.eclept.andjelazoric_eclept_be_labflow.entity.TestRequest;
import com.eclept.andjelazoric_eclept_be_labflow.enums.TestStatus;
import com.eclept.andjelazoric_eclept_be_labflow.entity.TestType;
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
public class TestProcessingService {

    private final TechnicianRepository technicianRepository;
    private final TestRequestRepository testRequestRepository;
    private final TestRequestProducer producer;
    private final Logger logger = LoggerFactory.getLogger(TestProcessingService.class);


    @Value(value = "${labflow.reagentReplacementTimeMinutes}")
    private int reagentReplacementTimeMinutes;

    public TestProcessingService(TechnicianRepository technicianRepository, TestRequestRepository testRequestRepository, TestRequestProducer producer) {
        this.technicianRepository = technicianRepository;
        this.testRequestRepository = testRequestRepository;
        this.producer = producer;
    }

    @RabbitListener(queues = RabbitMQConfig.TEST_QUEUE, concurrency = "5")
    public void processTest(Long testRequestId) {
        Optional<TestRequest> optionalRequest = testRequestRepository.findById(testRequestId);
        if (optionalRequest.isEmpty()) {
            logger.warn(" Test request does not exist for ID: {}", testRequestId);
            return;
        }
        TestRequest testRequest = optionalRequest.get();
        // todo: provjera da li je tip ok

        if (testRequest.getStatus() != TestStatus.RECEIVED) {
            logger.info("Test ID {} is already processed or in progress", testRequestId);
            return;
        }
        // Check if TestType exists
        TestType testType = testRequest.getTestType();
        // Checking if there is a technician available
        Optional<Technician> optionalTechnician = technicianRepository.findFirstByAvailableTrue();
        if (optionalTechnician.isEmpty()) {
            logger.info("\nNo available technician for test ID {}, keeping it in queue", testRequestId);
            return;
        }
        Technician tech = optionalTechnician.get();
        testRequest.setAssignedTechnician(tech);
        tech.setAvailable(false);
        technicianRepository.saveAndFlush(tech);

        try {
            testRequest.setStatus(TestStatus.PROCESSING);
            testRequestRepository.save(testRequest);

            // Reagent check
            if (tech.getAvailableReagents() < testType.getReagentUnits()) {
                logger.info("""
                                \n{} does not have enough reagents\s
                                Number of available: \
                                {}
                                Number of reagent units: {}""",
                        tech.getName(), tech.getAvailableReagents(), testType.getReagentUnits());
                tech.setReplacingReagents(true);
                technicianRepository.saveAndFlush(tech);
                logger.info("\n{} is replacing reagents, time: {}", tech.getName(), LocalDateTime.now());

                Thread.sleep(reagentReplacementTimeMinutes * 60 * 1000L);
                tech.setAvailableReagents(500);
                tech.setReplacingReagents(false);

                logger.info("\n{} finished replacing reagents, time: {}", tech.getName(), LocalDateTime.now());
            }

            tech.setAvailableReagents(tech.getAvailableReagents() - testType.getReagentUnits());
            technicianRepository.save(tech);

            // Test processing simulation
            Thread.sleep(testType.getProcessingTimeSeconds() * 1000L);

            testRequest.setStatus(TestStatus.COMPLETED);
            testRequest.setCompletedAt(LocalDateTime.now());
            testRequestRepository.save(testRequest);
            logger.info("Test ID {} was processed successfully", testRequestId);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Processing of test ID {} was aborted", testRequestId, e);
        } catch (Exception e) {
            logger.error(" Error processing test ID {}: {}", testRequestId, e.getMessage(), e);
        } finally {

            tech.setAvailable(true);
            technicianRepository.save(tech);
            testRequestRepository
                    .findFirstByStatusOrderByReceivedAt(TestStatus.RECEIVED)
                    .ifPresent(next ->
                            producer.sendTest(next.getId())
                    );
        }
    }
}
