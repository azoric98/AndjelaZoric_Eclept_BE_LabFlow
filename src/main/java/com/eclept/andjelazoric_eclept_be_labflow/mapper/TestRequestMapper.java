package com.eclept.andjelazoric_eclept_be_labflow.mapper;

import com.eclept.andjelazoric_eclept_be_labflow.dto.request.TestRequestDTO;
import com.eclept.andjelazoric_eclept_be_labflow.dto.response.TestRequestResponseDTO;
import com.eclept.andjelazoric_eclept_be_labflow.entity.TestRequest;

import com.eclept.andjelazoric_eclept_be_labflow.entity.TestType;
import com.eclept.andjelazoric_eclept_be_labflow.enums.TestStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class TestRequestMapper {

    public TestRequest toEntity(TestRequestDTO dto, TestType testType) {
        return TestRequest.builder()
                .walkIn(dto.isWalkIn())
                .testType(testType)
                .status(TestStatus.RECEIVED)
                .receivedAt(LocalDateTime.now())
                .build();
    }

    public TestRequestResponseDTO toResponseDTO(TestRequest entity) {
        return TestRequestResponseDTO.builder()
                .id(entity.getId())
                .testTypeId(entity.getTestType().getId())
                .walkIn(entity.isWalkIn())
                .testName(entity.getTestType().getName())
                .assignedTechnicianName(
                        entity.getAssignedTechnician() != null
                                ? entity.getAssignedTechnician().getName()
                                : "Not assigned yet"
                )
                .status(entity.getStatus())
                .receivedAt(entity.getReceivedAt())
                .completedAt(entity.getCompletedAt())
                .build();
    }

}
