package com.eclept.andjelazoric_eclept_be_labflow.dto;
import com.eclept.andjelazoric_eclept_be_labflow.enums.TestStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TestStatusDTO {
    private Long testRequestId;
    private String testName;
    private String technicianName;
    private TestStatus status;
    private LocalDateTime receivedAt;
    private LocalDateTime completedAt;
}
