package com.eclept.andjelazoric_eclept_be_labflow.testutils;

import com.eclept.andjelazoric_eclept_be_labflow.dto.request.TestRequestDTO;

public final class TestRequestTestData {

    private TestRequestTestData() {
    }

    /* ====== Hospital requests ====== */

    public static TestRequestDTO hospitalBloodTest() {
        return TestRequestDTO.builder()
                .testTypeId(1L)
                .walkIn(false)
                .build();
    }

    public static TestRequestDTO hospitalPcrTest() {
        return TestRequestDTO.builder()
                .testTypeId(3L)
                .walkIn(false)
                .build();
    }

    /* ====== Walk-in requests ====== */

    public static TestRequestDTO walkInUrineTest() {
        return TestRequestDTO.builder()
                .testTypeId(2L)
                .walkIn(true)
                .build();
    }

    public static TestRequestDTO walkInHeavyTest() {
        return TestRequestDTO.builder()
                .testTypeId(5L)
                .walkIn(true)
                .build();
    }

    /* ====== Generic builder ====== */

    public static TestRequestDTO hospitalRequest(Long testTypeId) {
        return TestRequestDTO.builder()
                .testTypeId(testTypeId)
                .walkIn(false)
                .build();
    }

    public static TestRequestDTO walkInRequest(Long testTypeId) {
        return TestRequestDTO.builder()
                .testTypeId(testTypeId)
                .walkIn(true)
                .build();
    }
}
