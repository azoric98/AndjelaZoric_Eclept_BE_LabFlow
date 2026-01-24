package com.eclept.andjelazoric_eclept_be_labflow.service;

import com.eclept.andjelazoric_eclept_be_labflow.dto.common.TestStatusDTO;
import com.eclept.andjelazoric_eclept_be_labflow.dto.request.TestRequestDTO;
import com.eclept.andjelazoric_eclept_be_labflow.dto.response.TestResponseDTO;
import com.eclept.andjelazoric_eclept_be_labflow.entity.TestRequest;
import com.eclept.andjelazoric_eclept_be_labflow.enums.TestStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TestRequestService {
    void submitTest(TestRequestDTO dto);

    TestStatusDTO getTestStatus(Long testRequestId);

    List<TestResponseDTO> findAllTestRequests();

    TestResponseDTO save(TestResponseDTO dto);

    Optional<TestRequest> getProcessableTestRequest(Long testRequestId);

    void setProcessingStatus(TestRequest testRequest, LocalDateTime time, TestStatus status);

    void getFirstByStatusOrderByReceivedAt();
}
