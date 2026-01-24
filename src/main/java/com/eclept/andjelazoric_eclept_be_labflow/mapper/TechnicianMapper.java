package com.eclept.andjelazoric_eclept_be_labflow.mapper;

import com.eclept.andjelazoric_eclept_be_labflow.dto.common.TechnicianDTO;
import com.eclept.andjelazoric_eclept_be_labflow.entity.Technician;
import org.springframework.stereotype.Component;

@Component
public class TechnicianMapper {
    public TechnicianDTO toResponseDTO(Technician entity) {
        if (entity == null) {
            return null;
        }
        return TechnicianDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .machineName(entity.getMachineName())
                .available(entity.isAvailable())
                .replacingReagents(entity.isReplacingReagents())
                .availableReagents(entity.getAvailableReagents())
                .build();
    }

    public Technician toEntity(
            TechnicianDTO dto
    ) {
        return Technician.builder()
                .id(dto.getId())
                .name(dto.getName())
                .machineName(dto.getMachineName())
                .available(dto.isAvailable())
                .replacingReagents(dto.isReplacingReagents())
                .availableReagents(dto.getAvailableReagents())
                .build();
    }
}
