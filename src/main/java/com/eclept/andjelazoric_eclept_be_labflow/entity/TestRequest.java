package com.eclept.andjelazoric_eclept_be_labflow.entity;

import com.eclept.andjelazoric_eclept_be_labflow.enums.TestStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "test_type_id", nullable = false)
    private TestType testType;
    @ManyToOne
    @JoinColumn(name = "assigned_technician_id")
    private Technician assignedTechnician;
    @Enumerated(EnumType.STRING)
    // RECEIVED, PROCESSING, COMPLETED, REJECTED
    private TestStatus status;
    // the patient walk-in or the hospital flag
    private boolean walkIn;
    private LocalDateTime receivedAt;
    private LocalDateTime completedAt;
}
