package com.eclept.andjelazoric_eclept_be_labflow.controller;

import com.eclept.andjelazoric_eclept_be_labflow.annotation.AdminOnly;
import com.eclept.andjelazoric_eclept_be_labflow.dto.common.TestTypeDTO;
import com.eclept.andjelazoric_eclept_be_labflow.service.impl.TestTypeServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/test-types")
public class LabAdminController {

    private final TestTypeServiceImpl testTypeService;

    public LabAdminController(TestTypeServiceImpl testTypeService) {
        this.testTypeService = testTypeService;
    }

    @AdminOnly
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new test type",
            description = "Adds a new test type to the system with name, reagent units, and processing time.")
    public TestTypeDTO create(@RequestBody TestTypeDTO dto) {
        return testTypeService.create(dto);
    }

    @AdminOnly
    @PutMapping("/{id}")
    @Operation(summary = "Update test type", description = "Updates an existing test type by ID")
    public TestTypeDTO update(@PathVariable Long id, @RequestBody TestTypeDTO dto) {
        return testTypeService.update(id, dto);
    }

    @AdminOnly
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a test type", description = "Deletes a test type by its ID if it exists.")
    public void delete(@PathVariable Long id) {
        testTypeService.delete(id);
    }

    @GetMapping
    @Operation(summary = "List all test types", description = "Returns all test types in the system")
    public List<TestTypeDTO> getAll() {
        return testTypeService.findAll();
    }
}
