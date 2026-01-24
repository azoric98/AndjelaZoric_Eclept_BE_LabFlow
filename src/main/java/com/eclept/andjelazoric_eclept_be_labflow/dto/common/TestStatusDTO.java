package com.eclept.andjelazoric_eclept_be_labflow.dto.common;

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
public class TestStatusDTO {
    private Long testRequestId;
    private String testName;
    private String technicianName;
    private TestStatus status;
    private LocalDateTime receivedAt;
    private LocalDateTime completedAt;
}
