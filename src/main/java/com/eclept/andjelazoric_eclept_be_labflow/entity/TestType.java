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
public class TestType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private int processingTimeSeconds;
    private int reagentUnits;
    @OneToMany(
            mappedBy = "testType",
            fetch = FetchType.LAZY
    )
    private List<TestRequest> testRequests = new ArrayList<>();

    public TestType(Long id, String name, int processingTimeSeconds, int reagentUnits) {
        this.id = id;
        this.name = name;
        this.processingTimeSeconds = processingTimeSeconds;
        this.reagentUnits = reagentUnits;
    }

}
