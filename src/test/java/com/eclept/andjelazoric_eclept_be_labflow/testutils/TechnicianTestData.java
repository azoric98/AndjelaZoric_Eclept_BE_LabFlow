package com.eclept.andjelazoric_eclept_be_labflow.testutils;

import com.eclept.andjelazoric_eclept_be_labflow.entity.Technician;

public final class TechnicianTestData {

    private TechnicianTestData() {}

    public static Technician availableWithFullReagents() {
        return Technician.builder()
                .name("Technician 1")
                .available(true)
                .availableReagents(500)
                .replacingReagents(false)
                .build();
    }

    public static Technician availableLowReagents() {
        return Technician.builder()
                .name("Technician 2")
                .available(true)
                .availableReagents(50)
                .replacingReagents(false)
                .build();
    }

    public static Technician needsReagentReplacement() {
        return Technician.builder()
                .name("Technician 3")
                .available(true)
                .availableReagents(0)
                .replacingReagents(false)
                .build();
    }

    public static Technician busyTechnician() {
        return Technician.builder()
                .name("Technician 4")
                .available(false)
                .availableReagents(300)
                .replacingReagents(false)
                .build();
    }

    public static Technician replacingReagents() {
        return Technician.builder()
                .name("Technician 5")
                .available(false)
                .availableReagents(0)
                .replacingReagents(true)
                .build();
    }
}
