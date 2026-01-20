package com.eclept.andjelazoric_eclept_be_labflow.repository;

import com.eclept.andjelazoric_eclept_be_labflow.entity.TestRequest;
import com.eclept.andjelazoric_eclept_be_labflow.enums.TestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface TestRequestRepository extends JpaRepository<TestRequest, Long> {

    long countByStatusIn(Collection<TestStatus> statuses);
    
    Optional<TestRequest> findFirstByStatusOrderByReceivedAt(TestStatus status);


}
