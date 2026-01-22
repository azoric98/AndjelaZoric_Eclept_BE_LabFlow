package com.eclept.andjelazoric_eclept_be_labflow.dto;

import com.eclept.andjelazoric_eclept_be_labflow.enums.TestStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestRequestResponseDTO {
    private Long id;
    private Long testTypeId;
    private boolean walkIn;
    private String testName;
    private String assignedTechnicianName;
    private TestStatus status;
    private LocalDateTime receivedAt;
    private LocalDateTime completedAt;
}
