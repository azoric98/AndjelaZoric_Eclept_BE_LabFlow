package com.eclept.andjelazoric_eclept_be_labflow.service;

import com.eclept.andjelazoric_eclept_be_labflow.dto.TestTypeDTO;
import com.eclept.andjelazoric_eclept_be_labflow.entity.TestType;
import com.eclept.andjelazoric_eclept_be_labflow.repository.TestTypeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TestTypeService {

    private final TestTypeRepository testTypeRepository;

    public TestTypeService(TestTypeRepository testTypeRepository) {
        this.testTypeRepository = testTypeRepository;
    }

    public TestType create(TestTypeDTO dto) {
        TestType type = new TestType();
        type.setName(dto.getName());
        type.setReagentUnits(dto.getReagentUnits());
        type.setProcessingTimeSeconds(dto.getProcessingTimeSeconds());
        return testTypeRepository.save(type);
    }

    public TestType update(Long id, TestTypeDTO dto) {
        TestType type = testTypeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Test type not found"));
        if (dto.getName() != null) {
            type.setName(dto.getName());
        }

        if (dto.getReagentUnits() != null) {
            type.setReagentUnits(dto.getReagentUnits());
        }

        if (dto.getProcessingTimeSeconds() != null) {
            type.setProcessingTimeSeconds(dto.getProcessingTimeSeconds());
        }

        return testTypeRepository.save(type);
    }

    public void delete(Long id) {
        if (!testTypeRepository.existsById(id)) {
            throw new IllegalArgumentException("Test type not found");
        }
        testTypeRepository.deleteById(id);
    }

    public List<TestType> findAll() {
        return testTypeRepository.findAll();
    }
}

