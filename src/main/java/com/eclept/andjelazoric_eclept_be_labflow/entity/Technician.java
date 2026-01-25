package com.eclept.andjelazoric_eclept_be_labflow.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Technician {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String machineName;
    private boolean available;
    private boolean replacingReagents;
    private int availableReagents;

    @OneToMany(
            mappedBy = "assignedTechnician",
            fetch = FetchType.LAZY
    )
    @Builder.Default
    private List<TestRequest> assignedTests = new ArrayList<>();

    public Technician(Long id, String name, String machineName, boolean available, boolean replacingReagents, int availableReagents) {
        this.id = id;
        this.name = name;
        this.machineName = machineName;
        this.available = available;
        this.replacingReagents = replacingReagents;
        this.availableReagents = availableReagents;
    }

}
