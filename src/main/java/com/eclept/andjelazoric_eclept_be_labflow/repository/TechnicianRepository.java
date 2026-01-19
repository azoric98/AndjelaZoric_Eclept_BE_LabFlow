package com.eclept.andjelazoric_eclept_be_labflow.repository;

import com.eclept.andjelazoric_eclept_be_labflow.entity.Technician;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TechnicianRepository extends JpaRepository<Technician, Long> {
    Optional<Technician> findFirstByAvailableTrue();
}
