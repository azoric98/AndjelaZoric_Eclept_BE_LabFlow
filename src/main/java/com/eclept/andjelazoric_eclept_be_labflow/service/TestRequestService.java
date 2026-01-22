package com.eclept.andjelazoric_eclept_be_labflow.service;

import com.eclept.andjelazoric_eclept_be_labflow.dto.TestRequestDTO;
import com.eclept.andjelazoric_eclept_be_labflow.dto.TestRequestResponseDTO;
import com.eclept.andjelazoric_eclept_be_labflow.dto.TestStatusDTO;
import com.eclept.andjelazoric_eclept_be_labflow.entity.TestRequest;
import com.eclept.andjelazoric_eclept_be_labflow.entity.TestType;
import com.eclept.andjelazoric_eclept_be_labflow.enums.TestStatus;
import com.eclept.andjelazoric_eclept_be_labflow.exception.LabFlowException;
import com.eclept.andjelazoric_eclept_be_labflow.exception.QueueFullException;
import com.eclept.andjelazoric_eclept_be_labflow.mapper.TestRequestMapper;
import com.eclept.andjelazoric_eclept_be_labflow.mapper.TestStatusMapper;
import com.eclept.andjelazoric_eclept_be_labflow.repository.TechnicianRepository;
import com.eclept.andjelazoric_eclept_be_labflow.repository.TestRequestRepository;
import com.eclept.andjelazoric_eclept_be_labflow.repository.TestTypeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TestRequestService {

    private final TestRequestRepository testRequestRepository;
    private final TechnicianRepository technicianRepository;
    private final TestRequestProducer producer;
    private final TestRequestMapper testRequestMapper;
    private final TestStatusMapper testStatusMapper;
    private final TestTypeRepository testTypeRepository;

    public TestRequestService(TestRequestRepository testRequestRepository, TechnicianRepository technicianRepository,
                              TestRequestProducer producer, TestRequestMapper testRequestMapper, TestStatusMapper testStatusMapper, TestTypeRepository testTypeRepository) {
        this.testRequestRepository = testRequestRepository;
        this.technicianRepository = technicianRepository;
        this.producer = producer;
        this.testRequestMapper = testRequestMapper;
        this.testStatusMapper = testStatusMapper;
        this.testTypeRepository = testTypeRepository;
    }

    public void submitTest(TestRequestDTO dto) {
        TestType testType = testTypeRepository.findById(dto.getTestTypeId())
                .orElseThrow(() -> new LabFlowException("Test type not found"));

        TestRequest request = testRequestMapper.toEntity(dto, testType);

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

    public TestStatusDTO getTestStatus(Long testRequestId) {
        TestRequest request = testRequestRepository.findById(testRequestId)
                .orElseThrow(() -> new LabFlowException("Test request not found with ID: " + testRequestId));

        return testStatusMapper.toDTO(request);
    }
    public List<TestRequestResponseDTO> findAll() {
        return testRequestRepository.findAll()
                .stream()
                .map(testRequestMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

}

