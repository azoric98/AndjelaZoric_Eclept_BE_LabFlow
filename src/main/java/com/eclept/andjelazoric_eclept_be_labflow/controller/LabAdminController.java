package com.eclept.andjelazoric_eclept_be_labflow.controller;

import com.eclept.andjelazoric_eclept_be_labflow.dto.TestTypeDTO;
import com.eclept.andjelazoric_eclept_be_labflow.entity.TestType;
import com.eclept.andjelazoric_eclept_be_labflow.security.AdminOnly;
import com.eclept.andjelazoric_eclept_be_labflow.service.TestTypeService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/test-types")

public class LabAdminController {

    private final TestTypeService testTypeService;

    public LabAdminController(TestTypeService testTypeService) {
        this.testTypeService = testTypeService;
    }

    @AdminOnly
    @PostMapping
    public TestType create(@RequestBody TestTypeDTO dto) {
        return testTypeService.create(dto);
    }

    @AdminOnly
    @PutMapping("/{id}")
    public TestType update(@PathVariable Long id, @RequestBody TestTypeDTO dto) {
        return testTypeService.update(id, dto);
    }

    @AdminOnly
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        testTypeService.delete(id);
    }

    @GetMapping
    public List<TestType> getAll() {
        return testTypeService.findAll();
    }
}
