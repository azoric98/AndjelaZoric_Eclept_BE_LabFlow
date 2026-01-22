package com.eclept.andjelazoric_eclept_be_labflow.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TestTypeDTO {

    private String name;
    private Integer reagentUnits;
    private Integer processingTimeSeconds;
}
