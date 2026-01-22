package com.eclept.andjelazoric_eclept_be_labflow.service;

import com.eclept.andjelazoric_eclept_be_labflow.dto.common.TestTypeDTO;

import java.util.List;

public interface TestTypeService {
     TestTypeDTO create(TestTypeDTO dto);
     TestTypeDTO update(Long id, TestTypeDTO dto);
    void delete(Long id);
    List<TestTypeDTO> findAll();

}
