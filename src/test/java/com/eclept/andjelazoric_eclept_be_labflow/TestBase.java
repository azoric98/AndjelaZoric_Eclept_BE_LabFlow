package com.eclept.andjelazoric_eclept_be_labflow;

import com.eclept.andjelazoric_eclept_be_labflow.entity.Technician;
import com.eclept.andjelazoric_eclept_be_labflow.entity.TestType;
import com.eclept.andjelazoric_eclept_be_labflow.repository.TechnicianRepository;
import com.eclept.andjelazoric_eclept_be_labflow.repository.TestRequestRepository;
import com.eclept.andjelazoric_eclept_be_labflow.repository.TestTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Testcontainers
public class TestBase {
    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected TechnicianRepository technicianRepository;

    @Autowired
    protected TestRequestRepository testRequestRepository;

    @Autowired
    protected TestTypeRepository testTypeRepository;
    private static final int INITIAL_REAGENTS = 500;

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

    @BeforeEach
     void setUpTestData() {
        testRequestRepository.deleteAll();
        technicianRepository.deleteAll();
        testTypeRepository.deleteAll();

            testTypeRepository.save(new TestType(null, "Blood Test", 60, 20));
            testTypeRepository.save(new TestType(null, "Urine Test", 90, 30));
            testTypeRepository.save(new TestType(null, "PCR Test", 180, 100));
            testTypeRepository.save(new TestType(null, "Allergy Panel", 240, 150));


        if (technicianRepository.count() == 0) {
            for (int i = 1; i <= 5; i++) {
                technicianRepository.save(new Technician(null,
                        "Technician " + i,
                        "Machine " + i,
                        true,
                        false,
                        INITIAL_REAGENTS));
            }
        }
    }
}
