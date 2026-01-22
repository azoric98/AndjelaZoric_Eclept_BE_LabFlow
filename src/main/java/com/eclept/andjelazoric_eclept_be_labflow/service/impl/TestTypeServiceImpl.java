package com.eclept.andjelazoric_eclept_be_labflow.service.impl;

import com.eclept.andjelazoric_eclept_be_labflow.dto.common.TestTypeDTO;
import com.eclept.andjelazoric_eclept_be_labflow.entity.TestType;
import com.eclept.andjelazoric_eclept_be_labflow.mapper.TestTypeMapper;
import com.eclept.andjelazoric_eclept_be_labflow.repository.TestTypeRepository;
import com.eclept.andjelazoric_eclept_be_labflow.service.TestTypeService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TestTypeServiceImpl implements TestTypeService {

    private final TestTypeRepository testTypeRepository;
    private final TestTypeMapper testTypeMapper;

    public TestTypeServiceImpl(TestTypeRepository testTypeRepository, TestTypeMapper testTypeMapper) {
        this.testTypeRepository = testTypeRepository;
        this.testTypeMapper = testTypeMapper;
    }

    public TestTypeDTO create(TestTypeDTO dto) {
        TestType type = testTypeMapper.toEntity(dto);
        TestType saved = testTypeRepository.save(type);
        return testTypeMapper.toResponseDTO(saved);
    }

    public TestTypeDTO update(Long id, TestTypeDTO dto) {
        TestType type = testTypeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Test type not found"));
        if (dto.getName() != null) type.setName(dto.getName());
        if (dto.getReagentUnits() != null) type.setReagentUnits(dto.getReagentUnits());
        if (dto.getProcessingTimeSeconds() != null) type.setProcessingTimeSeconds(dto.getProcessingTimeSeconds());
        TestType updated = testTypeRepository.save(type);
        return testTypeMapper.toResponseDTO(updated);    }

    public void delete(Long id) {
        if (!testTypeRepository.existsById(id)) {
            throw new IllegalArgumentException("Test type not found");
        }
        testTypeRepository.deleteById(id);
    }

    public List<TestTypeDTO> findAll() {
        return testTypeRepository.findAll()
                .stream()
                .map(testTypeMapper::toResponseDTO)
                .toList();
    }
}

