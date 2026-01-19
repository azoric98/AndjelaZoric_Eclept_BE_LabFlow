package com.eclept.andjelazoric_eclept_be_labflow.repository;

import com.eclept.andjelazoric_eclept_be_labflow.entity.TestRequest;
import com.eclept.andjelazoric_eclept_be_labflow.entity.TestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestRequestRepository extends JpaRepository<TestRequest, Long> {
    List<TestRequest> findByStatus(TestStatus status);
    List<TestRequest> findByWalkInTrue();
    long countByStatus(TestStatus status);


}
