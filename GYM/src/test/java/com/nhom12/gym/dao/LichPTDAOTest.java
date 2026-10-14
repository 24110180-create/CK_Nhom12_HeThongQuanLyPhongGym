package com.nhom12.gym.dao;

import com.nhom12.gym.entity.LichPT;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LichPTDAOTest {
    private final LocalDateTime start = LocalDateTime.of(2026, 10, 15, 10, 0);

    @Test
    void validatesBusinessHoursAndAllowedDurations() {
        assertTrue(LichPTDAO.hopLe(schedule(start, 60, "HLV01", 101, LichPT.DA_DAT)));
        assertFalse(LichPTDAO.hopLe(schedule(start, 45, "HLV01", 101, LichPT.DA_DAT)));
        assertFalse(LichPTDAO.hopLe(schedule(LocalDateTime.of(2026, 10, 15, 21, 30),
                60, "HLV01", 101, LichPT.DA_DAT)));
    }

    @Test
    void rejectsOverlapsForTrainerOrMemberButAllowsAdjacentSessions() {
        LichPT existing = schedule(start, 60, "HLV01", 101, LichPT.DA_DAT);
        assertFalse(LichPTDAO.coXungDot(schedule(start.plusMinutes(30), 30, "HLV02", 102, LichPT.DA_DAT),
                Collections.singletonList(existing)));
        assertTrue(LichPTDAO.coXungDot(schedule(start.plusMinutes(30), 30, "HLV01", 102, LichPT.DA_DAT),
                Collections.singletonList(existing)));
        assertTrue(LichPTDAO.coXungDot(schedule(start.plusMinutes(30), 30, "HLV02", 101, LichPT.DA_DAT),
                Collections.singletonList(existing)));
        assertFalse(LichPTDAO.coXungDot(schedule(start.plusMinutes(60), 30, "HLV01", 102, LichPT.DA_DAT),
                Collections.singletonList(existing)));
    }

    @Test
    void cancelledSessionsDoNotBlockReservations() {
        LichPT cancelled = schedule(start, 60, "HLV01", 101, LichPT.DA_HUY);
        assertFalse(LichPTDAO.coXungDot(schedule(start.plusMinutes(30), 30, "HLV01", 102, LichPT.DA_DAT),
                Arrays.asList(cancelled)));
    }

    private LichPT schedule(LocalDateTime startsAt, int duration, String trainerCode,
                            int memberId, String status) {
        return new LichPT(memberId, "Hội viên " + memberId, "0900000000", trainerCode,
                "Huấn luyện viên " + trainerCode, "CN01", startsAt, duration, status, "", "test");
    }
}
