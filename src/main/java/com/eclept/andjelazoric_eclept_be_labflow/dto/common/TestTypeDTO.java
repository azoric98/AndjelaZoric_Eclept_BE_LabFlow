package com.eclept.andjelazoric_eclept_be_labflow.dto.common;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestTypeDTO {

    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Reagent units required")
    @Min(value = 1, message = "Reagent units must be at least 1")
    private Integer reagentUnits;

    @NotNull(message = "Processing time required")
    @Min(value = 1, message = "Processing time must be at least 1 second")
    private Integer processingTimeSeconds;
}
