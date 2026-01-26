package com.eclept.andjelazoric_eclept_be_labflow.service.impl;

import com.eclept.andjelazoric_eclept_be_labflow.dto.common.TestTypeDTO;
import com.eclept.andjelazoric_eclept_be_labflow.entity.TestType;
import com.eclept.andjelazoric_eclept_be_labflow.exception.LabFlowException;
import com.eclept.andjelazoric_eclept_be_labflow.mapper.TestTypeMapper;
import com.eclept.andjelazoric_eclept_be_labflow.repository.TestTypeRepository;
import com.eclept.andjelazoric_eclept_be_labflow.service.TestTypeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class TestTypeServiceImpl implements TestTypeService {

    private final TestTypeRepository testTypeRepository;
    private final TestTypeMapper testTypeMapper;


    public TestTypeServiceImpl(TestTypeRepository testTypeRepository, TestTypeMapper testTypeMapper) {
        this.testTypeRepository = testTypeRepository;
        this.testTypeMapper = testTypeMapper;
    }

    @Override
    public TestTypeDTO create(TestTypeDTO dto) {
        List<String> errors = new ArrayList<>();

        if (dto.getName() == null || dto.getName().isBlank()) errors.add("Test type name must not be empty");
        if (dto.getReagentUnits() == null || dto.getReagentUnits() <= 0) errors.add("Reagent units must be positive");
        if (dto.getProcessingTimeSeconds() == null || dto.getProcessingTimeSeconds() <= 0) errors.add("Processing time must be positive");

        if (!errors.isEmpty()) {
            throw new LabFlowException(String.join("; ", errors));
        }

        TestType type = testTypeMapper.toEntity(dto);
        TestType saved = testTypeRepository.save(type);
        log.info("Created new TestType: id={}, name={}", saved.getId(), saved.getName());
        return testTypeMapper.toResponseDTO(saved);
    }

    @Override
    public TestTypeDTO update(Long id, TestTypeDTO dto) {
        TestType type = testTypeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Test type not found"));
        if (dto.getName() != null) type.setName(dto.getName());
        if (dto.getReagentUnits() != null) type.setReagentUnits(dto.getReagentUnits());
        if (dto.getProcessingTimeSeconds() != null) type.setProcessingTimeSeconds(dto.getProcessingTimeSeconds());
        TestType updated = testTypeRepository.save(type);
        return testTypeMapper.toResponseDTO(updated);
    }

    @Override
    public void delete(Long id) {
        if (id == null || id <= 0) {
            log.warn("Attempted to delete TestType with invalid id={}", id);
            throw new LabFlowException("Test type ID must be positive and non-null");
        }

        if (!testTypeRepository.existsById(id)) {
            log.warn("Attempted to delete non-existent TestType id={}", id);
            throw new IllegalArgumentException("Test type not found");
        }
        testTypeRepository.deleteById(id);
    }

    @Override
    public List<TestTypeDTO> findAll() {
        return testTypeRepository.findAll()
                .stream()
                .map(testTypeMapper::toResponseDTO)
                .toList();
    }

    @Override
    public Optional<TestTypeDTO> findById(Long id) {
        if (id == null || id <= 0) {
            log.warn("Attempted to find TestType with invalid id={}", id);
            return Optional.empty();
        }
        try {
            return testTypeRepository.findById(id)
                    .map(testTypeMapper::toResponseDTO);
        } catch (Exception e) {
            log.error("Failed to find TestType with id {}", id, e);
            return Optional.empty();
        }
    }
}

