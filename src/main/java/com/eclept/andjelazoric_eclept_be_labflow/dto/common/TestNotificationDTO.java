package com.eclept.andjelazoric_eclept_be_labflow.dto.common;

import com.eclept.andjelazoric_eclept_be_labflow.enums.TestStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestNotificationDTO {
    private Long testRequestId;
    private String testType;
    private LocalDateTime completedAt;
    private TestStatus status;
}
