package com.eclept.andjelazoric_eclept_be_labflow.mapper;

import com.eclept.andjelazoric_eclept_be_labflow.dto.common.TestTypeDTO;
import com.eclept.andjelazoric_eclept_be_labflow.entity.TestType;
import org.springframework.stereotype.Component;

@Component
public class TestTypeMapper {

    public TestType toEntity(TestTypeDTO dto) {
        return TestType.builder()
                .name(dto.getName())
                .reagentUnits(dto.getReagentUnits())
                .processingTimeSeconds(dto.getProcessingTimeSeconds())
                .build();
    }

    public TestTypeDTO toResponseDTO(TestType entity) {
        return TestTypeDTO.builder()
                .name(entity.getName())
                .reagentUnits(entity.getReagentUnits())
                .processingTimeSeconds(entity.getProcessingTimeSeconds())
                .build();
    }
}
