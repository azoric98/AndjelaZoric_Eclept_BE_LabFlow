package com.eclept.andjelazoric_eclept_be_labflow.unit;

import com.eclept.andjelazoric_eclept_be_labflow.dto.common.TechnicianDTO;
import com.eclept.andjelazoric_eclept_be_labflow.entity.Technician;
import com.eclept.andjelazoric_eclept_be_labflow.mapper.TechnicianMapper;
import com.eclept.andjelazoric_eclept_be_labflow.repository.TechnicianRepository;
import com.eclept.andjelazoric_eclept_be_labflow.service.impl.TechnicianServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TechnicianServiceImplTest {

    @Mock
    private TechnicianRepository repository;

    @Mock
    private TechnicianMapper mapper;

    @InjectMocks
    private TechnicianServiceImpl service;

    private Technician technician;

    @BeforeEach
    void setUp() {
        technician = new Technician();
        technician.setId(1L);
        technician.setName("Tech 1");
        technician.setMachineName("Machine 1");
        technician.setAvailable(true);
        technician.setReplacingReagents(false);
        technician.setAvailableReagents(500);

    }

    @Test
    void findAvailableTech_ShouldReturnTech_WhenAvailable() {
        when(repository.findFirstByAvailableTrue()).thenReturn(Optional.of(technician));

        Optional<Technician> result = service.findAvailableTechnician(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
        assertThat(result.get().getName()).isEqualTo("Tech 1");
        verify(repository, times(1)).findFirstByAvailableTrue();
    }

    @Test
    void findAvailableTechnician_ShouldReturnEmpty_WhenNoTechnicianAvailable() {
        when(repository.findFirstByAvailableTrue()).thenReturn(Optional.empty());

        Optional<Technician> result = service.findAvailableTechnician(1L);
        assertThat(result).isEmpty();
        verify(repository, times(1)).findFirstByAvailableTrue();

    }

    @Test
    void save_ShouldReturnSavedTechnician_WhenSuccessful() {
        when(repository.save(technician)).thenReturn(technician);

        Technician result = service.save(technician);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(repository, times(1)).save(technician);
    }

    @Test
    void save_ShouldReturnNull_WhenExceptionOccurs() {
        when(repository.save(technician)).thenThrow(new RuntimeException("Database error"));

        Technician result = service.save(technician);

        assertThat(result).isNull();
        verify(repository, times(1)).save(technician);
    }

    @Test
    void findTechnicianById_ShouldReturnDTO_WhenTechnicianExists() {
        TechnicianDTO dto = TechnicianDTO.builder()
                .name("Tech 1")
                .machineName("Machine 1")
                .available(true)
                .availableReagents(500)
                .build();

        when(repository.findById(1L)).thenReturn(Optional.of(technician));
        when(mapper.toResponseDTO(technician)).thenReturn(dto);

        Optional<TechnicianDTO> result = service
                .findTechnicianById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Tech 1");
        verify(repository, times(1)).findById(1L);
        verify(mapper, times(1)).toResponseDTO(technician);
    }

    @Test
    void findTechnicianById_ShouldReturnEmpty_WhenTechnicianDoesNotExist() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        Optional<TechnicianDTO> result = service.findTechnicianById(999L);

        assertThat(result).isEmpty();
        verify(repository, times(1)).findById(999L);
        verify(mapper, never()).toResponseDTO(any());
    }
}
