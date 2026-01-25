package com.eclept.andjelazoric_eclept_be_labflow.unit;

import com.eclept.andjelazoric_eclept_be_labflow.dto.common.TestTypeDTO;
import com.eclept.andjelazoric_eclept_be_labflow.entity.TestType;
import com.eclept.andjelazoric_eclept_be_labflow.mapper.TestTypeMapper;
import com.eclept.andjelazoric_eclept_be_labflow.repository.TestTypeRepository;
import com.eclept.andjelazoric_eclept_be_labflow.service.impl.TestTypeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TestTypeServiceImplTest {

    @Mock
    private TestTypeRepository testTypeRepository;

    @Mock
    private TestTypeMapper testTypeMapper;

    @InjectMocks
    private TestTypeServiceImpl testTypeService;

    private TestType testType;
    private TestTypeDTO testTypeDTO;

    @BeforeEach
    void setUp() {
        testType = new TestType();
        testType.setId(1L);
        testType.setName("Blood Test");
        testType.setProcessingTimeSeconds(60);
        testType.setReagentUnits(20);

        testTypeDTO = TestTypeDTO.builder()
                .name("Blood Test")
                .processingTimeSeconds(60)
                .reagentUnits(20)
                .build();
    }

    @Test
    void create_ShouldReturnCreatedTestType() {
        when(testTypeMapper.toEntity(testTypeDTO)).thenReturn(testType);
        when(testTypeRepository.save(testType)).thenReturn(testType);
        when(testTypeMapper.toResponseDTO(testType)).thenReturn(testTypeDTO);

        TestTypeDTO result = testTypeService.create(testTypeDTO);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Blood Test");
        assertThat(result.getProcessingTimeSeconds()).isEqualTo(60);
        assertThat(result.getReagentUnits()).isEqualTo(20);
        verify(testTypeRepository, times(1)).save(testType);
    }

    @Test
    void update_ShouldReturnUpdatedTestType_WhenTestTypeExists() {
        TestTypeDTO updateDTO = TestTypeDTO.builder()
                .name("Updated Blood Test")
                .processingTimeSeconds(90)
                .reagentUnits(30)
                .build();

        when(testTypeRepository.findById(1L)).thenReturn(Optional.of(testType));
        when(testTypeRepository.save(any(TestType.class))).thenReturn(testType);
        when(testTypeMapper.toResponseDTO(any(TestType.class))).thenReturn(updateDTO);

        TestTypeDTO result = testTypeService.update(1L, updateDTO);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Updated Blood Test");
        verify(testTypeRepository, times(1)).findById(1L);
        verify(testTypeRepository, times(1)).save(any(TestType.class));
    }

    @Test
    void update_ShouldThrowException_WhenTestTypeDoesNotExist() {
        when(testTypeRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> testTypeService.update(999L, testTypeDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Test type not found");

        verify(testTypeRepository, times(1)).findById(999L);
        verify(testTypeRepository, never()).save(any());
    }

    @Test
    void delete_ShouldDeleteTestType_WhenExists() {
        when(testTypeRepository.existsById(1L)).thenReturn(true);
        doNothing().when(testTypeRepository).deleteById(1L);

        testTypeService.delete(1L);

        verify(testTypeRepository, times(1)).existsById(1L);
        verify(testTypeRepository, times(1)).deleteById(1L);
    }

    @Test
    void delete_ShouldThrowException_WhenTestTypeDoesNotExist() {
        when(testTypeRepository.existsById(999L)).thenReturn(false);

        assertThatThrownBy(() -> testTypeService.delete(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Test type not found");

        verify(testTypeRepository, times(1)).existsById(999L);
        verify(testTypeRepository, never()).deleteById(any());
    }

    @Test
    void findAll_ShouldReturnAllTestTypes() {
        TestType testType2 = new TestType();
        testType2.setId(2L);
        testType2.setName("Urine Test");
        testType2.setProcessingTimeSeconds(90);
        testType2.setReagentUnits(30);

        TestTypeDTO dto2 = TestTypeDTO.builder()
                .name("Urine Test")
                .processingTimeSeconds(90)
                .reagentUnits(30)
                .build();

        when(testTypeRepository.findAll()).thenReturn(Arrays.asList(testType, testType2));
        when(testTypeMapper.toResponseDTO(testType)).thenReturn(testTypeDTO);
        when(testTypeMapper.toResponseDTO(testType2)).thenReturn(dto2);

        List<TestTypeDTO> result = testTypeService.findAll();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Blood Test");
        assertThat(result.get(1).getName()).isEqualTo("Urine Test");
        verify(testTypeRepository, times(1)).findAll();
    }

    @Test
    void findById_ShouldReturnTestType_WhenExists() {
        when(testTypeRepository.findById(1L)).thenReturn(Optional.of(testType));
        when(testTypeMapper.toResponseDTO(testType)).thenReturn(testTypeDTO);

        Optional<TestTypeDTO> result = testTypeService.findById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Blood Test");
        verify(testTypeRepository, times(1)).findById(1L);
    }

    @Test
    void findById_ShouldReturnEmpty_WhenDoesNotExist() {
        when(testTypeRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<TestTypeDTO> result = testTypeService.findById(999L);

        assertThat(result).isEmpty();
        verify(testTypeRepository, times(1)).findById(999L);
    }
}
