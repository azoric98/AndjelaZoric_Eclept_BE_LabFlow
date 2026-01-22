package com.eclept.andjelazoric_eclept_be_labflow.mapper;

import com.eclept.andjelazoric_eclept_be_labflow.dto.common.TestStatusDTO;
import com.eclept.andjelazoric_eclept_be_labflow.entity.TestRequest;
import org.springframework.stereotype.Component;

@Component
public class TestStatusMapper {
    public TestStatusDTO toDTO(TestRequest entity) {
        return TestStatusDTO.builder()
                .testRequestId(entity.getId())
                .testName(entity.getTestType().getName())
                .technicianName(
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
