package com.eclept.andjelazoric_eclept_be_labflow.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestRequestDTO {

    @NotNull(message = "Test type ID is required")
    private Long testTypeId;

    @NotNull(message = "Walk-in flag is required")
    private boolean walkIn;
}
