package com.eclept.andjelazoric_eclept_be_labflow.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestTypeDTO {

    private String name;
    private Integer reagentUnits;
    private Integer processingTimeSeconds;
}
