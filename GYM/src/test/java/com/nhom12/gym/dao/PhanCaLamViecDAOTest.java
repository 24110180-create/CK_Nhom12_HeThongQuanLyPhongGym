package com.nhom12.gym.dao;

import com.nhom12.gym.entity.PhanCaLamViec;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PhanCaLamViecDAOTest {
    private final LocalDate monday = LocalDate.of(2026, 10, 5);

    @Test
    void acceptsDistinctStaffInValidWeekAndBranch() {
        assertTrue(PhanCaLamViecDAO.hopLe(monday, "CN01", Arrays.asList(
                assignment(monday, "letan1", "Lễ tân", "SANG", "CN01"),
                assignment(monday.plusDays(1), "HLV01", "HLV", "CHIEU", "CN01"))));
    }

    @Test
    void rejectsDuplicateEmployeeInSameShiftAndDay() {
        assertFalse(PhanCaLamViecDAO.hopLe(monday, "CN01", Arrays.asList(
                assignment(monday, "hlv01", "HLV", "SANG", "CN01"),
                assignment(monday, "HLV01", "HLV", "SANG", "CN01"))));
    }

    @Test
    void rejectsAssignmentsOutsideWeekOrBranch() {
        assertFalse(PhanCaLamViecDAO.hopLe(monday, "CN01",
                Collections.singletonList(assignment(monday.minusDays(1), "HLV01", "HLV", "SANG", "CN01"))));
        assertFalse(PhanCaLamViecDAO.hopLe(monday, "CN01",
                Collections.singletonList(assignment(monday, "HLV01", "HLV", "SANG", "CN02"))));
    }

    private PhanCaLamViec assignment(LocalDate date, String code, String role, String shift, String branch) {
        return new PhanCaLamViec(date, code, code, role, branch, shift);
    }
}
