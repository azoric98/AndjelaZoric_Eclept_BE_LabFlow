package com.eclept.andjelazoric_eclept_be_labflow.service;

import com.eclept.andjelazoric_eclept_be_labflow.dto.TestRequestDTO;
import com.eclept.andjelazoric_eclept_be_labflow.entity.Technician;
import com.eclept.andjelazoric_eclept_be_labflow.entity.TestRequest;
import com.eclept.andjelazoric_eclept_be_labflow.entity.TestStatus;
import com.eclept.andjelazoric_eclept_be_labflow.exception.LabFlowException;
import com.eclept.andjelazoric_eclept_be_labflow.exception.QueueFullException;
import com.eclept.andjelazoric_eclept_be_labflow.repository.TechnicianRepository;
import com.eclept.andjelazoric_eclept_be_labflow.repository.TestRequestRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TestRequestService {

    private final TestRequestRepository testRequestRepository;
    private final TechnicianRepository technicianRepository;
    private final TestRequestProducer producer;

    public TestRequestService(TestRequestRepository testRequestRepository, TechnicianRepository technicianRepository,
                              TestRequestProducer producer) {
        this.testRequestRepository = testRequestRepository;
        this.technicianRepository = technicianRepository;
        this.producer = producer;
    }

    public void submitTest(TestRequestDTO dto) {
        TestRequest request = mapToEntity(dto);

        // hospital rules / walk-in
        if (!request.isWalkIn()) {
            long pendingHospitalTests = testRequestRepository.countByStatus(TestStatus.RECEIVED);
            if (pendingHospitalTests >= 20) {
                throw new QueueFullException("The hospital queue is full.");
            }
        }
        Technician tech = technicianRepository.findFirstByAvailableTrue()
                .orElseThrow(() -> new LabFlowException("There are no free technicians."));
        tech.setAvailable(false);


        technicianRepository.save(tech);
        request.setAssignedTechnicianId(tech.getId());

        testRequestRepository.save(request);
        producer.sendTest(request.getId());
    }

    private TestRequest mapToEntity(TestRequestDTO dto) {
        TestRequest request = new TestRequest();
        request.setTestTypeId(dto.getTestTypeId());
        request.setWalkIn(dto.isWalkIn());
        request.setStatus(TestStatus.RECEIVED);
        request.setReceivedAt(LocalDateTime.now());
        return request;
    }
}

