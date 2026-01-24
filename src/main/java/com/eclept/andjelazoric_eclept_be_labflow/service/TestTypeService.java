package com.eclept.andjelazoric_eclept_be_labflow.service;

import com.eclept.andjelazoric_eclept_be_labflow.dto.common.TestTypeDTO;

import java.util.List;
import java.util.Optional;

public interface TestTypeService {
    TestTypeDTO create(TestTypeDTO dto);

    TestTypeDTO update(Long id, TestTypeDTO dto);

    void delete(Long id);

    List<TestTypeDTO> findAll();

    Optional<TestTypeDTO> findById(Long id);

}
