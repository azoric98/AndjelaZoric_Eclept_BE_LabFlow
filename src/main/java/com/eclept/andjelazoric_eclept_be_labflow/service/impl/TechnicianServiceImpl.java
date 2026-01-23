package com.eclept.andjelazoric_eclept_be_labflow.service.impl;

import com.eclept.andjelazoric_eclept_be_labflow.dto.common.TechnicianDTO;
import com.eclept.andjelazoric_eclept_be_labflow.entity.Technician;
import com.eclept.andjelazoric_eclept_be_labflow.entity.TestType;
import com.eclept.andjelazoric_eclept_be_labflow.mapper.TechnicianMapper;
import com.eclept.andjelazoric_eclept_be_labflow.repository.TechnicianRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class TechnicianServiceImpl implements com.eclept.andjelazoric_eclept_be_labflow.service.TechnicianService {

    private final TechnicianRepository repository;
    private final TechnicianMapper mapper;
    private final Logger logger = LoggerFactory.getLogger(TechnicianServiceImpl.class);


    @Value(value = "${labflow.reagentReplacementTimeMinutes}")
    private int reagentReplacementTimeMinutes;

    public TechnicianServiceImpl(
            TechnicianRepository repository,
            TechnicianMapper mapper
    ) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Optional<Technician> findAvailableTechnician(Long testRequestId) {
        Optional<Technician> optionalTech = repository.findFirstByAvailableTrue();
        if (optionalTech.isEmpty()) {
            logger.info("No available technician for test ID {}, keeping it in queue", testRequestId);
        }
        return optionalTech;
    }

    @Override
    public Technician save(Technician tech) {
        try {
            return repository.save(tech);
        } catch (Exception e) {
            logger.error("Failed to save technician {}", tech.getId(), e);
            return null;
        }
    }

    @Override
    public void ensureSufficientReagents(Technician tech, TestType testType) throws InterruptedException {
            // Reagents check
            if (tech.getAvailableReagents() < testType.getReagentUnits()) {
                logger.info("""
                    {} does not have enough reagents
                    Available: {}
                    Needed: {}""",
                        tech.getName(), tech.getAvailableReagents(), testType.getReagentUnits());

                tech.setReplacingReagents(true);
                repository.saveAndFlush(tech);

                logger.info("{} is replacing reagents at {}", tech.getName(), LocalDateTime.now());
                    Thread.sleep(reagentReplacementTimeMinutes * 60 * 1000L);
                tech.setAvailableReagents(500);
                tech.setReplacingReagents(false);
                logger.info("{} finished replacing reagents at {}", tech.getName(), LocalDateTime.now());
            }

            tech.setAvailableReagents(tech.getAvailableReagents() - testType.getReagentUnits());
            repository.save(tech);

    }

    @Override
    public Optional<TechnicianDTO> findTechnicianById(Long technicianId) {
        try {
            return repository.findById(technicianId)
                    .map(mapper::toResponseDTO);
        } catch (Exception e) {
            logger.error("Failed to find technician by id {}", technicianId, e);
            return Optional.empty();
        }
    }

}

