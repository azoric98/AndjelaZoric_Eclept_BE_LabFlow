package com.eclept.andjelazoric_eclept_be_labflow.dto;

import lombok.Builder;
import lombok.Data;
@Data
@Builder
public class TestRequestDTO {

    private Long testTypeId;
    private boolean walkIn;
}
