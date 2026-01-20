package com.eclept.andjelazoric_eclept_be_labflow.dto;

import com.eclept.andjelazoric_eclept_be_labflow.enums.TestStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TestNotificationDTO {
    private Long testRequestId;
    private TestStatus status;
}
