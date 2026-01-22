package com.eclept.andjelazoric_eclept_be_labflow.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestRequestDTO {

    private Long testTypeId;
    private boolean walkIn;
}
