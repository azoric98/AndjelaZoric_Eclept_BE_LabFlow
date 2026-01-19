package com.eclept.andjelazoric_eclept_be_labflow.entity;

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
    private Long testTypeId;
    private Long assignedTechnicianId;
    @Enumerated(EnumType.STRING)
    // RECEIVED, PROCESSING, COMPLETED, REJECTED
    private TestStatus status;
    // the patient walk-in or the hospital flag
    private boolean walkIn;
    private LocalDateTime receivedAt;
    private LocalDateTime completedAt;
}
