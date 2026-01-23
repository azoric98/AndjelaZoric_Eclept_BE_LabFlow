package com.eclept.andjelazoric_eclept_be_labflow.service;

import com.eclept.andjelazoric_eclept_be_labflow.dto.common.TechnicianDTO;
import com.eclept.andjelazoric_eclept_be_labflow.entity.Technician;
import com.eclept.andjelazoric_eclept_be_labflow.entity.TestType;

import java.util.Optional;

public interface TechnicianService {

    Optional<Technician> findAvailableTechnician(Long testRequestId);
    Technician save(Technician tech);
    void ensureSufficientReagents(Technician tech, TestType testType) throws InterruptedException;
    Optional<TechnicianDTO> findTechnicianById(Long technicianId) ;

    }
