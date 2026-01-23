package com.eclept.andjelazoric_eclept_be_labflow.integration;

import com.eclept.andjelazoric_eclept_be_labflow.entity.TestRequest;
import com.eclept.andjelazoric_eclept_be_labflow.entity.Technician;
import com.eclept.andjelazoric_eclept_be_labflow.entity.TestType;
import com.eclept.andjelazoric_eclept_be_labflow.processor.TestProcessor;
import com.eclept.andjelazoric_eclept_be_labflow.repository.TestRequestRepository;
import com.eclept.andjelazoric_eclept_be_labflow.repository.TechnicianRepository;
import com.eclept.andjelazoric_eclept_be_labflow.repository.TestTypeRepository;
import com.eclept.andjelazoric_eclept_be_labflow.service.TechnicianService;
import com.eclept.andjelazoric_eclept_be_labflow.service.TestRequestService;
import com.eclept.andjelazoric_eclept_be_labflow.testutils.TechnicianTestData;
import com.eclept.andjelazoric_eclept_be_labflow.testutils.TestTypeTestData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static com.eclept.andjelazoric_eclept_be_labflow.enums.TestStatus.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
class TestProcessingIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @Container
    static RabbitMQContainer rabbitMQ = new RabbitMQContainer("rabbitmq:3.11-management");

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");

        registry.add("spring.rabbitmq.host", rabbitMQ::getHost);
        registry.add("spring.rabbitmq.port", rabbitMQ::getAmqpPort);
    }

    @Autowired
    private TechnicianRepository technicianRepository;

    @Autowired
    private TestRequestRepository testRequestRepository;

    @Autowired
    private TestTypeRepository testTypeRepository;

    @Autowired
    private TestRequestService testRequestService;

    @Autowired
    private TechnicianService technicianService;

    @Autowired
    private TestProcessor processor;

    @Test
    void testTechnicianAssignment() {
        Technician tech = TechnicianTestData.availableWithFullReagents();
        technicianRepository.save(tech);
        TestType testType= TestTypeTestData.bloodTest();
        testType = testTypeRepository.save(testType);

        TestRequest request = new TestRequest();
        request.setStatus(RECEIVED);
        request.setReceivedAt(LocalDateTime.now());
        request.setTestType(testType);
        testRequestRepository.save(request);

        assertThat(technicianRepository.findById(tech.getId())).isPresent();
        assertThat(testRequestRepository.findById(request.getId())).isPresent();
    }

    @Test
    void testProcessingTimeDependsOnTestType() {
        Technician tech = TechnicianTestData.availableWithFullReagents();
        technicianRepository.save(tech);
        TestType testType= TestTypeTestData.bloodTest();
        testType = testTypeRepository.save(testType);

        LocalDateTime receivedAt = LocalDateTime.now();
        TestRequest request = new TestRequest();
        request.setStatus(RECEIVED);
        request.setReceivedAt(receivedAt);
        request.setTestType(testType);
        testRequestRepository.save(request);

        processor.processTest(request.getId());

        TestRequest processed = testRequestRepository.findById(request.getId()).orElseThrow();

        LocalDateTime expectedFinish = receivedAt.plusSeconds(testType.getProcessingTimeSeconds());
        assertThat(processed.getCompletedAt())
                .isCloseTo(expectedFinish, within(1, ChronoUnit.SECONDS));

        Technician updatedTech = technicianRepository.findById(tech.getId()).orElseThrow();
        assertThat(updatedTech.isAvailable()).isTrue();
    }

}
