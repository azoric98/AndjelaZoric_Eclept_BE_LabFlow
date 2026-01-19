package com.eclept.andjelazoric_eclept_be_labflow.repository;

import com.eclept.andjelazoric_eclept_be_labflow.entity.TestType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestTypeRepository extends JpaRepository<TestType, Long> { }

