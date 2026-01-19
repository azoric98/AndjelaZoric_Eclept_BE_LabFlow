package com.eclept.andjelazoric_eclept_be_labflow.config;

import com.eclept.andjelazoric_eclept_be_labflow.entity.Technician;
import com.eclept.andjelazoric_eclept_be_labflow.entity.TestType;
import com.eclept.andjelazoric_eclept_be_labflow.repository.TechnicianRepository;
import com.eclept.andjelazoric_eclept_be_labflow.repository.TestTypeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    private final TestTypeRepository testTypeRepository;
    private final TechnicianRepository technicianRepository;
    private static final int INITIAL_REAGENTS = 500;


    public DataLoader(TestTypeRepository testTypeRepository,
                      TechnicianRepository technicianRepository) {
        this.testTypeRepository = testTypeRepository;
        this.technicianRepository = technicianRepository;
    }

    @Override
    public void run(String... args) {
        if (testTypeRepository.count() == 0) {
            testTypeRepository.save(new TestType(null, "Blood Test", 60, 20));
            testTypeRepository.save(new TestType(null, "Urine Test", 90, 30));
            testTypeRepository.save(new TestType(null, "PCR Test", 180, 100));
            testTypeRepository.save(new TestType(null, "Allergy Panel", 240, 150));
        }

        if (technicianRepository.count() == 0) {
            for (int i = 1; i <= 5; i++) {
                technicianRepository.save(new Technician(null,
                        "Technician " + i,
                        "Machine " + i,
                        true,
                        false,
                        INITIAL_REAGENTS));
            }
        }
    }
}
