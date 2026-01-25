package com.eclept.andjelazoric_eclept_be_labflow.integration;

import com.eclept.andjelazoric_eclept_be_labflow.TestBase;
import com.eclept.andjelazoric_eclept_be_labflow.entity.TestRequest;
import com.eclept.andjelazoric_eclept_be_labflow.entity.TestType;
import com.eclept.andjelazoric_eclept_be_labflow.enums.TestStatus;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
class TestProcessingIntegrationTest extends TestBase {
    @Value("${labflow.admin.api-key}")
    private String adminApiKey;
    private static final String ADMIN_HEADER = "X-ADMIN-KEY";

    private static final String CREATE_TEST_TYPE_BODY = """
            {
              "name": "PCR COVID",
              "reagentUnits": 3,
              "processingTimeSeconds": 10
            }
            """;

    private String createTestRequestJson(Long testTypeId, boolean walkIn) {
        return """
                {
                  "testTypeId": %d,
                  "walkIn": %b
                }
                """.formatted(testTypeId, walkIn);
    }

    @Test
    void shouldProcessTestRequestCompleteFlow() throws Exception {
        TestType testType = createTestType("PCR COVID", 10, 10);

        Long testTypeId = testType.getId();
        log.info("Created TestType with id={}", testTypeId);

        String testRequestJson = createTestRequestJson(testTypeId, false);

        log.info("Performing request to /api/tests");
        mockMvc.perform(post("/api/tests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(testRequestJson))
                .andExpect(status().isOk());
        log.info("Api test successfully executed, wait 30 sec so Rabbit listener can finish its job.");
        Thread.sleep(30_000);
        log.info("Perform addition assertations.");
        //Get all tests from the database, since the database is clean,
        // there is only 1 that was added through the api above
        List<TestRequest> tests = testRequestRepository.findAll();
        // Database is not empty and it has 1 test
        Assertions.assertFalse(tests.isEmpty());
        Assertions.assertEquals(1, tests.size());
        TestRequest testRequest = tests.get(0);

        Assertions.assertEquals(TestStatus.COMPLETED, testRequest.getStatus());

    }

    @Test
    void shouldCreateTestType_whenAdminKeyProvided() throws Exception {
        mockMvc.perform(post("/api/admin/test-types")
                        .header(ADMIN_HEADER, adminApiKey)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CREATE_TEST_TYPE_BODY))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    void shouldReturnForbidden_whenAdminKeyIsMissing() throws Exception {
        mockMvc.perform(post("/api/admin/test-types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CREATE_TEST_TYPE_BODY))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturnForbidden_whenAdminKeyIsInvalid() throws Exception {
        mockMvc.perform(post("/api/admin/test-types")
                        .header(ADMIN_HEADER, "wrong-key")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CREATE_TEST_TYPE_BODY))
                .andExpect(status().isForbidden());
    }

    private void fillQueue(TestType testType, long activeCount) {
        var statuses = List.of(TestStatus.RECEIVED, TestStatus.PROCESSING);
        for (int i = 0; i < activeCount; i++) {
            TestRequest request = new TestRequest();
            request.setTestType(testType);
            request.setWalkIn(true); // walkIn=true, da ne aktivira bolnicu
            request.setStatus(statuses.get(i % 2));
            testRequestRepository.save(request);
        }
    }

    @Test
    void shouldThrowQueueFullException_whenHospitalQueueFull() throws Exception {
        TestType testType = createTestType("PCR Test", 10, 10);

        fillQueue(testType, 20);

        // Make all technicians unavailable
        makeAllTechniciansUnavailable();

        String hospitalRequestJson = createTestRequestJson(testType.getId(), false);
        // Send request and expect 429
        mockMvc.perform(post("/api/tests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(hospitalRequestJson))
                .andDo(print())
                .andExpect(status().isTooManyRequests());
    }

    @Test
    void shouldAcceptWalkInEvenWhenQueueFull() throws Exception {
        TestType testType = createTestType("PCR Test", 10, 10);

        fillQueue(testType, 20);

        makeAllTechniciansUnavailable();

        String walkInRequestJson = createTestRequestJson(testType.getId(), true);

        // Send request and expect 200 OK
        mockMvc.perform(post("/api/tests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(walkInRequestJson))
                .andDo(print())
                .andExpect(status().isOk());

        // Verify request is saved
        List<TestRequest> tests = testRequestRepository.findAll();
        Assertions.assertFalse(tests.isEmpty());
        Assertions.assertEquals(21, tests.size());
    }

    private void makeAllTechniciansUnavailable() {
        technicianRepository.findAll().forEach(t -> {
            t.setAvailable(false);
        technicianRepository.save(t);
        });
    }

    private TestType createTestType(String testName, int reagentUnits, int processingTimeSeconds) {
        TestType testType = new TestType();
        testType.setName(testName);
        testType.setReagentUnits(reagentUnits);
        testType.setProcessingTimeSeconds(processingTimeSeconds);
        testType = testTypeRepository.save(testType);
        return testType;
    }

}
