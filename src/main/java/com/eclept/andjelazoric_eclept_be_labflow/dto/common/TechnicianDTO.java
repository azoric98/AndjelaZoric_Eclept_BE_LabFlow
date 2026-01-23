package com.eclept.andjelazoric_eclept_be_labflow.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TechnicianDTO {
    private Long id;
    private String name;
    private String machineName;
    private boolean available;
    private boolean replacingReagents;
    private int availableReagents;
}
