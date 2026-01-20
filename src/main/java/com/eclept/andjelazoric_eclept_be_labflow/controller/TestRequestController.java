package com.eclept.andjelazoric_eclept_be_labflow.controller;

import com.eclept.andjelazoric_eclept_be_labflow.dto.TestRequestDTO;
import com.eclept.andjelazoric_eclept_be_labflow.dto.TestStatusDTO;
import com.eclept.andjelazoric_eclept_be_labflow.service.TestRequestService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tests")
public class TestRequestController {

    private final TestRequestService testRequestService;


    public TestRequestController(TestRequestService testRequestService) {
        this.testRequestService = testRequestService;
    }

    @PostMapping
    public ResponseEntity<String> submitTest(@RequestBody TestRequestDTO dto) {
        testRequestService.submitTest(dto);
        return ResponseEntity.ok("Test accepted and sent for processing.");
    }
    @GetMapping("/{id}/status")
    public TestStatusDTO getStatus(@PathVariable("id") Long testRequestId) {
        return testRequestService.getTestStatus(testRequestId);
    }
}
