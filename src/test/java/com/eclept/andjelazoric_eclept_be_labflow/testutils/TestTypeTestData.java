package com.eclept.andjelazoric_eclept_be_labflow.testutils;

import com.eclept.andjelazoric_eclept_be_labflow.entity.TestType;

public final class TestTypeTestData {

    private TestTypeTestData() {}

    public static TestType bloodTest() {
        return TestType.builder()
                .name("Blood Test")
                .processingTimeSeconds(3)
                .reagentUnits(20)
                .build();
    }

    public static TestType urineTest() {
        return TestType.builder()
                .name("Urine Test")
                .processingTimeSeconds(90)
                .reagentUnits(30)
                .build();
    }
    public static TestType pcrTest() {
        return TestType.builder()
                .name("PCR Test")
                .processingTimeSeconds(180)
                .reagentUnits(100)
                .build();
    }

    public static TestType allergyPanel() {
        return TestType.builder()
                .name("Allergy Panel")
                .processingTimeSeconds(240)
                .reagentUnits(150)
                .build();
    }

    /** Edge case – test koji skoro troši sve reagense */
    public static TestType heavyTest() {
        return TestType.builder()
                .name("Heavy Load Test")
                .processingTimeSeconds(300)
                .reagentUnits(480)
                .build();
    }
}
