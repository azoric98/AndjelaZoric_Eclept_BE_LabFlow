package com.eclept.andjelazoric_eclept_be_labflow.dto;

import com.eclept.andjelazoric_eclept_be_labflow.enums.TestStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestNotificationDTO {
    private Long testRequestId;
    private TestStatus status;
}
