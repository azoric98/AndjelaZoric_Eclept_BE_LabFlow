package com.eclept.andjelazoric_eclept_be_labflow.service;

import com.eclept.andjelazoric_eclept_be_labflow.dto.TestRequestDTO;
import com.eclept.andjelazoric_eclept_be_labflow.dto.TestStatusDTO;
import com.eclept.andjelazoric_eclept_be_labflow.entity.TestRequest;
import com.eclept.andjelazoric_eclept_be_labflow.entity.TestType;
import com.eclept.andjelazoric_eclept_be_labflow.enums.TestStatus;
import com.eclept.andjelazoric_eclept_be_labflow.exception.LabFlowException;
import com.eclept.andjelazoric_eclept_be_labflow.exception.QueueFullException;
import com.eclept.andjelazoric_eclept_be_labflow.repository.TechnicianRepository;
import com.eclept.andjelazoric_eclept_be_labflow.repository.TestRequestRepository;
import com.eclept.andjelazoric_eclept_be_labflow.repository.TestTypeRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TestRequestService {

    private final TestRequestRepository testRequestRepository;
    private final TechnicianRepository technicianRepository;
    private final TestRequestProducer producer;
    private final TestTypeRepository testTypeRepository;

    public TestRequestService(TestRequestRepository testRequestRepository, TechnicianRepository technicianRepository,
                              TestRequestProducer producer, TestTypeRepository testTypeRepository) {
        this.testRequestRepository = testRequestRepository;
        this.technicianRepository = technicianRepository;
        this.producer = producer;
        this.testTypeRepository = testTypeRepository;
    }

    public void submitTest(TestRequestDTO dto) {
        TestRequest request = mapToEntity(dto);
        if (!testTypeRepository.existsById(dto.getTestTypeId())) {
            throw new LabFlowException("Test type not found");
        }
        // hospital rules / walk-in
        if (!request.isWalkIn()) {
            int availableTech = technicianRepository.countAllByAvailable(true);
            long activeOrWaiting =
                    testRequestRepository.countByStatusIn(
                            List.of(TestStatus.RECEIVED, TestStatus.PROCESSING)
                    );

            if (availableTech < 1 && activeOrWaiting>= 20) {
                throw new QueueFullException("The hospital queue is full.");
            }
        }

        request.setStatus(TestStatus.RECEIVED);
        testRequestRepository.save(request);
        producer.sendTest(request.getId());
    }

    private TestRequest mapToEntity(TestRequestDTO dto) {
        TestRequest request = new TestRequest();
        TestType testType =
                testTypeRepository.getReferenceById(dto.getTestTypeId());
        request.setTestType(testType);
        request.setWalkIn(dto.isWalkIn());
        request.setStatus(TestStatus.RECEIVED);
        request.setReceivedAt(LocalDateTime.now());
        return request;
    }

    public TestStatusDTO getTestStatus(Long testRequestId) {
        TestRequest request = testRequestRepository.findById(testRequestId)
                .orElseThrow(() -> new LabFlowException("Test request not found with ID: " + testRequestId));

        TestStatusDTO dto = new TestStatusDTO();
        dto.setTestRequestId(request.getId());
        dto.setStatus(request.getStatus());
        dto.setReceivedAt(request.getReceivedAt());
        dto.setCompletedAt(request.getCompletedAt());
        dto.setTestName(request.getTestType().getName());
        dto.setTechnicianName(
                request.getAssignedTechnician() != null
                        ? request.getAssignedTechnician().getName()
                        : "Not assigned yet"
        );
        return dto;
    }
}

