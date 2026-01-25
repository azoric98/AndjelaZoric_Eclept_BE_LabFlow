package com.eclept.andjelazoric_eclept_be_labflow.controller;

import com.eclept.andjelazoric_eclept_be_labflow.annotation.AdminOnly;
import com.eclept.andjelazoric_eclept_be_labflow.dto.common.TestStatusDTO;
import com.eclept.andjelazoric_eclept_be_labflow.dto.request.TestRequestDTO;
import com.eclept.andjelazoric_eclept_be_labflow.dto.response.TestResponseDTO;
import com.eclept.andjelazoric_eclept_be_labflow.service.impl.TestRequestServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/tests")
@Tag(name = "Test Requests", description = "Operations on test requests")
public class TestRequestController {

    private final TestRequestServiceImpl testRequestService;


    public TestRequestController(TestRequestServiceImpl testRequestService) {
        this.testRequestService = testRequestService;
    }

    @PostMapping
    @Operation(summary = "Submit a new test request", description = "Creates a new test request for a patient or hospital")
    public ResponseEntity<String> submitTest(@RequestBody TestRequestDTO dto) {
        testRequestService.submitTest(dto);
        return ResponseEntity.ok("Test accepted and sent for processing.");
    }

    @GetMapping("/{id}/status")
    @Operation(summary = "Get test request status", description = "Returns the current status of a test request by ID")
    public TestStatusDTO getStatus(@PathVariable("id") Long testRequestId) {
        return testRequestService.getTestStatus(testRequestId);
    }

    @GetMapping
    @AdminOnly
    @Operation(
            summary = "List all test requests",
            description = "Returns a paginated list of all test requests"
    )
    public Page<TestResponseDTO> getAll(
            @PageableDefault(page = 0, size = 20)
            Pageable pageable
    ) {
        return testRequestService.findAllTestRequests(pageable);
    }

}
