package com.eclept.andjelazoric_eclept_be_labflow.service;

import com.eclept.andjelazoric_eclept_be_labflow.config.RabbitMQConfig;
import com.eclept.andjelazoric_eclept_be_labflow.entity.Technician;
import com.eclept.andjelazoric_eclept_be_labflow.entity.TestRequest;
import com.eclept.andjelazoric_eclept_be_labflow.entity.TestStatus;
import com.eclept.andjelazoric_eclept_be_labflow.entity.TestType;
import com.eclept.andjelazoric_eclept_be_labflow.repository.TechnicianRepository;
import com.eclept.andjelazoric_eclept_be_labflow.repository.TestRequestRepository;
import com.eclept.andjelazoric_eclept_be_labflow.repository.TestTypeRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class TestProcessingService {

    private final TechnicianRepository technicianRepository;
    private final TestTypeRepository testTypeRepository;
    private final TestRequestRepository testRequestRepository;
    private final Logger logger = LoggerFactory.getLogger(TestProcessingService.class);


    @Value(value = "${labflow.reagentReplacementTimeMinutes}")
    private int reagentReplacementTimeMinutes;

    public TestProcessingService(TechnicianRepository technicianRepository, TestTypeRepository testTypeRepository, TestRequestRepository testRequestRepository) {
        this.technicianRepository = technicianRepository;
        this.testTypeRepository = testTypeRepository;
        this.testRequestRepository = testRequestRepository;
    }

    @RabbitListener(queues = RabbitMQConfig.TEST_QUEUE, concurrency = "5")
    @Transactional
    public void processTest(Long testRequestId) {
        Optional<TestRequest> optionalRequest = testRequestRepository.findById(testRequestId);
        if (optionalRequest.isEmpty()) {
            logger.warn(" Test request does not exist for ID: {}", testRequestId);
            return;
        }
        TestRequest testRequest = optionalRequest.get();

        // Check if TestType exists
        Optional<TestType> optionalTestType = testTypeRepository.findById(testRequest.getTestTypeId());
        if (optionalTestType.isEmpty()) {
            logger.warn(" Test type does not exist for test request ID: {}", testRequestId);
            return;
        }
        TestType testType = optionalTestType.get();
        // Checking if there is a technician available
        Optional<Technician> optionalTech = technicianRepository.findFirstByAvailableTrue();
        if (optionalTech.isEmpty()) {
            logger.info(" There are no available technicians for the test ID: {}", testRequestId);
            return;
        }

        Technician tech = optionalTech.get();

        try {
            // The technician is busy.
            tech.setAvailable(false);
            technicianRepository.save(tech);

            testRequest.setAssignedTechnicianId(tech.getId());
            testRequest.setStatus(TestStatus.PROCESSING);
            testRequestRepository.save(testRequest);

            // Reagent check
            if (tech.getAvailableReagents() < testType.getReagentUnits()) {
                tech.setReplacingReagents(true);
                technicianRepository.save(tech);
                Thread.sleep(reagentReplacementTimeMinutes * 60 * 1000L);
                tech.setAvailableReagents(500);
                tech.setReplacingReagents(false);
            }

            tech.setAvailableReagents(tech.getAvailableReagents() - testType.getReagentUnits());
            technicianRepository.save(tech);

            // Test processing simulation
            Thread.sleep(testType.getProcessingTimeSeconds() * 1000L);

            testRequest.setStatus(TestStatus.COMPLETED);
            testRequest.setCompletedAt(LocalDateTime.now());
            testRequestRepository.save(testRequest);

            tech.setAvailable(true);
            technicianRepository.save(tech);
            logger.info("Test ID {} was processed successfully", testRequestId);


        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Processing of test ID {} was aborted", testRequestId, e);
        } catch (Exception e) {
            logger.error(" Error processing test ID {}: {}", testRequestId, e.getMessage(), e);
        }
    }
}
