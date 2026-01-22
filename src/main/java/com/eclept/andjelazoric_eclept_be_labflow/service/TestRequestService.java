package com.eclept.andjelazoric_eclept_be_labflow.service;

import com.eclept.andjelazoric_eclept_be_labflow.dto.request.TestRequestDTO;
import com.eclept.andjelazoric_eclept_be_labflow.dto.response.TestRequestResponseDTO;
import com.eclept.andjelazoric_eclept_be_labflow.dto.common.TestStatusDTO;

import java.util.List;

public interface TestRequestService {
    void submitTest(TestRequestDTO dto);
    TestStatusDTO getTestStatus(Long testRequestId);
    List<TestRequestResponseDTO> findAllTestRequests();
}
