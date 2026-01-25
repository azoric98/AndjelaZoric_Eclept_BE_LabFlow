package com.eclept.andjelazoric_eclept_be_labflow.service.impl;

import com.eclept.andjelazoric_eclept_be_labflow.dto.common.TestStatusDTO;
import com.eclept.andjelazoric_eclept_be_labflow.dto.request.TestRequestDTO;
import com.eclept.andjelazoric_eclept_be_labflow.dto.response.TestResponseDTO;
import com.eclept.andjelazoric_eclept_be_labflow.entity.TestRequest;
import com.eclept.andjelazoric_eclept_be_labflow.entity.TestType;
import com.eclept.andjelazoric_eclept_be_labflow.enums.TestStatus;
import com.eclept.andjelazoric_eclept_be_labflow.exception.LabFlowException;
import com.eclept.andjelazoric_eclept_be_labflow.exception.QueueFullException;
import com.eclept.andjelazoric_eclept_be_labflow.mapper.TestRequestMapper;
import com.eclept.andjelazoric_eclept_be_labflow.mapper.TestStatusMapper;
import com.eclept.andjelazoric_eclept_be_labflow.queue.TestRequestProducer;
import com.eclept.andjelazoric_eclept_be_labflow.repository.TechnicianRepository;
import com.eclept.andjelazoric_eclept_be_labflow.repository.TestRequestRepository;
import com.eclept.andjelazoric_eclept_be_labflow.repository.TestTypeRepository;
import com.eclept.andjelazoric_eclept_be_labflow.service.TestRequestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class TestRequestServiceImpl implements TestRequestService {

    private final TestRequestRepository testRequestRepository;
    private final TechnicianRepository technicianRepository;
    private final TestRequestProducer producer;
    private final TestRequestMapper testRequestMapper;
    private final TestStatusMapper testStatusMapper;
    private final TestTypeRepository testTypeRepository;


    public TestRequestServiceImpl(TestRequestRepository testRequestRepository, TechnicianRepository technicianRepository,
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

            if (availableTech < 1 && activeOrWaiting >= 20) {
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

    public Page<TestResponseDTO> findAllTestRequests(Pageable pageable) {
        return testRequestRepository.findAll(pageable)
                .map(testRequestMapper::toResponseDTO);
    }

    @Override
    public TestResponseDTO save(TestResponseDTO dto) {
        try {
            TestRequest entity = new TestRequest();
            entity.setWalkIn(dto.isWalkIn());
            entity.setTestType(testTypeRepository.getReferenceById(entity.getId()));
            if (dto.getStatus() != null) {
                entity.setStatus(dto.getStatus());
            } else {
                entity.setStatus(TestStatus.RECEIVED);
            }
            TestRequest saved = testRequestRepository.save(entity);
            return testRequestMapper.toResponseDTO(saved);

        } catch (Exception e) {
            log.error("Failed to save test request {}", dto.getId(), e);
            return null;
        }
    }

    @Override
    public Optional<TestRequest> getProcessableTestRequest(Long testRequestId) {
        Optional<TestRequest> optionalRequest = testRequestRepository.findById(testRequestId);
        if (optionalRequest.isEmpty()) {
            log.warn("Test request does not exist for ID: {}", testRequestId);
        } else if (optionalRequest.get().getStatus() != TestStatus.RECEIVED) {
            log.info("Test ID {} is already processed or in progress", testRequestId);
            return Optional.empty();
        }
        return optionalRequest;
    }

    @Override
    public void setProcessingStatus(TestRequest testRequest, LocalDateTime time, TestStatus status) {
        testRequest.setStatus(TestStatus.COMPLETED);
        testRequest.setCompletedAt(time);
        testRequestRepository.save(testRequest);
    }

    @Override
    public void getFirstByStatusOrderByReceivedAt() {
        testRequestRepository.findFirstByStatusOrderByReceivedAt(TestStatus.RECEIVED)
                .ifPresent(next -> producer.sendTest(next.getId()));
    }


}

