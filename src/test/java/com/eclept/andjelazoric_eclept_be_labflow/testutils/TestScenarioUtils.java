package com.eclept.andjelazoric_eclept_be_labflow.testutils;

import com.eclept.andjelazoric_eclept_be_labflow.entity.TestRequest;
import com.eclept.andjelazoric_eclept_be_labflow.entity.TestType;
import com.eclept.andjelazoric_eclept_be_labflow.enums.TestStatus;

import java.util.ArrayList;
import java.util.List;

public class TestScenarioUtils {

    public static List<TestRequest> createHospitalQueue(TestType type, int size) {
        List<TestRequest> queue = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            TestRequest request = TestRequest.builder()
                    .testType(type)
                    .walkIn(false)
                    .status(TestStatus.RECEIVED)
                    .build();
            queue.add(request);
        }
        return queue;
    }
}
