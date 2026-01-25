package com.eclept.andjelazoric_eclept_be_labflow.unit;

import com.eclept.andjelazoric_eclept_be_labflow.dto.common.TestTypeDTO;
import com.eclept.andjelazoric_eclept_be_labflow.entity.TestType;
import com.eclept.andjelazoric_eclept_be_labflow.mapper.TestTypeMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TestTypeMapperTest {

    private TestTypeMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new TestTypeMapper();
    }

    @Test
    void toEntity_ShouldMapDTOToEntity() {
        TestTypeDTO dto = TestTypeDTO.builder()
                .name("Blood Test")
                .processingTimeSeconds(60)
                .reagentUnits(20)
                .build();

        TestType entity = mapper.toEntity(dto);

        assertThat(entity).isNotNull();
        assertThat(entity.getName()).isEqualTo("Blood Test");
        assertThat(entity.getProcessingTimeSeconds()).isEqualTo(60);
        assertThat(entity.getReagentUnits()).isEqualTo(20);
        assertThat(entity.getId()).isNull();
    }

    @Test
    void toResponseDTO_ShouldMapEntityToDTO() {
        TestType entity = new TestType();
        entity.setId(1L);
        entity.setName("Urine Test");
        entity.setProcessingTimeSeconds(90);
        entity.setReagentUnits(30);

        TestTypeDTO dto = mapper.toResponseDTO(entity);

        assertThat(dto).isNotNull();
        assertThat(dto.getName()).isEqualTo("Urine Test");
        assertThat(dto.getProcessingTimeSeconds()).isEqualTo(90);
        assertThat(dto.getReagentUnits()).isEqualTo(30);
    }

    @Test
    void toEntity_ShouldHandleNullValues() {
        TestTypeDTO dto = TestTypeDTO.builder()
                .name(null)
                .processingTimeSeconds(1)
                .reagentUnits(1)
                .build();

        TestType entity = mapper.toEntity(dto);

        assertThat(entity).isNotNull();
        assertThat(entity.getName()).isNull();
        assertThat(entity.getProcessingTimeSeconds()).isEqualTo(1);
        assertThat(entity.getReagentUnits()).isEqualTo(1);
    }

    @Test
    void toResponseDTO_ShouldHandleNullValues() {
        TestType entity = new TestType();
        entity.setId(1L);
        entity.setName(null);
        entity.setProcessingTimeSeconds(1);
        entity.setReagentUnits(1);

        TestTypeDTO dto = mapper.toResponseDTO(entity);

        assertThat(dto).isNotNull();
        assertThat(dto.getName()).isNull();
        assertThat(dto.getProcessingTimeSeconds()).isEqualTo(1);
        assertThat(dto.getReagentUnits()).isEqualTo(1);
    }

    @Test
    void roundTrip_ShouldPreserveData() {
        TestTypeDTO originalDTO = TestTypeDTO.builder()
                .name("PCR Test")
                .processingTimeSeconds(180)
                .reagentUnits(100)
                .build();

        TestType entity = mapper.toEntity(originalDTO);
        TestTypeDTO resultDTO = mapper.toResponseDTO(entity);

        assertThat(resultDTO.getName()).isEqualTo(originalDTO.getName());
        assertThat(resultDTO.getProcessingTimeSeconds()).isEqualTo(originalDTO.getProcessingTimeSeconds());
        assertThat(resultDTO.getReagentUnits()).isEqualTo(originalDTO.getReagentUnits());
    }
}
